"""
Wrapper module for simplifying the creation of Nengo models via Jython.

This system is meant to allow short, concise code to create Nengo models.  The
goal is not to add functionality to Nengo, but rather to simplify the
syntax for common use cases.

For example, we can make a communication channel like this:

    import nef
    net=nef.network('Test Network')
    input=net.make_input('input',values=[0])
    A=net.make('A',neurons=100,dimensions=1)
    B=net.make('B',neurons=100,dimensions=1)
    net.connect(input,A)
    net.connect(A,B)
    net.add_to(world)

The system will automatically create the necessary origins, terminations,
ensemble factories, and so on.

"""

from ca.nengo.model.impl import *
from ca.nengo.model import *
from ca.nengo.model.nef.impl import *
from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model.neuron.impl import *
from ca.nengo.model.neuron import *
from ca.nengo.util import *
from ca.nengo.util.impl import TimeSeriesImpl
from ca.nengo.math.impl import ConstantFunction,IndicatorPDF,AbstractFunction
import java

__all__=['Network','EnsembleArray']

import math
class FixedVectorGenerator(VectorGenerator,java.io.Serializable):
    serialVersionUID=1
    def __init__(self,vectors):
        self.vectors=[]
        for v in vectors:
            length=math.sqrt(sum([x*x for x in v]))
            self.vectors.append([x/length for x in v])
        
    def genVectors(self,number,dimensions):        
        vectors=[]
        while len(vectors)<number:
            vectors.extend(self.vectors)    
        return vectors[:number]

class FixedEvalPointGenerator(VectorGenerator,java.io.Serializable):
    serialVersionUID=1
    def __init__(self,points):
        self.points=points
        
    def genVectors(self,number,dimensions):        
        points=[]
        while len(points)<number:
            points.extend(self.points)
        return points[:number]

# keep the functions outside of the class, since they can't be serialized in the
#  current version of Jython
transientFunctions={}
class PythonFunction(AbstractFunction):
    serialVersionUID=1
    def __init__(self,func):
        AbstractFunction.__init__(self,1)
        transientFunctions[self]=func
    def map(self,x):
        if self in transientFunctions:
            return transientFunctions[self](x)        
        else:
            raise Exception('Python Functions are not kept when saving/loading networks')



class BaseNode(Node):
    serialVersionUID=1
    listeners={}   # kept outside of instances so it doesn't get serialized
    
    def __init__(self,name):
        self._origins={}
        self._terminations={}    
        self._name=name
        self._states=java.util.Properties()
        self._mode=SimulationMode.DEFAULT
        

    def reset(self,randomize=False):
        pass
    def getName(self):
        return self._name
    def setName(self,name):
        VisiblyMutableUtils.nameChanged(self, self.getName(), name, BaseNode.listeners.get(self,[]))
        self._name=name

    def addChangeListener(self,listener):
        if self not in BaseNode.listeners: BaseNode.listeners[self]=[listener]
        else: BaseNode.listeners[self].append(listener)
    def removeChangeListener(self,listener):
        if self in BaseNode.listeners:
            BaseNode.listeners[self].remove(listener)

    def run(self,start,end):
        pass

    def getOrigins(self):
        return self._origins.values()
    def getOrigin(self,name):
        return self._origins.get(name,None)

    def getTerminations(self):
        return self._terminations.values()
    def getTermination(self,name):
        return self._terminations.get(name,None)

    def getDocumentation(self):
        return ""

    def clone(self):
        raise java.lang.CloneNotSupportedException()

    def setMode(self,mode):
        self._mode=mode
    def getMode(self):
        return self._mode

    def listStates(self):
        return self._states


