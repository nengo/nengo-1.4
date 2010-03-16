
from ca.nengo.model.impl import *
from ca.nengo.model import *
from ca.nengo.model.nef.impl import *
from ca.nengo.model.neuron.impl import *
from ca.nengo.model.neuron import *
from ca.nengo.util import *
from ca.nengo.math.impl import ConstantFunction,IndicatorPDF,AbstractFunction

class FixedVectorGenerator(VectorGenerator):
    serialVersionUID=1
    def __init__(self,basis):
        self.basis=basis
        
    def genVectors(self,number,dimensions):        
        vectors=[]
        while len(vectors)<number:
            vectors.extend(self.basis)    
        return vectors


class PythonFunction(AbstractFunction):
    serialVersionUID=1    
    def __init__(self,func):
        AbstractFunction.__init__(self,1)
        self.func=func
    def map(self,x):
        return self.func(x)


class Network:
    def __init__(self,name):
        self.network=NetworkImpl()
        self.network.name=name
    def add_to(self,world):
        n=world.getNode(self.network.name)
        if n is not None: world.remove(n)
        world.add(self.network)
        
    def make(self,name,neurons,dimensions,tau_rc=0.02,tau_ref=0.002,max_rate=(200,400),intercept=(-1,1),radius=1,basis=None,noise=None,noise_frequency=1000):
        ef=NEFEnsembleFactoryImpl()
        ef.nodeFactory=LIFNeuronFactory(tauRC=tau_rc, tauRef=tau_ref,
                                maxRate=IndicatorPDF(max_rate[0],max_rate[1]),
                                intercept=IndicatorPDF(intercept[0],intercept[1]))
        if basis is not None:
            ef.encoderFactory=FixedVectorGenerator(basis)            
        n=ef.make(name,neurons,dimensions)
        if noise is not None:
            for nn in n.nodes:
                nn.noise=NoiseFactory.makeRandomNoise(noise_frequency,IndicatorPDF(-noise,noise))            
        if radius!=1:
            n.radii=[radius]*dimensions
        self.network.addNode(n)
        return n
    
    def make_input(self,name,values):
        funcs=[]
        for v in values:
            funcs.append(ConstantFunction(1,v))
        input=FunctionInput(name,funcs,Units.UNK)
        self.network.addNode(input)
        return input
        
    def connect(self,pre,post,transform=None,index_pre=None,index_post=None,pstc=0.01,func=None):
        if type(pre) is str: pre=self.network.getNode(pre)
        if type(post) is str: post=self.network.getNode(post)
        
        if isinstance(pre,FunctionInput):
            origin=pre.getOrigin('origin')
            pre_d=len(pre.functions)
        else:    
            origin=pre.getOrigin('X')
            pre_d=pre.dimension
        post_d=post.dimension    
        
        if func is not None:
            fname='%s_%s'%(func.__name__,post.name)
            origin=pre.addDecodedOrigin(fname,[PythonFunction(func)],'AXON')
            pre_d=1
        
        if transform is None:
            if index_pre is not None:
                transform=[0]*pre_d
                transform[index_pre]=1
            elif index_post is not None:
                transform=[[0] for i in range(post_d)]
                transform[index_post][0]=1
            else:
                transform=1
        if isinstance(transform,(int,float)):
            v=transform
            transform=[[0]*pre_d for i in range(post_d)]
            for i in range(min(pre_d,post_d)):
                transform[i][i]=v
        if isinstance(transform[0],(int,float)):
            transform=[transform]        
        
        term=post.addDecodedTermination(pre.name,transform,pstc,False)
        self.network.addProjection(origin,term)
        
