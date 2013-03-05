import ensemble
import input

from theano import tensor as TT
import theano
import numpy
import random

class Network:
    def __init__(self,name,seed=None):
        self.name=name
        self.dt=0.001
        self.seed=seed        
        self.node={}            # all the nodes in the network, indexed by name
        self.theano_tick=None   # the function to call to run the theano protions of the model ahead one timestep
        self.tick_nodes=[]      # the list of nodes who have non-theano code that must be run each timestep
        if seed is not None:
            self.random=random.Random()
            self.random.seed(seed)
            
    # make an ensemble,  Note that all ensembles are actually arrays of length 1        
    def make(self,name,neurons,dimensions,array_count=1,intercept=(-1,1),seed=None,type='lif',encoders=None):
        if seed is None:
            if self.seed is not None: 
                seed=self.random.randrange(0x7fffffff)
    
        self.theano_tick=None  # just in case the model has been run previously, as adding a new node means we have to rebuild the theano function
        e=ensemble.Ensemble(neurons,dimensions,count=array_count,intercept=intercept,dt=self.dt,seed=seed,type=type,encoders=encoders)        
        self.node[name]=e
    def make_array(self,name,neurons,count,dimensions=1,**args):
        return self.make(name=name,neurons=neurons,dimensions=dimensions,array_count=count,**args)
    
    # create an input
    def make_input(self,name,value,zero_after=None):
        self.add(input.Input(name,value,zero_after=zero_after))
            
    # add an arbitrary non-theano node (used for Input now, should be used for SimpleNodes when those get implemented
    def add(self,node):
        self.tick_nodes.append(node)        
        self.node[node.name]=node
    
        
    def connect(self,pre,post,transform=None,pstc=0.01,func=None,origin_name=None):
        self.theano_tick=None  # just in case the model has been run previously, as adding a new node means we have to rebuild the theano function
                        
        pre=self.node[pre]
        post=self.node[post]
        if hasattr(pre,'value'):   # used for Input objects now, could also be used for SimpleNode origins when they are written
            assert func is None
            value=pre.value
        else:  # this should only be used for ensembles (maybe reorganize this if statement to check if it is an ensemble?)          
            if func is not None:
                if origin_name is None: origin_name=func.__name__   #TODO: better analysis to see if we need to build a new origin (rather than just relying on the name)
                if origin_name not in pre.origin:
                    pre.add_origin(origin_name,func)                
                value=pre.origin[origin_name].value
            else:                     
                value=pre.origin['X'].value
        if transform is not None: value=TT.dot(value,transform)
        post.add_filtered_input(value, pstc)

    def make_tick(self):
        updates={}
        for e in self.node.values():
            if hasattr(e,'update'):
                updates.update(e.update())    
        return theano.function([],[],updates=updates)
        
    run_time=0.0    
    def run(self,time):
        if self.theano_tick is None: self.theano_tick=self.make_tick()
        for i in range(int(time/self.dt)):
            t=self.run_time+i*self.dt
            for node in self.tick_nodes:    # run the non-theano nodes
                node.t=t
                node.tick()
            self.theano_tick()               # run the theano nodes
        self.run_time+=time   
            
        
       

