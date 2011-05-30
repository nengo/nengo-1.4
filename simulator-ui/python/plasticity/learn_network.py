"""
Module for easily creating and testing learning experiments via Jython.

This module is primarily useful for investigating how effectively a
particular learning rule can learn a particular function. This module
is unlikely to be useful for applying learning function in general
situations; for those purposes, the ability to add plasticity to
networks has been integrated in other convenience modules, such
as the Network.learn function in nef_core.py.
"""

import math
import nef
import random
import datetime
import os

class SensoryInfo(nef.SimpleNode):
    """A node that provides randomly varying input and a precomputed answer
    that will be used by connected populations in a learning simulation.
    
    It also accepts input from another node, usually a population
    representing the amount of error, and logs the accumulated value
    during testing phases. That logged information can be output to
    console or file.
    """
    
    def __init__(self,name,in_dim,out_dim,train_len,func,post):
        """Arguments:
        name -- name assigned to the node
        in_dim -- number of dimensions produced by func
        out_dim -- number of dimension consumed by func
        train_len -- the length of the training phase. The node will
          alternate between testing phases and training phases.
        func -- the function that will be precomputed by this node.
          Must take in a vector of length out_dim and
          return a vector of length in_dim.
        post -- the population that will have learning applied to it.
          This is necessary so that the node can call post.setLearning.
        """
        
        self.train_len = train_len
        self.func = func
        self.in_dim = in_dim
        self.out_dim = out_dim
        self.post = post
        self.error_sum = [0.0] * in_dim
        self._x = [0.0] * out_dim
        
        self.data_log = []

        self.test_len = 5.0
        self.testing = False
        self.test_runs = 0

        nef.SimpleNode.__init__(self,name)

    def origin_X(self):
        """A randomly varying vector of length self.out_dim.
        Varies by adding a small random value to each dimension
        each timestep, drawn from a Gaussian distribution.
        """
        for i in range(self.out_dim):
            self._x[i] += random.gauss(0.0,0.05)
            self._x[i] = max(self._x[i], -1.0)
            self._x[i] = min(self._x[i],  1.0)
        return self._x

    def origin_answer(self):
        """Calls self.func on the randomly varying vector, X."""
        return self.func(self._x)

    def termination_error(self,x):
        """A termination for collecting data on network performance.
        Should be projected to from an error population representing
        a vector of length self.in_dim.
        
        During training phases, this termination does nothing.
        
        During testing phases, this termination accumulates the value
        from the error poplation, in each dimension.
        
        When a testing phase completes, the accumulated value is logged
        as a string in the self.data_log list.
        """
        t = self.t_start
        
        if not self.testing and t >= self.test_runs * (self.test_len + self.train_len):
            self.post.setLearning(False)
            self.error_sum = [0.0] * self.in_dim
            self.test_runs += 1
            self.testing = True
        elif self.testing and t < (self.test_runs * self.test_len) + ((self.test_runs-1) * self.train_len):
            if self.in_dim == 1:
                if x < 0.0: self.error_sum[0] -= x
                else:       self.error_sum[0] += x
            else:
                for i in range(self.in_dim):
                    if x[i] < 0.0: self.error_sum[i] -= x[i]
                    else:          self.error_sum[i] += x[i]
        elif self.testing:
            self.post.setLearning(True)
            self.testing = False
            
            out_l = str((self.test_runs - 1) * self.train_len)
            for i in range(self.in_dim):
                out_l += ', ' + str((self.error_sum[i]))
            
            self.data_log.append(out_l)
    
    def write_data_log(self, filename):
        """Attempts to write the contents of self.data_log to
        the file pointed to by the consumed string, filename.
        If there is an error writing to that file,
        the contents of self.data_log are printed to console instead.
        """
        try:
            f = open(filename, 'w')
        except:
            print "Error opening %s" % filename
            return self.print_data_log()
        
        for line in self.data_log:
            f.write("%s\n" % line)
        f.close()
    
    def print_data_log(self):
        """Prints the contents of self.data_log to the console."""
        for line in self.data_log:
            print line

def make_learn_network(net,func,in_dim,out_dim,train_len=2.0,NperD=35,stdp=False,rate=5e-7,learning=True,**kwargs):
    """Creates a network that will learn the function passed as func.
    That function is some transformation from a vector of length in_dim to
    a vector of length out_dim.
    
    Keyword arguments:
    train_len -- length of the training phase. Learning only happens
      during the training phase.
    NperD -- number of neurons per dimension
    stdp -- set to True to use the spiking plasticity rule, False to
      use the real plasticity rule
    rate -- learning rate (kappa)
    learning -- set to True to have learning enabled, False to create
      a control network that can be used for comparison
    kwargs -- additional arguments to pass to nef.learn
    """
    
    pre_neurons = in_dim*NperD
    answer_neurons = out_dim*NperD
    post_neurons = max(in_dim, out_dim)*NperD
    err_neurons = out_dim*NperD
    radius = 1.1
    
    pre = net.make('pre',pre_neurons,in_dim,radius=radius)
    answer = net.make('answer',answer_neurons,out_dim,radius=radius)
    post = net.make('post',post_neurons,out_dim,radius=radius)
    error = net.make('error',err_neurons,out_dim,radius=radius)
    senses = SensoryInfo(name='SensoryInfo',in_dim=out_dim,
                         out_dim=in_dim,train_len=train_len,
                         func=func,post=post)    
    net.add(senses)
    
    def rand_weights(w):
        for i in range(len(w)):
            for j in range(len(w[0])):
                w[i][j] = random.uniform(-1e-3,1e-3)
        return w
    
    net.connect(senses.getOrigin('X'),pre)
    net.connect(senses.getOrigin('answer'),answer)   
    
    if learning:
        net.connect(pre,post,origin_name='AXON',weight_func=rand_weights)
    else:
        net.connect(pre,post,func=func)
    
    net.connect(answer,error,weight=1)
    net.connect(post,error,weight= -1)
    net.connect(error,post,modulatory=True)
    
    if learning:
        net.learn(post,'pre','error',rate=rate,stdp=stdp,**kwargs)
    
    senses.getTermination('error').setDimensions(out_dim)
    net.connect(error,senses.getTermination('error'))

def run_experiment(name,func,in_dim,out_dim,train_len=2.0,length=2.0,directory=None,**ln_args):
    """Sets up and optionally runs a learning experiment.
    A network is created to learn func, with the given dimensions.
    
    Keyword arguments:
    train_len -- length of the training phase. Learning only happens
      during the training phase.
    length -- the amount of time that the network will learn. Note
      that because of the alternating training and testing phases,
      the actual amount of time that the network runs will be much
      larger (depending on train_len)
    directory -- the directory in which to store the results of the
      experiment. If directory is None, then the network will not
      be run. Since the function returns the created network, a calling
      script can then add the network to the world and look at its
      interactive plots, and the like.
    ln_args -- Arguments to pass on to make_learning_net but not used
      in this function.
    """
    net = nef.Network(name)
    make_learn_network(net,func,in_dim,out_dim,train_len=train_len,**ln_args)
    senses = net.network.getNode('SensoryInfo')
    
    if directory != None:
        # Translate 'length' into the full amount needed...
        num_phases = math.ceil(length / train_len) + 1
        run_length = num_phases * (train_len + senses.test_len)
        net.network.run(0,run_length)
        
        now = datetime.datetime.now()
        f_name = os.path.join(directory, name+'-'+now.strftime("%Y-%m-%d_%H-%M-%S")+'.csv')
        senses.write_data_log(f_name)
    
    return net
