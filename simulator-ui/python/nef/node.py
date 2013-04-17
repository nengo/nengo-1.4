from ca.nengo.model import Termination, Origin, Probeable, SimulationMode, Units
import ca.nengo.model.Node
from ca.nengo.model.impl import BasicOrigin, RealOutputImpl
from ca.nengo.util import VisiblyMutableUtils
import java
import inspect
import warnings
import math


class NodeTermination(Termination):
    def __init__(self, name, node, tau=0, dimensions=1):
        self._name = name
        self._node = node
        self._tau = tau
        self._modulatory = False
        self.setDimensions(dimensions)
    def getName(self):
        return self._name        
    def setDimensions(self, dimensions):
        self._values = [0]*dimensions
        self._filtered_values = [0]*dimensions
        self._dimensions = dimensions
    def getDimensions(self):
        return self._dimensions
    def getNode(self):
        return self._node
    def getTau(self):
        return self._tau
    def setTau(self, tau):
        self._tau = tau
    def getModulatory(self):
        return self._modulatory
    def setModulatory(self, modulatory):
        self._modulatory = modulatory
    def setValues(self, values):
        self._values = values.values
    def getOutput(self):
        return self._values

    def reset(self):
        self._values = [0]*self._dimensions
        self._filtered_values = [0]*self._dimensions

    def run(self, start, end):
        dt = end - start
        v = self.getOutput()
        if self.tau < dt or dt == 0 or self.tau <= 0:
            self._filtered_values = v
        else:    
            decay = math.exp(-dt / self.tau)
            for i in range(self._dimensions):
                x = self._filtered_values[i]
                self._filtered_values[i] = x * decay + v[i] * (1 - decay)
                
    def get(self):
        return self.getOutput()            


class NodeOrigin(BasicOrigin):
    def __init__(self, name, node, dimensions):
        BasicOrigin.__init__(self, node, name, dimensions, Units.UNK)
        self._value = [0] * dimensions
    def reset(self):
        self._value = [0] * self.dimensions
    def set(self, value):
        if len(value) != self.dimensions:
            raise Exception('Expected vector of length %d, not %d'%
                        (self.dimensions, len(value)))
        self._value = value    
    def run(self, start, end):
        self.values = RealOutputImpl(self._value, Units.UNK, end)


