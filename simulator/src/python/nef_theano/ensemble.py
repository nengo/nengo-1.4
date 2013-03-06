from theano.tensor.shared_randomstreams import RandomStreams
from theano import tensor as TT
import theano
import numpy

import neuron
import origin

def make_encoders(neurons, dimensions, srng, encoders=None):
    """Generates a set of encoders

    :param int neurons: number of neurons 
    :param int dimensions: number of dimensions
    :param theano.tensor.shared_randomstreams snrg: theano random number generator function
    :param list encoders: set of possible preferred directions of neurons
    """
    if encoders is None: # if no encoders specified
        encoders = srng.normal((neurons, dimensions)) # generate randomly
    else:    
        encoders = numpy.array(encoders) # if encoders were specified, cast list as array
        # repeat array until 'encoders' is the same length as number of neurons in population
        encoders = numpy.tile(encoders, (neurons / len(encoders) + 1, 1))[:neurons, :dimensions]
       
    # normalize encoders across represented dimensions 
    norm = TT.sum(encoders * encoders, axis=[1], keepdims=True)
    encoders = encoders / TT.sqrt(norm)        

    return theano.function([], encoders)()

    
class Accumulator:
    def __init__(self, ensemble, pstc):
        """A collection of terminations in the same population, all sharing the same time constant        
        Stores the projected_value accumulated across these terminations, i.e. their summed contribution to the represented signal

        :param Ensemble ensemble: the ensemble this set of terminations is attached to
        :param float pstc: post-synaptic time constant on filter
        """
        self.ensemble = ensemble
        self.projected_value = theano.shared(numpy.zeros(self.ensemble.dimensions * self.ensemble.array_count).astype('float32')) # the current filtered projected_value
        self.decay = numpy.exp(-self.ensemble.neuron.dt / pstc) # time constant for filter
        self.total = None # the theano object representing the sum of the inputs to this filter

    def add(self, projected_value):
        """Add to the current set of termination inputs (with the same post-synaptic time constant pstc) an additional termination input
        self.new_projected_value is the calculation of the contribution of all of the input to each termination with pstc to the population's represented projected_value

        :param theano object projected_value: theano object representing the output of the pre population multiplied by this termination's transform matrix
        """
        if self.total is None: self.total = projected_value # initialize internal value storing voltage levels of neurons
        else: self.total = self.total + projected_value # add input current to voltage level

        self.new_projected_value = self.decay * self.projected_value + (1 - self.decay) * self.total # the theano object representing the filtering operation        
            
        
class Ensemble:
    #TODO: implement radius
    def __init__(self, neurons, dimensions, tau_ref=0.002, tau_rc=0.02, max_rate=(200,300), intercept=(-1.0,1.0), 
                                    radius=1.0, encoders=None, seed=None, neuron_type='lif', dt=0.001, array_count=1):
        """Create an population of neurons with NEF parameters on top
        
        :param int neurons: number of neurons in this population
        :param int dimensions: number of dimensions in signal these neurons represent 
        :param float tau_ref: refractory period of neurons in this population
        :param float tau_rc: RC constant 
        :param tuple max_rate: lower and upper bounds on randomly generate firing rates for neurons in this population
        :param tuple intercept: lower and upper bounds on randomly generated x offset
        :param float radius: 
        :param list encoders: set of possible preferred directions of neurons
        :param int seed: seed value for random number generator
        :param string neuron_type: type of neuron model to use, options = {'lif'}
        :param float dt: time step of neurons during update step
        :param int array_count: number of sub-populations - for network arrays
        """
        self.seed = seed
        self.neurons = neurons
        self.dimensions = dimensions
        self.array_count = array_count
        
        # create the neurons
        # TODO: handle different neuron types, which may have different parameters to pass in
        self.neuron = neuron.names[neuron_type]((array_count, self.neurons), tau_rc=tau_rc, tau_ref=tau_ref, dt=dt)
        
        # compute alpha and bias
        srng = RandomStreams(seed=seed) # set up theano random number generator
        max_rates = srng.uniform([neurons], low=max_rate[0], high=max_rate[1])  
        threshold = srng.uniform([neurons], low=intercept[0], high=intercept[1])
        alpha, self.bias = theano.function([], self.neuron.make_alpha_bias(max_rates, threshold))()
        self.bias = self.bias.astype('float32') # force to 32 bit for consistency / speed
                
        # compute encoders
        self.encoders = make_encoders(neurons, dimensions, srng, encoders=encoders)
        self.encoders = (self.encoders.T * alpha).T # combine encoders and gain for simplification
        
        # make default origin
        self.origin = dict(X=origin.Origin(self)) # creates origin with identity function

        self.accumulator = {} # dictionary of accumulators tracking terminations with different pstc values
    
    def add_filtered_input(self, projected_value, pstc):
        """Create a new termination that takes the given input (a theano object) and filters it with the given pstc
        Adds its contributions to the set of terminations with the same pstc

        :param projected_value: theano object representing the output of the pre population multiplied by this termination's transform matrix
        :param float pstc: post-synaptic time constant
        """
        if pstc not in self.accumulator: # make sure there's an accumulator for given pstc
            self.accumulator[pstc] = Accumulator(self, pstc)

        self.accumulator[pstc].add(projected_value) # add this termination's contribution to the set of terminations with the same pstc
        
    def add_origin(self, name, func):
        """Create a new origin to perform a given function over the represented signal
        
        :param string name: name of origin
        :param function func: desired transformation to perform over represented signal
        """
        self.origin[name] = origin.Origin(self, func)    
    
    def update(self):
        """Compute the set of theano updates needed for this ensemble
        Returns dictionary with new neuron state, termination, and origin values
        """
        input_current = numpy.tile(self.bias, (self.array_count, 1)) # apply respective biases to neurons in the population 
        
        # increase the input_current by (accumulated current x encoders)
        if len(self.accumulator) > 0: # if there is at least one termination on this population
            X = sum(a.new_projected_value for a in self.accumulator.projected_values()) # sum of input across each termination for each represented dimension
            X = X.reshape((self.array_count, self.dimensions)) # reshape for network arrays
            input_current += TT.dot(X,self.encoders.T) # calculate input_current for each neuron as represented input signal x preferred direction
        
        # pass that total into the neuron model to produce the main theano computation
        updates = self.neuron.update(input_current) # updates is dictionary of theano internal variables to update
        
        # also update the filter projected_values in the accumulators
        for a in self.accumulator.projected_values():            
            updates[a.projected_value] = a.new_projected_value.astype('float32') # add accumulated termination inputs to theano internal variable updates
            
        # and compute the decoded origin projected_values from the neuron output
        for o in self.origin.projected_values():
            # in the dictionary updates, set each origin's output projected_values equal to the self.neuron.output() we just calculated
            updates.update(o.update(updates[self.neuron.output]))
        
        return updates    
