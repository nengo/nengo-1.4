import theano
import numpy
from theano import tensor as TT

from neuron import Neuron

class LIFNeuron(Neuron):
    def __init__(self,size,dt=0.001,t_rc=0.02,t_ref=0.002):
        Neuron.__init__(self,size,dt)
        self.t_rc=t_rc
        self.t_ref=t_ref
        self.voltage=theano.shared(numpy.zeros(size).astype('float32'))
        self.refractory_time=theano.shared(numpy.zeros(size).astype('float32'))
        
    def update(self,input_current):        
        dV=self.dt / self.t_rc*(input_current-self.voltage)
        v=TT.maximum(self.voltage+dV,0)                
                
        post_ref=1.0-(self.refractory_time - self.dt)/self.dt        
        v*=TT.clip(post_ref,0,1)
        
        spiked=TT.switch(v>1,1.0,0.0)
        
        overshoot=(v-1)/dV
        spiketime=self.dt*(1.0-overshoot)
        new_refractory_time=TT.switch(spiked,spiketime+self.t_ref,self.refractory_time-self.dt)
        
        
        return {self.voltage:(v*(1-spiked)).astype('float32'),
            self.refractory_time:new_refractory_time.astype('float32'),
            self.output:spiked.astype('float32')}
            
    def make_alpha_bias(self,max_rates,intercepts):
        x1=intercepts
        x2=1.0
        z1=1
        z2=1.0/(1-TT.exp((self.t_ref-(1.0/max_rates))/self.t_rc))        
        m=(z1-z2)/(x1-x2)
        b=z1-m*x1
        return m,b            
                
    
    def reset(self):
        Neuron.reset(self)
        self.voltage.set_value(numpy.zeros(self.size).astype('float32'))
        self.refractory_time.set_value(numpy.zeros(self.size).astype('float32'))
                     

