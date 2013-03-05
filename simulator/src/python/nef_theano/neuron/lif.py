import theano
import numpy
from theano import tensor as TT

from neuron import Neuron

class LIFNeuron(Neuron):
    def __init__(self, size, dt=0.001, t_rc=0.02, t_ref=0.002):
        Neuron.__init__(self, size, dt)
        self.t_rc = t_rc
        self.t_ref  =t_ref
        self.voltage = theano.shared(numpy.zeros(size).astype('float32'))          # internal variables
        self.refractory_time = theano.shared(numpy.zeros(size).astype('float32'))  # internal variables
        
    # define the theano update rules that implement this neuron type    
    def update(self, input_current):        
    
        # Euler's method
        dV = self.dt / self.t_rc * (input_current - self.voltage)
        
        # increase the voltage, ignore values below 0
        v = TT.maximum(self.voltage + dV, 0)  
        
        # handle refractory period        
        post_ref = 1.0 - (self.refractory_time - self.dt) / self.dt        
        v *= TT.clip(post_ref, 0, 1) # set any post_ref elements < 0 = 0, and > 1 = 1
        
        # determine which neurons spike
        spiked = TT.switch(v > 1, 1.0, 0.0) # if v > 1 set spiked = 1, else 0
        
        # adjust refractory time (neurons that spike get a new refractory time set, all others get it reduced by dt)
        overshoot = (v - 1) / dV # linearly approximate time since neuron crossed spike threshold
        spiketime = self.dt * (1.0 - overshoot)
        new_refractory_time = TT.switch(spiked, spiketime + self.t_ref, self.refractory_time - self.dt)
        
        # return dictionary of internal variables to update (including setting a neuron that spikes to a voltage of 0)
        return { self.voltage:(v * (1 - spiked)).astype('float32'),
                 self.refractory_time:new_refractory_time.astype('float32'),
                 self.output:spiked.astype('float32') }
            
    # compute the alpha and bias needed to get the given max_rate and intercept values
    #  TODO: make this generic so it can be applied to any neuron model (by running the neurons
    #   and finding their response function), rather than this special-case implementation for LIF        
    def make_alpha_bias(self,max_rates,intercepts):
        x1=intercepts
        x2=1.0
        z1=1
        z2=1.0/(1-TT.exp((self.t_ref-(1.0/max_rates))/self.t_rc))        
        m=(z1-z2)/(x1-x2)
        b=z1-m*x1
        return m,b            
                
    # TODO: have a reset() function at the ensemble and network level that would actually call this    
    def reset(self):
        Neuron.reset(self)
        self.voltage.set_value(numpy.zeros(self.size).astype('float32'))
        self.refractory_time.set_value(numpy.zeros(self.size).astype('float32'))
                     

