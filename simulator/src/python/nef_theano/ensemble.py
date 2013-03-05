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
    encoders=encoders/TT.sqrt(norm)        

    return theano.function([],encoders)()

    
class Accumulator:
    def __init__(self, ensemble, t_psc):
        """A collection of terminations in the same population, all sharing the same time constant        
        Stores the value accumulated across these terminations, i.e. their summed contribution to the represented signal

        :param Ensemble ensemble: the ensemble this set of terminations is attached to
        :param float t_psc: post-synaptic time constant on filter
        """
        self.ensemble = ensemble
        self.value = theano.shared(numpy.zeros(self.ensemble.dimensions * self.ensemble.count).astype('float32')) # the current filtered value
        self.decay = numpy.exp(-self.ensemble.neuron.dt / t_psc) # time constant for filter
        self.total = None # the theano object representing the sum of the inputs to this filter

    def add(self, new_termination_input):
        """Add to the current set of termination inputs (with the same post-synaptic time constant t_psc) an additional termination input
        self.new_value is the calculation of the contribution of all of the input to each termination with t_psc to the population's represented value

        :param theano object new_termination_input: theano object representing the output of the pre population multiplied by this termination's transform matrix
        """
        if self.total is None: self.total = new_termination_input # initialize internal value storing voltage levels of neurons
        else: self.total = self.total + new_termination_input # add input current to voltage level

        self.new_value = self.decay * self.value + (1 - self.decay) * self.total # the theano object representing the filtering operation        
            
        
class Ensemble:
    def __init__(self, neurons, dimensions, count=1, max_rate=(200,300), intercept=(-1.0,1.0), 
                            t_ref=0.002, t_rc=0.02, seed=None, neuron_type='lif', dt=0.001, encoders=None):
        """Create an population of neurons with NEF parameters on top
        
        :param int neurons: number of neurons in this population
        :param int dimensions: number of dimensions in signal these neurons represent 
        :param int count: number of sub-populations - for network arrays
        :param tuple max_rate: lower and upper bounds on randomly generate firing rates for neurons in this population
        :param tuple intercept: lower and upper bounds on randomly generated x offset
        :param float t_ref: refractory period of neurons in this population
        :param float t_rc: RC constant 
        :param int seed: seed value for random number generator
        :param string neuron_type: type of neuron model to use, options = {'lif'}
        :param float dt: time step of neurons during update step
        :param list encoders: set of possible preferred directions of neurons
        """
        self.seed = seed
        self.neurons = neurons
        self.dimensions = dimensions
        self.count = count
        
        # create the neurons
        # TODO: handle different neuron types, which may have different parameters to pass in
        self.neuron = neuron.names[neuron_type]((count, self.neurons), t_rc=t_rc, t_ref=t_ref, dt=dt)
        
        # compute alpha and bias
        srng = RandomStreams(seed=seed) # set up theano random number generator
        max_rates = srng.uniform([neurons], low=max_rate[0], high=max_rate[1])  
        threshold = srng.uniform([neurons], low=intercept[0], high=intercept[1])
        alpha, self.bias = theano.function([], self.neuron.make_alpha_bias(max_rates, threshold))()
        self.bias = self.bias.astype('float32') # cast biases to float values - why?                    ????????????????
                
        # compute encoders
        self.encoders = make_encoders(neurons, dimensions, srng, encoders=encoders)
        self.encoders = (self.encoders.T * alpha).T # combine encoders and gain for simplification
        
        # make default origin
        self.origin = dict(X=origin.Origin(self)) # creates origin with identity function

        self.accumulator = {} # dictionary of accumulators tracking terminations with different t_psc values
    
    def add_filtered_input(self, input, t_psc):
        """Create a new termination that takes the given input (a theano object) and filters it with the given t_psc
        Adds its contributions to the set of terminations with the same t_psc

        :param theano object input: theano object representing the output of the pre population multiplied by this termination's transform matrix
        :param float t_psc: post-synaptic time constant
        """
        if t_psc not in self.accumulator: # make sure there's an accumulator for given t_psc
            self.accumulator[t_psc] = Accumulator(self, t_psc)

        self.accumulator[t_psc].add(input) # add this termination's contribution to the set of terminations with the same t_psc
        
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
        input_current = numpy.tile(self.bias, (self.count, 1)) # apply respective biases to neurons in the population 
        
        # increase the input_current by (accumulated current x encoders)
        if len(self.accumulator) > 0: # if there is at least one termination on this population
            X = sum(a.new_value for a in self.accumulator.values()) # sum of input across each termination for each represented dimension
            X = X.reshape((self.count, self.dimensions)) # reshape for network arrays
            input_current += TT.dot(X,self.encoders.T) # calculate input_current for each neuron as represented input signal x preferred direction
        
        # pass that total into the neuron model to produce the main theano computation
        updates = self.neuron.update(input_current) # updates is dictionary of theano internal variables to update
        
        # also update the filter values in the accumulators
        for a in self.accumulator.values():            
            updates[a.value] = a.new_value.astype('float32') # add accumulated termination inputs to theano internal variable updates
            
        # and compute the decoded origin values from the neuron output
        for o in self.origin.values():
            # in the dictionary updates, set each origin's output values equal to the self.neuron.output() we just calculated
            updates.update(o.update(updates[self.neuron.output]))
        
        return updates    
