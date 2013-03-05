import theano
import numpy
from theano import tensor as TT

from neuron import Neuron

# an example of implementing a rate-mode neuron

class LIFRateNeuron(Neuron):
    def __init__(self, size, dt=0.001, t_rc=0.02, t_ref=0.002):
        Neuron.__init__(self, size, dt)
        self.t_rc = t_rc
        self.t_ref = t_ref
        
    # define the theano update rules that implement this neuron type    
    def update(self, input_current):        
        # set up denominator of LIF firing rate equation
        rate = self.t_ref - self.t_rc * TT.log(1 - 1.0 / TT.maximum(input_current, 0)) 
        # if input current is enough to make neuron spike, calculate firing rate, else return 0
        rate = TT.switch(input_current > 1, 1 / rate, 0) 
        
        # return dictionary of internal variables to update 
        return {self.output:TT.unbroadcast(rate.astype('float32'), 0)}
                
    # compute the alpha and bias needed to get the given max_rate and intercept values
    def make_alpha_bias(self, max_rates, intercepts):
        x1 = intercepts
        x2 = 1.0
        z1 = 1
        z2 = 1.0 / (1 - TT.exp((self.t_ref - (1.0 / max_rates)) / self.t_rc))        
        m = (z1 - z2) / (x1 - x2)
        b = z1 - m * x1
        return m, b                                                 

