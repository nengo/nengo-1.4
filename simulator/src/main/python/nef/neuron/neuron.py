import theano
import numpy
from theano import tensor as TT

class Neuron:
    def __init__(self,size,dt):
        self.size=size
        self.dt=dt
        self.output=theano.shared(numpy.zeros(size).astype('float32'))
    def reset(self):
        self.output.set_value(numpy.zeros(self.size).astype('float32'))
            
            
# take a neuron model, run it for the given amount of time with a fixed input, and return the accumulated output over that time            
def accumulate(input,neuron,time=1.0,init_time=0.05):
    total=theano.shared(numpy.zeros(neuron.size).astype('float32'))
    
    # make the standard neuron update function
    updates=neuron.update(input.astype('float32'))
    tick=theano.function([],[],updates=updates)
    
    # makea  variant that also includes computing the total output
    updates[total]=total+neuron.output
    accumulate=theano.function([],[],updates=updates)
    
    tick.fn(n_calls=int(init_time/neuron.dt))    # call the standard one a few times to get some startup transients out of the way
    accumulate.fn(n_calls=int(time/neuron.dt))   # call the accumulator version a bunch of times
    return total.get_value().astype('float32')/time
    
    
    
    
        
                        