class BaseEnsemble(BaseNode,Ensemble):
    serialVersionUID=1
    def __init__(self,name,nodes):
        BaseNode.__init__(self,name)
        self._nodes=nodes
    def getNodes(self):
        return self._nodes
    def getSpikePattern(self):
        return None
    def collectSpikes(self,collect):
        return
    def isCollectingSpikes(self):
        return False
    def redefineNodes(nodes):
        assert False
    def reset(self,randomize=False):
        for n in self._nodes:
            n.reset(randomize)
    def run(self,start,end):
        for n in self._nodes:
            n.run(start,end)
    def setMode(self,mode):
        BaseNode.setMode(self,mode)
        for n in self._nodes:
            n.setMode(mode)        

class EnsembleArray(BaseEnsemble,Probeable):
    """Collects a set of NEFEnsembles into a single group."""
    serialVersionUID=1
    def __init__(self,name,nodes):
        """Create an ensemble array from the given nodes."""        
        BaseEnsemble.__init__(self,name,nodes)
        self._origins['X']=EnsembleOrigin(self,'X',[n.getOrigin('X') for n in nodes])
        self.dimension=len(self._nodes)
    def getDimension(self):
        return len(self._nodes)    
    def addDecodedOrigin(self,name,functions,nodeOrigin):
        """Create a new origin.  A new origin is created on each of the 
        ensembles, and these are grouped together to create an output. The 
        function must be one-dimensional."""
        if name in self._origins: raise StructuralException('Node already has an origin called "%s"'%name)
        if len(functions)!=1: raise StructuralException('Functions on EnsembleArrays must be one-dimensional')
        origins=[n.addDecodedOrigin(name,functions,nodeOrigin) for n in self._nodes]
        self._origins[name]=EnsembleOrigin(self,name,origins)
        return self._origins[name]        
    def addDecodedTermination(self,name,matrix,tauPSC,isModulatory):
        """Create a new termination.  A new termination is created on each
        of the ensembles, which are then grouped together."""
        if name in self._terminations: raise StructuralException('Node already has a termination called "%s"'%name)
        terminations=[n.addDecodedTermination(name,[matrix[i]],tauPSC,isModulatory) for i,n in enumerate(self._nodes)]
        self._terminations[name]=EnsembleTermination(self,name,terminations)
        return self._terminations[name]
        
    def listStates(self):
        return self._nodes[0].listStates()

    def getHistory(self,stateName):
        times=None
        values=[None]*len(self._nodes)
        units=[None]*len(self._nodes)
        for i,n in enumerate(self._nodes):
            data=n.getHistory(stateName)
            if i==0: times=data.getTimes()
            units[i]=data.getUnits()[0]
            values[i]=data.getValues()[0][0]
        return TimeSeriesImpl(times,[values],units)
            




