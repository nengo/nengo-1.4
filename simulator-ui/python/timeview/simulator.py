from ca.nengo.model.impl import *
from ca.nengo.model import *
from ca.nengo.model.nef.impl import *
from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model.neuron.impl import *
from ca.nengo.model.neuron import *
from ca.nengo.util import *
from ca.nengo.util.impl import NodeThreadPool, NEFGPUInterface
from ca.nengo.sim.impl import LocalSimulator

from java.lang import System

class Simulator:
    def __init__(self,network):
        self.nodes=[]
        self.projections=[]
        self.tasks=[]
        self.network=network
        self.initialize(network)

        if NodeThreadPool.getNumJavaThreads() > 0:
            NEFGPUInterface.requireAllOutputsOnCPU(True);
            self.thread_pool=NodeThreadPool(network,[]);
        else:
            self.thread_pool=None;

        
    def initialize(self,network):
        for p in network.projections:
          self.projections.append(p);

        for n in network.nodes:
            if isinstance(n,Network) and not n.__class__.__name__ in ['CCMModelNetwork','PyramidalNetwork']:
                self.initialize(n)
            else:
                self.nodes.append(n)



    def reset(self,randomize=False):
        for n in self.nodes: n.reset(randomize)

        # Force java garbage collection to free (hopefully) unused memory
        System.gc()


    def step(self,start,end):
        self.network.fireStepListeners(start)
        if self.thread_pool is not None:
            self.thread_pool.step(start,end)
        else:    
            for p in self.projections:
                p.termination.setValues(p.origin.getValues())
            for n in self.nodes:
                n.run(start,end)

            #for t in self.tasks:
            #    t.run(start,end)

    def kill(self):
      if self.thread_pool is not None:
        NEFGPUInterface.requireAllOutputsOnCPU(False);
        self.thread_pool.kill()
        self.thread_pool = None
