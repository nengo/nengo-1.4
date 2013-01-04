import theano
import numpy
from theano import tensor as TT

from neuron import Neuron

# an example of implementing a rate-mode neuron

class LIFRateNeuron(Neuron):
    def __init__(self,size,dt=0.001,t_rc=0.02,t_ref=0.002):
        Neuron.__init__(self,size,dt)
        self.t_rc=t_rc
        self.t_ref=t_ref
        
    def update(self,input_current):        
        rate=self.t_ref-self.t_rc*TT.log(1-1.0/TT.maximum(input_current,0))
        rate=TT.switch(input_current>1,1/rate,0)
        return {self.output:TT.unbroadcast(rate.astype('float32'),0)}
                
    def make_alpha_bias(self,max_rates,intercepts):
        x1=intercepts
        x2=1.0
        z1=1
        z2=1.0/(1-TT.exp((self.t_ref-(1.0/max_rates))/self.t_rc))        
        m=(z1-z2)/(x1-x2)
        b=z1-m*x1
        return m,b                                                 