class Network:
    """Wraps a Nengo network with a set of helper functions."""
    serialVersionUID=1
    def __init__(self,name):
        self.network=NetworkImpl()
        self.network.name=name
        
    def add_to(self,world):
        """Add the network to the given Nengo world object.  If there is a
        network with that name already there, remove the old one.
        """        
        n=world.getNode(self.network.name)
        if n is not None: world.remove(n)
        world.add(self.network)
        
    def add(self,node):
        """Add the node to the network.

        This is only used for manually created nodes, not FunctionInputs or
        NEFEnsembles.
        """
        self.network.addNode(node)
        return node
    
    def make_array(self,name,neurons,length,**args):
        """Create and return an array of ensembles.  Each ensemble will be
        1-dimensional.  All of the parameters from Network.make() can be
        used."""
        ensemble=EnsembleArray(name,[self.make('%d'%i,neurons,1,add_to_network=False,**args) for i in range(length)])
        self.network.addNode(ensemble)
        return ensemble
        
    def make(self,name,neurons,dimensions,
                  tau_rc=0.02,tau_ref=0.002,
                  max_rate=(200,400),intercept=(-1,1),
                  radius=1,encoders=None,
                  eval_points=None,
                  noise=None,noise_frequency=1000,
                  mode='spike',add_to_network=True):
        """Create and return an ensemble of LIF neurons.

        Keyword arguments:
        tau_rc -- membrane time constant
        tau_ref -- refractory period
        max_rate -- range for uniform selection of maximum firing rate in Hz
        intercept -- normalized range for uniform selection of tuning curve x-intercept
        radius -- representational range
        encoders -- list of encoder vectors to use (if None, uniform distribution around unit sphere)
        eval_points -- list of points within unit sphere to do optimization over
        noise -- current noise to inject, chosen uniformly from (-noise,noise)
        noise_frequency -- sampling rate (how quickly the noise changes)
        mode -- simulation mode ('direct', 'rate', or 'spike')
        """
        ef=NEFEnsembleFactoryImpl()
        ef.nodeFactory=LIFNeuronFactory(tauRC=tau_rc, tauRef=tau_ref,
                          maxRate=IndicatorPDF(max_rate[0],max_rate[1]),
                          intercept=IndicatorPDF(intercept[0],intercept[1]))
        if encoders is not None:
            ef.encoderFactory=FixedVectorGenerator(encoders)            
        if eval_points is not None:
            ef.evalPointFactory=FixedEvalPointGenerator(eval_points)            
        n=ef.make(name,neurons,dimensions)
        if noise is not None:
            for nn in n.nodes:
                nn.noise=NoiseFactory.makeRandomNoise(noise_frequency,IndicatorPDF(-noise,noise))            
        if radius!=1:
            n.radii=[radius]*dimensions
        if mode=='rate':
            n.mode=SimulationMode.RATE
        elif mode=='direct':
            n.mode=SimulationMode.DIRECT
        if add_to_network: self.network.addNode(n)
        return n
    
    def make_input(self,name,values):
        """Create and return a FunctionInput of dimensionality len(values)
        with those values as its constants.
        """
        funcs=[]
        for v in values:
            funcs.append(ConstantFunction(1,v))
        input=FunctionInput(name,funcs,Units.UNK)
        self.network.addNode(input)
        return input


    def _parse_pre(self,pre,func):
        if isinstance(pre,Origin):
            assert func==None
            return pre
        elif isinstance(pre,FunctionInput):
            assert func==None
            return pre.getOrigin('origin')
        elif isinstance(pre,NEFEnsemble) or (hasattr(pre,'getOrigin') and hasattr(pre,'addDecodedOrigin')):
            if func is not None:
                fname=func.__name__
                origin=pre.getOrigin(fname)
                if origin is None:
                    origin=pre.addDecodedOrigin(fname,[PythonFunction(func)],'AXON')
                return origin
            else:
                return pre.getOrigin('X')
        else:
            raise Exception('Unknown object to connect from')

    def compute_transform(self,dim_pre,dim_post,
                          weight=1,index_pre=None,index_post=None):
        """Create a dim_pre x dim_post matrix.  All values are either 0 or
        weight.  index_pre and index_post are used to determine which
        values are non-zero, and indicate which dimensions of the pre-synaptic
        neuron should be routed to which dimensions of the post-synaptic.

        For example, with dim_pre=2 and dim_post=3:
        index_pre=[0,1],index_post=[0,1] means "take the first two dimensions
        of pre and send them to the first two dimensions of post, giving
        [[1,0],[0,1],[0,0]]
        If an index is ommitted, the full range [0,1,2,...,N] is assumed,
        so the above example could just be index_post=[0,1]

        """
        t=[[0]*dim_pre for i in range(dim_post)]
        if index_pre is None: index_pre=range(dim_pre)
        elif isinstance(index_pre,int): index_pre=[index_pre]
        if index_post is None: index_post=range(dim_post)
        elif isinstance(index_post,int): index_post=[index_post]

        for i in range(max(len(index_pre),len(index_post))):
            pre=index_pre[i%len(index_pre)]
            post=index_post[i%len(index_post)]
            t[post][pre]=weight
        return t
        
    def connect(self,pre,post,
                transform=None,weight=1,index_pre=None,index_post=None,
                pstc=0.01,func=None,weight_func=None):
        """Connect two nodes in the network.

        pre and post can be strings giving the names of the nodes, or they
        can be the nodes themselves (FunctionInputs and NEFEnsembles are
        supported).  They can also be actual Origins or Terminations, or any
        combinaton of the above.

        pstc is the post-synaptic time constant of the new Termination

        If transform is not None, it is used as the transformation matrix for
        the new termination.  You can also use weight, index_pre, and index_post
        to define a transformation matrix instead.  weight gives the value,
        and index_pre and index_post identify which dimensions to connect (see
        compute_transform for more details).  For example,
            net.connect(A,B,weight=5)
        with both A and B as 2-dimensional ensembles, will use [[5,0],[0,5]] as
        the transform.  Also, you can do
            net.connect(A,B,index_pre=2,index_post=5)
        to connect the 3rd element in A to the 6th in B.  You can also do
            net.connect(A,B,index_pre=[0,1,2],index_post=[5,6,7])
        to connect multiple elements.

        If func is not None, a new Origin will be created on the pre-synaptic
        ensemble that will compute the provided function.  This must be an
        N->1 function at the moment (a single output value, as many
        input values as desired).

        If weight_func is not None, the connection will be made using a
        synaptic connection weight matric rather than a DecodedOrigin and
        a Decoded Termination.  The computed weight matrix will be passed
        to the provided function, which is then free to modify any values in
        that matrix, returning a new one that will actually be used.
        """

        if isinstance(pre,str):
            pre=self.network.getNode(pre)
        if isinstance(post,str):
            post=self.network.getNode(post)            

        # determine the origin and its dimensions
        origin=self._parse_pre(pre,func)
        dim_pre=origin.dimensions

        # check for the special case of being given a pre-existing termination
        if isinstance(post,Termination):
            self.network.addProjection(origin,post)
            return

        dim_post=post.dimension

        if transform is None:
            transform=self.compute_transform(dim_pre,dim_post,weight,index_pre,index_post)
        else:
            # handle 1-d transform vectors by changing to 1xN or Nx1
            if isinstance(transform[0],(int,float)):
                if dim_pre==1: transform=[[x] for x in transform]
                elif dim_post==1: transform=[transform]
                else:
                    raise Exception("Don't know how to turn %s into a %sx%s matrix"%(transform,dim_pre,dim_post))

        if weight_func is not None:
            # calculate weights and pass them to the given function
            decoder=origin.decoders
            encoder=post.encoders
            w=MU.prod(encoder,MU.transpose(decoder))   #gain is handled elsewhere
            w=weight_func(w)
            term=post.addTermination(pre.name,w,pstc,False)
            self.network.addProjection(pre.getOrigin('AXON'),term)
        else:
            term=post.addDecodedTermination(pre.name+"1",transform,pstc,False)
            self.network.addProjection(origin,term)
        

def test():
    net=Network('Test')
    m=net.compute_transform(3,3,weight=1)
    assert m==[[1,0,0],[0,1,0],[0,0,1]]
    m=net.compute_transform(2,3,weight=0.5)
    assert m==[[0.5,0],[0,0.5],[0.5,0]]
    m=net.compute_transform(2,4,index_pre=1,index_post=3)
    assert m==[[0,0],[0,0],[0,0],[0,1]]
    m=net.compute_transform(2,4,index_post=2)
    assert m==[[0,0],[0,0],[1,1],[0,0]]
    m=net.compute_transform(2,4,index_pre=0)
    assert m==[[1,0],[1,0],[1,0],[1,0]]
    m=net.compute_transform(2,4,index_post=[2,1])
    assert m==[[0,0],[0,1],[1,0],[0,0]]
    m=net.compute_transform(3,4,index_pre=[1,2],index_post=[2,1])
    assert m==[[0,0,0],[0,0,1],[0,1,0],[0,0,0]]    
    print 'tests passed'
        
