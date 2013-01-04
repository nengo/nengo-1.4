from theano import tensor as TT
import theano
import numpy

class Input:
    def __init__(self,name,value,zero_after=None):
        self.name=name
        self.t=0
        self.function=None
        self.zero_after=zero_after
        self.zeroed=False
        
        if callable(value):
            v=value(0.0)
            self.value=theano.shared(numpy.array(v).astype('float32'))
            self.function=value
        else:
            self.value=theano.shared(numpy.array(value).astype('float32'))    
    def tick(self):
        if self.zeroed: return
        if self.zero_after is not None and self.t>self.zero_after:
            self.value.set_value(numpy.zeros_like(self.value.get_value()))
            self.zeroed=True
        if self.function is not None:
            self.value.set_value(self.function(self.t))
    def reset(self):
        self.zeroed=False
                
        
        
