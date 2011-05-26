from ca.nengo.model.impl import *
from ca.nengo.model import *
from ca.nengo.model.nef.impl import *
from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model.neuron.impl import *
from ca.nengo.model.neuron import *
from ca.nengo.util import *
from ca.nengo.util.impl import NodeThreadPool, NEFGPUInterface
from ca.nengo.sim.impl import LocalSimulator

class Simulator:
    def __init__(self,network):
        self.nodes=[]
        self.projections=[]
        self.network=network
        self.initialize(network)

        
        if NEFGPUInterface.getUseGPU():
            gpuNodes = LocalSimulator.collectNodes(self.nodes) 
            gpuNetworkArrays = LocalSimulator.collectNetworkArraysForGPU(self.nodes) 
            gpuProjections = LocalSimulator.collectProjections(self.nodes, self.projections) 

            self.thread_pool=NEFGPUInterface(gpuNodes, gpuProjections, gpuNetworkArrays)

        elif NodeThreadPool.isMultithreading():
            multithread_nodes = LocalSimulator.collectNodes(self.nodes) 
            multithread_projs = LocalSimulator.collectProjections(self.nodes, self.projections) 

            self.thread_pool=NodeThreadPool(multithread_nodes, multithread_projs)
        else:    
            self.thread_pool=None

    def initialize(self,network):
        
        for p in network.projections:
          self.projections.append(p);

        for n in network.nodes:
            if n.__class__.__name__=='NetworkArray':
              self.nodes.append(n)
            elif isinstance(n,Network) and not n.__class__.__name__ in ['CCMModelNetwork','PyramidalNetwork']:
                self.initialize(n)
            else:
                self.nodes.append(n)

    def reset(self,randomize=False):
        for n in self.nodes: n.reset(randomize)

    def step(self,start,end):
        if self.thread_pool is not None:
            # the thread pool should take care of the projections
            self.thread_pool.step(start,end)
        else:    
            for p in self.projections:
              p.termination.setValues(p.origin.getValues())
            for n in self.nodes:
                n.run(start,end)    

    def kill(self):
      if self.thread_pool is not None:
        self.thread_pool.kill()
        self.thread_pool = None
