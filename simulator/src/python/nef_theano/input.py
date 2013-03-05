from theano import tensor as TT
import theano
import numpy

class Input:
    def __init__(self, name, value, zero_after=None):
        """A function input object

        :param string name: name of the function input
        :param value: defines the output values
        :type value: float or function
        :param float zero_after: time after which to set function output = 0 (s)
        """
        self.name = name
        self.t = 0
        self.function = None
        self.zero_after = zero_after
        self.zeroed = False
        
        if callable(value): # if value parameter is a python function
            v = value(0.0) # initial output value = function value with input 0.0                                   ????????????????
            self.value = theano.shared(numpy.array(v).astype('float32')) # theano internal state defining output value
            self.function = value
        else: # if value parameter is a float
            self.value = theano.shared(numpy.array(value).astype('float32')) # theano internal state defining output value 

        """ rewrite of above - test and implement
        if callable(value): # if value parameter is a python function
            self.function = value
            value = self.function(0.0) # initial output value = function value with input 0.0                                   ????????????????
        self.value = theano.shared(numpy.array(v).astype('float32')) # theano internal state defining output value
        """
       

    def reset(self):
        """Resets the function output state values
        """
        self.zeroed = False

    def tick(self):
        """Move function input forward in time
        """
        if self.zeroed: return

        if self.zero_after is not None and self.t > self.zero_after: # zero output
            self.value.set_value(numpy.zeros_like(self.value.get_value()))
            self.zeroed=True

        if self.function is not None: # update output value 
            self.value.set_value(self.function(self.t))
