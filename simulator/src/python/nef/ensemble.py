from theano.tensor.shared_randomstreams import RandomStreams
from theano import tensor as TT
import theano
import numpy

import neuron
import origin

# generates a set of encoders
def make_encoders(neurons,dimensions,srng,encoders=None):
    if encoders is None:
        encoders=srng.normal((neurons,dimensions))
    else:    
        encoders=numpy.array(encoders)
        encoders=numpy.tile(encoders,(neurons/len(encoders)+1,1))[:neurons,:dimensions]
        
    norm=TT.sum(encoders*encoders,axis=[1],keepdims=True)
    encoders=encoders/TT.sqrt(norm)        
    return theano.function([],encoders)()

    
# a collection of terminations, all sharing the same time constant        
class Accumulator:
    def __init__(self,ensemble,tau):
        self.ensemble=ensemble   # the ensemble this set of terminations is attached to
        self.value=theano.shared(numpy.zeros(self.ensemble.dimensions*self.ensemble.count).astype('float32'))  # the current filtered value
        self.decay=numpy.exp(-self.ensemble.neuron.dt/tau)   # time constant for filter
        self.total=None   # the theano object representing the sum of the inputs to this filter
    def add(self,input):
        if self.total is None: self.total=input
        else: self.total=self.total+input
        self.new_value=self.decay*self.value+(1-self.decay)*self.total   # the theano object representing the filtering operation        
            
        
class Ensemble:
    def __init__(self,neurons,dimensions,count=1,max_rate=(200,300),intercept=(-1.0,1.0),t_ref=0.002,t_rc=0.02,seed=None,type='lif',dt=0.001,encoders=None):
        self.seed=seed
        self.neurons=neurons
        self.dimensions=dimensions
        self.count=count
        
        # create the neurons
        # TODO: handle different neuron types, which may have different parameters to pass in
        self.neuron=neuron.names[type]((count,self.neurons),t_rc=t_rc,t_ref=t_ref,dt=dt)
        
        # compute alpha and bias
        srng = RandomStreams(seed=seed)
        max_rates=srng.uniform([neurons],low=max_rate[0],high=max_rate[1])
        threshold=srng.uniform([neurons],low=intercept[0],high=intercept[1])
        alpha,self.bias=theano.function([],self.neuron.make_alpha_bias(max_rates,threshold))()
        self.bias=self.bias.astype('float32')
                
        # compute encoders
        self.encoders=make_encoders(neurons,dimensions,srng,encoders=encoders)
        self.encoders=(self.encoders.T*alpha).T
        
        # make default origin
        self.origin=dict(X=origin.Origin(self))
        self.accumulator={}
    
    # create a new origin that computes a given function
    def add_origin(self,name,func):
        self.origin[name]=origin.Origin(self,func)    
    
    # create a new termination that takes the given input (a theano object) and filters it with the given tau
    def add_filtered_input(self,input,tau):
        if tau not in self.accumulator:  # make sure there's an accumulator for that tau
            self.accumulator[tau]=Accumulator(self,tau)
        self.accumulator[tau].add(input)
        
    # compute the set of theano updates needed for this ensemble        
    def update(self):
        input=numpy.tile(self.bias,(self.count,1))      # apply the bias to all neurons in the array
        
        # increase the input by the total of all the accumulators times the encoders
        if len(self.accumulator)>0:
            X=sum(a.new_value for a in self.accumulator.values())
            X=X.reshape((self.count,self.dimensions))
            input+=TT.dot(X,self.encoders.T)
        
        # pass that total into the neuron model to produce the main theano computation
        updates=self.neuron.update(input)
        
        # also update the filter values in the accumulators
        for a in self.accumulator.values():            
            updates[a.value]=a.new_value.astype('float32')
            
        # and compute the decoded origin values from the neuron output
        for o in self.origin.values():
            updates.update(o.update(updates[self.neuron.output]))
        return updates    
        
        
