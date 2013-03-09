import ensemble
import input

from theano import tensor as TT
import theano
import numpy
import random
import collections

class Network:
    def __init__(self,name,seed=None):
        """Wraps a Nengo network with a set of helper functions for simplifying the creation of Nengo models.

        :param string name: If a string, create and wrap a new NetworkImpl with the given *name*.  
        :param int seed:    random number seed to use for creating ensembles.  This one seed is used only to
                            start the random generation process, so each neural group created will be different.        
        """
        self.name = name
        self.dt = 0.001
        self.run_time = 0.0    
        self.seed = seed        
        self.nodes = {}            # all the nodes in the network, indexed by name
        self.theano_tick = None   # the function call to run the theano portions of the model one timestep
        self.tick_nodes = []      # the list of nodes who have non-theano code that must be run each timestep
        self.random = random.Random()
        if seed is not None:
            self.random.seed(seed)
          
    def make_subnetwork(self,name):
        """Create and return a subnetwork.  Subnetworks are just Network objects that are
        inside other Networks, and are useful for keeping a model organized.
        
        :param string name: name of created node
        :returns: the created Network       
        """
        
        parent,name=self._parse_name(name)
        network=NetworkImpl()
        network.name=name
        parent.addNode(network)
        if self.seed is None:
            seed = None
        else:
            seed = self.random.randrange(0x7fffffff)
        return Network(network, seed=seed, fixed_seed=self.fixed_seed)

    #TODO: used for Input now, should be used for SimpleNodes when those get implemented 
    def add(self, node):
        """Add an arbitrary non-theano node to the network 
        
        :param Node node: 
        """
        self.tick_nodes.append(node)        
        self.nodes[node.name]=node

    def compute_transform(self, dim_pre, dim_post, weight=1, index_pre=None, index_post=None):
        """Helper function used by :func:`nef.Network.connect()` to create the 
        *dim_pre* by *dim_post* transform matrix. Values are either 0 or *weight*.  
        *index_pre* and *index_post* are used to determine which values are 
        non-zero, and indicate which dimensions of the pre-synaptic ensemble 
        should be routed to which dimensions of the post-synaptic ensemble.

        :param integer dim_pre: first dimension of transform matrix
        :param integer dim_post: second dimension of transform matrix
        :param float weight: the non-zero value to put into the matrix
        :param index_pre: the indexes of the pre-synaptic dimensions to use
        :type index_pre: list of integers or a single integer
        :param index_post: the indexes of the post-synaptic dimensions to use
        :type index_post: list of integers or a single integer
        :returns: a two-dimensional transform matrix performing the requested routing        
        """
        transform = [[0] * dim_pre for i in range(dim_post)] # create a matrix of zeros
        # default index_pre/post lists set up *weight* value on diagonal of transform
        # if dim_post != dim_pre, then values wrap around when edge hit
        if index_pre is None: index_pre = range(dim_pre) 
        elif isinstance(index_pre, int): index_pre = [index_pre] 
        if index_post is None: index_post = range(dim_post) 
        elif isinstance(index_post, int): index_post = [index_post]

        for i in range(max(len(index_pre), len(index_post))):
            pre = index_pre[i % len(index_pre)]
            post = index_post[i % len(index_post)]
            transform[post][pre] = weight
        return transform
        
    def connect(self, pre, post, transform=None, weight=1, index_pre=None, index_post=None, pstc=0.01, func=None, origin_name=None):
        """Connect two nodes in the network.

        *pre* and *post* can be strings giving the names of the nodes, or they
        can be the nodes themselves (FunctionInputs and NEFEnsembles are
        supported). They can also be actual Origins or Terminations, or any
        combination of the above. 

        If transform is not None, it is used as the transformation matrix for
        the new termination. You can also use *weight*, *index_pre*, and *index_post*
        to define a transformation matrix instead.  *weight* gives the value,
        and *index_pre* and *index_post* identify which dimensions to connect.

        If *func* is not None, a new Origin will be created on the pre-synaptic
        ensemble that will compute the provided function. The name of this origin 
        will taken from the name of the function, or *origin_name*, if provided.  If an
        origin with that name already exists, the existing origin will be used 
        rather than creating a new one.

        :param string pre: Name of the node to connect from.
        :param string post: Name of the node to connect to.
        :param transform: The linear transfom matrix to apply across the connection.
                          If *transform* is T and *pre* represents ``x``, then the connection
                          will cause *post* to represent ``Tx``.  Should be an N by M array,
                          where N is the dimensionality of *post* and M is the dimensionality of *pre*.
        :type transform: array of floats                              
        :param index_pre: The indexes of the pre-synaptic dimensions to use.
                          Ignored if *transform* is not None. See :func:`nef.Network.compute_transform()`
        :param float weight: scaling factor for a transformation defined with *index_pre* and *index_post*.
                             Ignored if *transform* is not None. See :func:`nef.Network.compute_transform()`
        :type index_pre: List of integers or a single integer
        :param index_post: The indexes of the post-synaptic dimensions to use.
                           Ignored if *transform* is not None. See :func:`nef.Network.compute_transform()`
        :type index_post: List of integers or a single integer 
        :param float pstc: post-synaptic time constant for the neurotransmitter/receptor on this connection
        :param function func: function to be computed by this connection.  If None, computes ``f(x)=x``.
                              The function takes a single parameter x which is the current value of
                              the *pre* ensemble, and must return wither a float or an array of floats.
        :param string origin_name: Name of the origin to check for / create to compute the given function.
                                   Ignored if func is None.  If an origin with this name already
                                   exists, the existing origin is used instead of creating a new one.
        """
        self.theano_tick = None  # reset timer in case the model has been run previously, as adding a new node means we have to rebuild the theano function
                        
        pre = self.nodes[pre] # get pre Node object from node dictionary
        post = self.nodes[post] # get post Node object from node dictionary

        if hasattr(pre, 'projected_value'): # used for Input objects now, could also be used for SimpleNode origins when they are written
            assert func is None # if pre is an input Node, func must be None
            projected_value = pre.projected_value

        else:  # this should only be used for ensembles (TODO: maybe reorganize this if statement to check if it is an ensemble?)          
            if func is not None: 
                if origin_name is None: origin_name = func.__name__ # if no name provided, take name of function being calculated
                #TODO: better analysis to see if we need to build a new origin (rather than just relying on the name)
                if origin_name not in pre.origin: # if an origin for this function hasn't already been created
                    pre.add_origin(origin_name, func) # create origin with to perform desired func
            else:                    
                origin_name = 'X' # otherwise take default identity output from pre population
            projected_value = pre.origin[origin_name].projected_value

        # compute transform matrix if not given
        if transform is None:
            transform = self.compute_transform(dim_pre=pre.dimensions, dim_post=post.dimensions, weight=weight, index_pre=index_pre, index_post=index_post)

        # apply transform matrix, directing pre dimensions to specific post dimensions
        projected_value = TT.dot(projected_value, numpy.array(transform).T)

        # pass in the pre population output function to a new post termination, connecting them for theano
        post.add_filtered_input(projected_value, pstc) 

    def make(self, name, *args, **kwargs): 
        """Create and return an ensemble of neurons. Note that all ensembles are actually arrays of length 1        
        :returns: the newly created ensemble      

        :param string name: name of the ensemble (must be unique)
        :param int seed: random number seed to use.  Will be passed to both random.seed() and ca.nengo.math.PDFTools.setSeed().
                         If this is None and the Network was constructed with a seed parameter, a seed will be randomly generated.
        """
        if 'seed' not in kwargs.keys(): # if no seed provided, get one randomly from the rng
            kwargs['seed'] = self.random.randrange(0x7fffffff)
    
        self.theano_tick=None  # just in case the model has been run previously, as adding a new node means we have to rebuild the theano function
        e = ensemble.Ensemble(*args, **kwargs) 

        self.nodes[name] = e # store created ensemble in node dictionary

    def make_array(self, name, neurons, array_size, dimensions=1, **kwargs): 
        """Generate a network array specifically, for legacy code \ non-theano API compatibility
        """
        return self.make(name=name, neurons=neurons, dimensions=dimensions, array_size=array_size, **kwargs)
    
    def make_input(self, *args, **kwargs): 
        """ # Create an input and add it to the network
        """
        self.add(input.Input(*args, **kwargs))
            
    def make_theano_tick(self):
        """Generate the theano function for running the network simulation
        :returns theano function 
        """
        updates = collections.OrderedDict() # dictionary for all variables and the theano description of how to compute them 

        for node in self.nodes.values(): # for every node in the network
            if hasattr(node, 'update'): # if there is some variable to update 
                updates.update(node.update()) # add it to the list of variables to update every time step

        theano.config.compute_test_value = 'warn' # for debugging
        return theano.function([], [], updates=updates) # create graph and return optimized update function
       
    def run(self, time):
        """Run the simulation. If called twice, the simulation will continue for *time* more seconds.  
        Note that the ensembles are simulated at the dt timestep specified when they are created.
        
        :param float time: the amount of time (in seconds) to run
        """         
        # if theano graph hasn't been calculated yet, retrieve it
        if self.theano_tick is None: self.theano_tick = self.make_theano_tick() 

        for i in range(int(time / self.dt)):
            t = self.run_time + i * self.dt # get current time step
            # run the non-theano nodes
            for node in self.tick_nodes:    
                node.t = t
                node.tick()
            # run the theano nodes
            self.theano_tick()    
           
        self.run_time += time # update run_time variable
