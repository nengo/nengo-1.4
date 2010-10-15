from ca.nengo.model.impl import *
from ca.nengo.model import *
from ca.nengo.model.nef.impl import *
from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model.neuron.impl import *
from ca.nengo.model.neuron import *
from ca.nengo.util import *
from ca.nengo.util.impl import NodeThreadPool, GPUNodeThreadPool

class Simulator:
    def __init__(self,network):
        self.nodes=[]
        self.network=network
        self.initialize(network)
        
        if GPUNodeThreadPool.getUseGPU():
            for n in self.nodes: 
                if isinstance(n,NEFEnsemble) and n.mode==SimulationMode.DEFAULT: 
                    n.setGPU(True)
            self.thread_pool=GPUNodeThreadPool(self.nodes)
        elif NodeThreadPool.isMultithreading():
            self.thread_pool=NodeThreadPool(self.nodes)
        else:    
            self.thread_pool=None
    def initialize(self,network):
        for n in network.nodes:
            if n.__class__.__name__=='NetworkImpl':
            #if isinstance(n,Network) and not n.__class__.__name__ in ['CCMModelNetwork','PyramidalNetwork']:
                self.initialize(n)
            else:
                self.nodes.append(n)
    def reset(self,randomize=False):
        for n in self.nodes: n.reset(randomize)
    def step(self,start,end):
        for p in self.network.projections:
            p.termination.setValues(p.origin.getValues())
        if self.thread_pool is not None:
            self.thread_pool.step(start,end)
        else:    
            for n in self.nodes:
                n.run(start,end)    

