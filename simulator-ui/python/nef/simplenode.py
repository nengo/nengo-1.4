from ca.nengo.model import Termination, Origin, Probeable, Node, SimulationMode, Units
from ca.nengo.model.impl import BasicOrigin, RealOutputImpl
from ca.nengo.util import VisiblyMutableUtils
import java
import inspect
import warnings
import math

class SimpleTermination(Termination):
    def __init__(self,name,node,func,tau=0,dimensions=1):
        self._name=name
        self._node=node
        self._tau=tau
        self._modulatory=False
        self._func=func
        self.setDimensions(dimensions)

    def getName(self):
        return self._name
    def setDimensions(self,dimensions):
        self._values=[0]*dimensions
        self._filtered_values=[0]*dimensions
        self._dimensions=dimensions
    def getDimensions(self):
        return self._dimensions
    def getNode(self):
        return self._node
    def getTau(self):
        return self._tau
    def setTau(self,tau):
        self._tau=tau
    def getModulatory(self):
        return self._modulatory
    def setModulatory(self,modulatory):
        self._modulatory=modulatory

    def setValues(self,values):
        self._values=values.values
    def getOutput(self):
        return self._values

    def reset(self):
        self._values = [0] * self._dimensions
        self._filtered_values = [0] * self._dimensions

    def run(self,start,end):
        dt=end-start
        v=self.getOutput()
        if self.tau<dt or dt==0 or self.tau<=0:
            self._filtered_values=v
        else:    
            decay=math.exp(-dt/self.tau)
            for i in range(self._dimensions):
                x=self._filtered_values[i]
                self._filtered_values[i]=x*decay+v[i]*(1-decay)
        self._func(self._filtered_values)                               

class SimpleOrigin(BasicOrigin):
    def __init__(self,name,node,func):
        BasicOrigin.__init__(self,node,name,len(func()),Units.UNK)
        self.func=func
    def run(self,start,end):
        self.values=RealOutputImpl(self.func(),Units.UNK,end)

class SimpleNode(Node,Probeable):
    """A SimpleNode allows you to put arbitary code as part of a Nengo model.
        
    This object has Origins and Terminations which can be used just like
    any other Nengo component.  Arbitary code can be run every time step,
    making this useful for simulating sensory systems (reading data
    from a file or a webcam, for example), motor systems (writing data to
    a file or driving a robot, for example), or even parts of the brain
    that we don't want a full neural model for (symbolic reasoning or
    declarative memory, for example).
    
    Origins and terminations are defined by subclassing SimpleNode.  For
    example, the following code creates a node that takes a single
    input and outputs the square of that input::
    
      class SquaringNode(nef.SimpleNode):
          def init(self):
              self.value=0
          def termination_input(self,x):
              self.value=x[0]*x[0]
          def origin_output(self):
              return [self.value]
      square=net.add(SquaringNode('square'))
      net.connect(A,square.getTermination('input'))
      net.connect(square.getOrigin('output'),B)
      
    You can have as many origins and terminations as you like.  The dimensionality
    of the origins are set by the length of the returned vector of floats.  The
    dimensionality of the terminations can be set by specifing the dimensionality
    in the method definition::
    
      class SquaringFiveValues(nef.SimpleNode):
          def init(self):
              self.value=0
          def termination_input(self,x,dimensions=5):
              self.value=[xx*xx for xx in x]
          def origin_output(self):
              return [self.value]
    
    You can also specify a post-synaptic time constant for the filter on the
    terminations in the method definition::
    
      class SquaringNode(nef.SimpleNode):
          def init(self):
              self.value=0
          def termination_input(self,x,pstc=0.01):
              self.value=x[0]*x[0]
          def origin_output(self):
              return [self.value]
              
    There is also a special method called tick() that is called once per
    time step.  It is called after the terminations but before the origins::

      class HelloNode(nef.SimpleNode):
          def tick(self):
              print 'Hello world'
              
    The current time can be accessed via ``self.t``.  This value will be the
    time for the beginning of the current time step.  The end of the current
    time step is ``self.t_end``::
    
      class TimeNode(nef.SimpleNode):
          def tick(self):
              print 'Time:',self.t
    """
                  

    pstc=0
    def __init__(self,name):
        """
        :param string name: the name of the created node          
        """
        self._name=name
        self.listeners=[]
        self._origins={}
        self._terminations={}
        self._states=java.util.Properties()
        self.setMode(SimulationMode.DEFAULT)
        self.t_start=0
        self.t_end=0
        self.t=0

        for name,method in inspect.getmembers(self,inspect.ismethod):
            if name.startswith('termination_'):
                self.create_termination(name[12:],method)
        self.init()        
        for name,method in inspect.getmembers(self,inspect.ismethod):
            if name.startswith('origin_'):
                self.create_origin(name[7:],method)

    def create_origin(self,name,func):
        """Adds an origin to the SimpleNode.
        
        Every timestep the function *func* will be called.  It should return a
        vector which is the output value at this origin.
        
        Any member functions of the form ``origin_name`` will automatically be
        created in the constructor, so the following two nodes are equivalent::
        
            class Node1(nef.SimpleNode):
                def origin_test(self):
                    return [0]
            node1=Node1('node1')
            
            node2=nef.SimpleNode('node2')
            def test():
                return [0]
            node2.create_origin('test',test)
        
        :param string name: the name of the origin
        :param function func: the function to call
        
        .. note::
           The function *func* will be called once by create_origin to determine the dimensionality it returns.
        """
        self.addOrigin(SimpleOrigin(name,self,func))
        
    def create_termination(self,name,func):
        """Adds a termination to the SimpleNode.

        Every timestep the function *func* will be called.  It must accept a
        single parameter, which is a list of floats representing the current input to
        the termination.
        
        Any member functions of the form ``termination_name`` will automatically be
        created in the constructor, so the following two nodes are equivalent::
        
            class Node1(nef.SimpleNode):
                def termination_test(self,x):
                    self.data=x[0]
            node1=Node1('node1')
            
            node2=nef.SimpleNode('node2')
            def test(x):
                node2.data=x[0]
            node2.create_termination('test',test)
            
        By default, the termination will be 1 dimensional.  To change this, specify a
        different value in the function definition::
        
            class Node3(nef.SimpleNode):
                def termination_test(self,x,dimensions=4):
                    self.data=x[0]*x[1]+x[2]*x[3]
        
        By default, no post-synaptic filter is applied.  To change this, specify a pstc value::
        
            class Node4(nef.SimpleNode):
                def termination_test(self,x,pstc=0.01):
                    self.data=x[0]
        
        :param string name: the name of the termination
        :param function func: the function to call.
        
        .. note::
           The function *func* will be called once by create_termination with an input of all zeros.        
        """
        dimensions=1
        pstc=self.pstc
        a,va,k,d=inspect.getargspec(func)
        if 'dimensions' in a:
            index=a.index('dimensions')-len(a)
            dimensions=d[index]
        if 'pstc' in a:
            index=a.index('pstc')-len(a)
            pstc=d[index]
        
        t=SimpleTermination(name,self,func,tau=pstc,dimensions=dimensions)
        self.addTermination(t)
