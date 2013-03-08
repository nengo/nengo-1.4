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
           
    #TODO: used for Input now, should be used for SimpleNodes when those get implemented 
    def add(self, node):
        """Add an arbitrary non-theano node to the network 
        
        :param Node node: 
        """
        self.tick_nodes.append(node)        
        self.nodes[node.name]=node
    
        
    def connect(self, pre, post, transform=None, pstc=0.01, func=None, origin_name=None):
        """Connect two nodes in the network.

        *pre* and *post* can be strings giving the names of the nodes, or they
        can be the nodes themselves (FunctionInputs and NEFEnsembles are
        supported).  They can also be actual Origins or Terminations, or any
        combination of the above. 

        If transform is not None, it is used as the transformation matrix for
        the new termination. 

        If *func* is not None, a new Origin will be created on the pre-synaptic
        ensemble that will compute the provided function.  The name of this origin 
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
            projected_value=pre.projected_value # 

        else:  # this should only be used for ensembles (TODO: maybe reorganize this if statement to check if it is an ensemble?)          
            if func is not None: 
                if origin_name is None: origin_name=func.__name__ # if no name provided, take name of function being calculated
                #TODO: better analysis to see if we need to build a new origin (rather than just relying on the name)
                if origin_name not in pre.origin: # if an origin for this function hasn't already been created
                    pre.add_origin(origin_name, func) # create origin with to perform desired func
                projected_value = pre.origin[origin_name].projected_value
            else:                     
                projected_value = pre.origin['X'].projected_value # otherwise take default identity output from pre population

        # apply transform matrix if given, directing pre dimensions to specific post dimensions
        if transform is not None: projected_value=TT.dot(projected_value, transform) 

        # pass in the pre population output function to a new post termination, connecting them for theano
        post.add_filtered_input(projected_value, pstc) 

    def make(self, name, *args, **kwargs): 
        """Create and return an ensemble of neurons. Note that all ensembles are actually arrays of length 1        
        :returns: the newly created ensemble      

        :param string name:          name of the ensemble (must be unique)
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
