from theano.tensor.shared_randomstreams import RandomStreams
from theano import tensor as TT
import theano
import numpy

import neuron
import origin

def make_encoders(neurons,dimensions,srng):
    encoders=srng.normal((neurons,dimensions))
    norm=TT.sum(encoders*encoders,axis=[1],keepdims=True)
    encoders=encoders/TT.sqrt(norm)        
    return theano.function([],encoders)()

    
        
class Accumulator:
    def __init__(self,ensemble,tau):
        self.ensemble=ensemble
        self.value=theano.shared(numpy.zeros(self.ensemble.dimensions*self.ensemble.count).astype('float32'))
        self.decay=numpy.exp(-self.ensemble.neuron.dt/tau)
        self.total=None
    def add(self,input):
        if self.total is None: self.total=input
        else: self.total=self.total+input
        self.new_value=self.decay*self.value+(1-self.decay)*self.total        
            
        
class Ensemble:
    def __init__(self,neurons,dimensions,count=1,max_rate=(200,300),intercept=(-1.0,1.0),t_ref=0.002,t_rc=0.02,seed=None,type='lif',dt=0.001):
        self.seed=seed
        self.neurons=neurons
        self.dimensions=dimensions
        self.count=count
        
        
        self.neuron=neuron.names[type]((count,self.neurons),t_rc=t_rc,t_ref=t_ref,dt=dt)
        
        srng = RandomStreams(seed=seed)

        max_rates=srng.uniform([neurons],low=max_rate[0],high=max_rate[1])
        threshold=srng.uniform([neurons],low=intercept[0],high=intercept[1])
        alpha,self.bias=theano.function([],self.neuron.make_alpha_bias(max_rates,threshold))()
        self.bias=self.bias.astype('float32')
                
        self.encoders=make_encoders(neurons,dimensions,srng)
        self.encoders=(self.encoders.T*alpha).T
        
        self.origin=dict(X=origin.Origin(self))
        self.accumulator={}
    
    def add_origin(self,name,func):
        self.origin[name]=origin.Origin(self,func)    
    
    def add_filtered_input(self,input,tau):
        if tau not in self.accumulator:
            self.accumulator[tau]=Accumulator(self,tau)
        self.accumulator[tau].add(input)
        
            
    def update(self):
        input=numpy.tile(self.bias,(self.count,1))
        if len(self.accumulator)>0:
            X=sum(a.new_value for a in self.accumulator.values())
            X=X.reshape((self.count,self.dimensions))
            input+=TT.dot(X,self.encoders.T)
        updates=self.neuron.update(input)
        for a in self.accumulator.values():            
            updates[a.value]=a.new_value.astype('float32')
        for o in self.origin.values():
            updates.update(o.update(updates[self.neuron.output]))
        return updates    
        
        
