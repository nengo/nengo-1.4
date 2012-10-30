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
            
def accumulate(input,neuron,time=1.0,init_time=0.05):
    total=theano.shared(numpy.zeros(neuron.size).astype('float32'))
    
    updates=neuron.update(input.astype('float32'))
    tick=theano.function([],[],updates=updates)
    
    updates[total]=total+neuron.output
    accumulate=theano.function([],[],updates=updates)
    
    tick.fn(n_calls=int(init_time/neuron.dt))    
    accumulate.fn(n_calls=int(time/neuron.dt))
    return total.get_value().astype('float32')/time
    
    
    
    
        
                        