#        t.run(0,0)   # call the function once to make sure it works and to initialize any variables needed
            
        
        

    def tick(self):
        """An extra utility function that is called every time step.
        
        Override this to create custom behaviour that isn't necessarily tied
        to a particular input or output.  Often used to write spike data to a file
        or produce some other sort of custom effect.
        """
        pass

    def setTau(self,name,tau):
        """Change the post-synaptic time constant for a termination.
        
        :param string name: the name of the termination to change
        :param float tau: the desired post-synaptic time constant
        """
        self._terminations[name].setTau(tau)

    def init(self):
        """Initialize the node.
        
        Override this to initialize any internal variables.  This will
        also be called whenever the simulation is reset::
        
          class DoNothingNode(nef.SimpleNode):
              def init(self):
                  self.value=0
              def termination_input(self,x,pstc=0.01):
                  self.value=x[0]
              def origin_output(self):
                  return [self.value]
        """        
        pass


    # the following functions implement the basic interface needed to be a Node        
    def getName(self):
        return self._name
    def setName(self,name):
        VisiblyMutableUtils.nameChanged(self, self.getName(), name, self.listeners)
        self._name=name

    def reset(self,randomize=False):
        for termination in self.getTerminations():
            termination.reset()
        self.init()    


    def addChangeListener(self,listener):
        self.listeners.append(listener)
    def removeChangeListener(self,listener):
        self.listeners.remove(listener)

    def run(self,start,end):
        if start<self.t_start: self.reset()
        self.t_start=start
        self.t_end=end
        self.t=self.t_start
        for t in self.getTerminations():
            t.run(start,end)
        self.tick()    
        for o in self.getOrigins():
            o.run(start,end)

    def getOrigins(self):
        return self._origins.values()
    def getOrigin(self,name):
        return self._origins[name]
    def addOrigin(self,origin):
        self._origins[origin.name]=origin
        self._states.setProperty(origin.name,"data")
    def removeOrigin(self, name):
        del self._origins[name]
        self._states.remove(name)

    def getTerminations(self):
        return self._terminations.values()
    def getTermination(self,name):
        return self._terminations[name]
    def addTermination(self,termination):
        self._terminations[termination.name]=termination
    def removeTermination(self, name):
        del self._terminations[name]

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
        
    def getChildren(self):
        return None    




