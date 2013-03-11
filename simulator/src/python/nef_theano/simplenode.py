import inspect
import math
import theano

#TODO: consolidate Accumulator and SimpleAccumulator in a module to import and share here and in ensemble
class SimpleAccumulator:
    def __init__(self, node, pstc):
        """A collection of terminations in the same population, all sharing the same time constant        
        Stores the projected_value accumulated across these terminations, i.e. their summed contribution to the represented signal

        :param Ensemble ensemble: the ensemble this set of terminations is attached to
        :param float pstc: post-synaptic time constant on filter
        """
        self.node = node 
        self.projected_value = theano.shared(numpy.zeros(self.node.dimensions).astype('float32')) # the current filtered projected_value
        self.decay = numpy.exp(-self.node.dt / pstc) # time constant for filter
        self.total = None # the theano object representing the sum of the inputs to this filter

    def add(self, projected_value):
        """Add to the current set of termination inputs (with the same post-synaptic time constant pstc) an additional termination input
        self.new_projected_value is the calculation of the contribution of all of the input to each termination with pstc to the population's represented projected_value

        :param theano object projected_value: theano object representing the output of the pre population multiplied by this termination's transform matrix
        """
        if self.total is None: self.total = projected_value # initialize internal value storing voltage levels of neurons
        else: self.total = self.total + projected_value # add input current to voltage level

        self.new_projected_value = self.decay * self.projected_value + (1 - self.decay) * self.total # the theano object representing the filtering operation        

class SimpleOrigin():
    def __init__(self, name, node, func):
        self.func = func

    def run(self, start, end):

class SimpleNode(Node,Probeable):
    """A SimpleNode allows you to put arbitary code as part of a Nengo model.
        
    This object has Origins and Terminations which can be used just like
    any other Nengo component. Arbitrary code can be run every time step,
    making this useful for simulating sensory systems (reading data
    from a file or a webcam, for example), motor systems (writing data to
    a file or driving a robot, for example), or even parts of the brain
    that we don't want a full neural model for (symbolic reasoning or
    declarative memory, for example).
    
    You can have as many origins and terminations as you like.  The dimensionality
    of the origins are set by the length of the returned vector of floats.  The
    dimensionality of the terminations can be set by specifying the dimensionality
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
    
    There is also a special method called tick() that is called once per
    time step.  It is called after the terminations but before the origins::

      class HelloNode(nef.SimpleNode):
          def tick(self):
              print 'Hello world'
              
    The current time can be accessed via ``self.t``.  This value will be the
    time for the beginning of the current time step.  The end of the current
    time step is ``self.t_end``::
    """

    def __init__(self, name, pstc=0.0, dimensions=1, dt=.001):
        """
        :param string name: the name of the created node          
        :param float pstc: the default time constant on the filtered inputs
        :param int dimensions: the number of dimensions of the decoded input signal
        """
        self.name = name
        self.pstc = pstc
        self.dimensions = dimensions
        self.dt = dt
        self.origins = {}
        self.accumulators = {}

        #TODO: get the naming consistent, filtered inputs or terminations
        # look at all the defined methods, if any start with 'termination_', make 
        # filtered inputs that implement the defined function
        for name, method in inspect.getmembers(self, inspect.ismethod):
            if name.startswith('termination_'):
                self.create_termination(name[12:], method)

        self.init() # initialize internal variables if there are any

        # look at all the defined methods, if any start with 'origin_', make 
        # origins that implement the defined function
        for name, method in inspect.getmembers(self, inspect.ismethod):
            if name.startswith('origin_'):
                self.create_origin(name[7:], method)

    def create_origin(self,name,func):
        """Adds an origin to the SimpleNode.
        Every timestep the function *func* will be called.  It should return a
        vector which is the output value at this origin.
        
        :param string name: the name of the origin
        :param function func: the function to call
        """
        self.addOrigin(SimpleOrigin(name, self, func))
        
    def create_filtered_input(self, projected_value, pstc):
        """Create a new termination that takes the given input (a theano object) and filters it with the given pstc
        Adds its contributions to the set of terminations with the same pstc

        :param projected_value: theano object representing the output of the pre population multiplied by this termination's transform matrix
        :param float pstc: post-synaptic time constant
        """
        if pstc not in self.accumulators: # make sure there's an accumulator for given pstc
            self.accumulators[pstc] = SimpleAccumulator(self, pstc)

        # add this termination's contribution to the set of terminations with the same pstc
        # rescale projected_value by this neurons radius to put us in the right range
        self.accumulators[pstc].add(projected_value)

    def tick(self):
        """An extra utility function that is called every time step.
        
        Override this to create custom behaviour that isn't necessarily tied
        to a particular input or output.  Often used to write spike data to a file
        or produce some other sort of custom effect.
        """
        pass

    def init(self):
        """Initialize the node.
        
        Override this to initialize any internal variables.  This will
        also be called whenever the simulation is reset
        """        
        pass

    def reset(self):
        """Reset the state of all the filtered inputs and internal variables"""
        for termination in self.getTerminations():
            termination.reset()
        self.init()    

    def run(self, start, end):
        """Run the simple node

        :param float start: The time to start running 
        :param float end: The time to stop running
        """
        if start < self.time_start: self.reset()

        self.time_start = start
        self.time_end = end
        self.time = self.time_start

        for termination in self.getTerminations():
            termination.run(start, end)

        self.tick()    

        for o in self.getOrigins():
            o.run(start, end)

    def update(self):
        """Compute the set of theano updates needed for this ensemble
        Returns dictionary with new neuron state, termination, and origin values
        """
        self.X = numpy.zeros(self.dimensions) # represented signal
        
        # find the input decoded value 
        # also update the filter projected_values in the accumulators
        for a in self.accumulators.values():            
            self.X += a.new_projected_value # sum of input across each termination for each represented dimension
            updates[a.projected_value] = a.new_projected_value.astype('float32') # add accumulated termination inputs to theano internal variable updates

        self.tick() # call the SimpleNode custom function
            
        # and compute the decoded origin projected_values from the neuron output
        for o in self.origin.values():
            # in the dictionary updates, set each origin's output projected_values equal to the self.neuron.output() we just calculated
            updates.update(o.update(updates[self.neuron.output]))
        
        return updates    