class Node(ca.nengo.model.Node, Probeable):
    """A Node allows you to put arbitary code as part of a Nengo model.
        
    This object has Origins and Terminations which can be used just like
    any other Nengo component.  Arbitrary code can be run every time step,
    making this useful for simulating sensory systems (reading data
    from a file or a webcam, for example), motor systems (writing data to
    a file or driving a robot, for example), or even parts of the brain
    that we don't want a full neural model for (symbolic reasoning or
    declarative memory, for example).
    
    Behaviour, inputs, and outputs are defined by subclassing Node.  For
    example, the following code creates a node that takes a single
    input and outputs the square of that input::
    
      class SquaringNode(nef.Node):
          def __init__(self, name):
              nef.Node.__init__(self, name)
              self.input = self.make_input(dimensions=1, pstc=0.01)
              self.output = self.make_output(dimensions=1)
          def tick(self):
              self.output.set(self.input.get()**2)
              
      square = net.add(SquaringNode('square'))
      net.connect('A', 'square:input')
      net.connect('square:output', 'B')
      
    The current time can be accessed via ``self.t``.  This value will be the
    time for the beginning of the current time step.  The end of the current
    time step is ``self.t_end``::
    
      class TimeNode(nef.Node):
          def tick(self):
              print 'Time:',self.t
              
    You can also define a ``start`` method and use generators as a convenient
    way to make functions over time.  For example, to make a Node that will
    produce a random output that changes every 0.1 seconds, do the following::
    
        class YieldNode(nef.Node):
          def __init__(self, name):
              nef.Node.__init__(self, name)
              self.output = self.make_output('output', dimensions=1)

          def start(self):
              while True:
                  self.output.set([random.uniform(-1,1)])
                  yield 0.1   # waits for 0.1 seconds before continuing
    """
                  
    def __init__(self, name):
        """
        :param string name: the name of the created node          
        """
        self._name = name
        self.listeners = []
        self._origins = {}
        self._terminations = {}
        self._states = java.util.Properties()
        self.setMode(SimulationMode.DEFAULT)
        self.t_end = 0
        self.t = 0
        
        self._use_generator = hasattr(self, 'start') and callable(self.start)
        self._generator = None

    def make_output(self, name, dimensions):
        """Defines a new output for the Node.
        
        :param string name: the name of the output, for use when connecting
        :param int dimensions: the dimensionality of the output
        """
        o = NodeOrigin(name, self, dimensions)
        self.addOrigin(o)
        return o
        
    def make_input(self, name, dimensions, pstc=0):
        """Defines a new input for the Node.

        :param string name: the name of the termination
        :param int dimensions: the dimensionality of the input
        :param float pstc: the time constant of the post-synaptic filter to apply        
        """
        t=NodeTermination(name, self, tau=pstc, dimensions=dimensions)
        self.addTermination(t)
        return t

    def tick(self):
        """The main control function that defines how the Node works.
        
        This will be called every time step, and should get() data from the
        inputs (if any) and set() data on the outputs (if any).        
        """
        pass

    def init(self):
        """Initialize the node.
        
        Override this to initialize any internal variables.  This will
        also be called whenever the simulation is reset::
        
          class DelayNode(nef.Node):
              def __init__(self, name, steps, dimensions):
                  super(self, name)
                  self.steps = steps
                  self.input = self.make_input('input', dimensions=dimensions)
                  self.output = self.make_output('output', dimensions=dimensions)
                  self.init()
              def init(self):
                  self.delay = [[0]*dimensions for i in range(self.steps)]
              def tick(self):
                  self.output.set(self.delay[0])
                  self.delay = self.delay[1:] + [self.input.get()] 
        """        
        pass

    # the following functions implement the basic interface needed to be a Node        
    def getName(self):
        return self._name
    def setName(self, name):
        VisiblyMutableUtils.nameChanged(self, self.getName(), name, self.listeners)
        self._name = name

    def reset(self, randomize=False):
        for termination in self.getTerminations():
            termination.reset()
        for origin in self.getOrigins():
            origin.reset()
        self._use_generator = hasattr(self, 'start') and callable(self.start)
        self._generator = None
        self.init()    

    def addChangeListener(self, listener):
        self.listeners.append(listener)
    def removeChangeListener(self, listener):
        self.listeners.remove(listener)

    def run(self, start, end):
        if start < self.t: 
            self.reset()
        self.t = start
        self.t_end = end
        for t in self.getTerminations():
            t.run(start, end)
        
        if self._use_generator:
            if self._generator is None:
                self._generator = self.start()
                self._generator_time = 0
                if self._generator is None: 
                    self._use_generator = False  # just in case start() isn't
                                                 # a generator
        if self._use_generator:
            if self.t >= self._generator_time:
                time = self._generator.next()
                if time is None:
                    self._use_generator = False
                else:
                    self._generator_time += time    

        self.tick()
                    
        for o in self.getOrigins():
            o.run(start, end)

    def getOrigins(self):
        return self._origins.values()
    def getOrigin(self, name):
        return self._origins[name]
    def addOrigin(self, origin):
        self._origins[origin.name] = origin
        self._states.setProperty(origin.name, "data")
    def removeOrigin(self, name):
        del self._origins[name]
        self._states.remove(name)

    def getTerminations(self):
        return self._terminations.values()
    def getTermination(self, name):
        return self._terminations[name]
    def addTermination(self, termination):
        self._terminations[termination.name] = termination
    def removeTermination(self, name):
        del self._terminations[name]

    def getDocumentation(self):
        return ""

    def clone(self):
        raise java.lang.CloneNotSupportedException()

    def setMode(self, mode):
        self._mode = mode
    def getMode(self):
        return self._mode

    def listStates(self):
        return self._states
        
    def getChildren(self):
        return None
