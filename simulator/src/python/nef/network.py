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
        self.node={}
        self.tick=None
        self.tick_nodes=[]
        if seed is not None:
            self.random=random.Random()
            self.random.seed(seed)
            
    def add(self,node):
        self.tick_nodes.append(node)        
        self.node[node.name]=node
    
    def make(self,name,neurons,dimensions,array_count=1,intercept=(-1,1),seed=None,type='lif'):
        if seed is None:
            if self.seed is not None: 
                seed=self.random.randrange(0x7fffffff)
    
        self.tick=None  # just in case the model has been run previously
        e=ensemble.Ensemble(neurons,dimensions,count=array_count,intercept=intercept,dt=self.dt,seed=seed,type=type)        
        self.node[name]=e
    def make_array(self,name,neurons,count,dimensions=1,**args):
        return self.make(name=name,neurons=neurons,dimensions=dimensions,array_count=count,**args)
    
    def make_input(self,name,value,zero_after=None):
        self.add(input.Input(name,value,zero_after=zero_after))
            
        
    def connect(self,pre,post,transform=None,pstc=0.01,func=None,origin_name=None):
        self.tick=None  # just in case the model has been run previously
                        
        pre=self.node[pre]
        post=self.node[post]
        if hasattr(pre,'value'):
            assert func is None
            value=pre.value
        else:           
            if func is not None:
                if origin_name is None: origin_name=func.__name__
                if origin_name not in pre.origin:
                    pre.add_origin(origin_name,func)                
                value=pre.origin[origin_name].value
            else:                     
                value=pre.origin['X'].value
        if transform is not None: value=TT.dot(value,transform)
        post.add_filtered_input(value,pstc)

    def make_tick(self):
        updates={}
        for e in self.node.values():
            if hasattr(e,'update'):
                updates.update(e.update())    
        return theano.function([],[],updates=updates)
        
    run_time=0.0    
    def run(self,time):
        if self.tick is None: self.tick=self.make_tick()
        for i in range(int(time/self.dt)):
            t=self.run_time+i*self.dt
            for node in self.tick_nodes: 
                node.t=t
                node.tick()
            self.tick()
        self.run_time+=time   
            
        
       

