import theano
import numpy
from theano import tensor as TT

from neuron import Neuron

# an example of implementing a rate-mode neuron

class LIFRateNeuron(Neuron):
    def __init__(self, size, dt=0.001, t_rc=0.02, t_ref=0.002):
        '''
        Constructor for a set of LIF rate neuron
        
        Parameters
        ----------
        size: int, number of neurons in set
        dt: float, timestep for neuron update function
        t_rc: float, the RC time constant 
        t_ref: float, refractory period length (s)
        '''
        Neuron.__init__(self, size, dt)
        self.t_rc = t_rc
        self.t_ref = t_ref
        
    def make_alpha_bias(self, max_rates, intercepts):
        '''
        Compute the alpha and bias needed to get the given max_rate and intercept values
        Returns: gain (alpha) and offset (j_bias) values of neurons
        
        Parameters
        ----------
        max_rates: float array, maximum firing rates of neurons
        intercepts: float array, x-intercepts of neurons
        '''
        x1 = intercepts; 
        x2 = 1.0
        z1 = 1
        z2 = 1.0 / (1 - TT.exp((self.t_ref - (1.0 / max_rates)) / self.t_rc))        
        m = (z1 - z2) / (x1 - x2) # calculate alpha
        b = z1 - m * x1 # calculate j_bias
        return m, b                                                 

    def update(self, input_current):        
        '''
        Theano update rule that implementing LIF rate neuron type    
        Returns: dictionary with firing rate for current time step 
    
        Parameters
        ----------
        input_current: float array, the input current for the current time step
        '''
        # set up denominator of LIF firing rate equation
        rate = self.t_ref - self.t_rc * TT.log(1 - 1.0 / TT.maximum(input_current, 0)) 
        # if input current is enough to make neuron spike, calculate firing rate, else return 0
        rate = TT.switch(input_current > 1, 1 / rate, 0) 
        
        # return dictionary of internal variables to update 
        return {self.output:TT.unbroadcast(rate.astype('float32'), 0)}
