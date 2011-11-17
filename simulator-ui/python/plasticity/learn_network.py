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
    
    def __init__(self,name,in_dim,out_dim,train_len,func,post,noise='brown',**noise_args):
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
        noise -- type of noise to use as input. Either 'brown', 'pink', or
          'white' for now
        noise_args -- set of arguments that will be used by the function(s)
          generating the noise
        """
        
        self.train_len = train_len
        self.func = func
        self.in_dim = in_dim
        self.out_dim = out_dim
        self.post = post
        self.error_sum = [0.0] * in_dim
        self._x = [0.0] * out_dim
        
        self.noise = noise
        if noise == 'white':
            self.init_white_noise(spectrum='uniform',**noise_args)
            self.noise_func = self.white_X
        elif noise == 'pink':
            self.init_white_noise(spectrum='logarithmic',**noise_args)
            self.noise_func = self.white_X
        elif noise == 'brown':
            self.noise_func = self.brown_X
        
        self.data_log = []

        self.test_len = 5.0
        self.testing = False
        self.test_runs = 0

        nef.SimpleNode.__init__(self,name)
    
    def init_white_noise(self,fundamental=0.1,cutoff=15.0,rms=0.4,seed=0,spectrum='uniform'):
        """
        Creates a band-limited noise function with specified parameters for each out dimension.
          
        fundamental - The fundamental frequency (Hz), i.e., frequency step size.
        cutoff - The high-frequency limit (Hz)
        rms - The root-mean-squared function amplitude
        seed - Random seed
        spectrum - The type of noise: 'uniform' = white noise, 'logarithmic' = pink noise
        
        """
        n = int(math.floor(cutoff / fundamental))
        
        self.frequencies = [0.0 for _ in range(n)]
        self.amplitudes = [[0.0 for _ in range(n)] for _ in range(self.out_dim)]
        self.phases = [[0.0 for _ in range(n)] for _ in range(self.out_dim)]
        
        if seed > 0:
            random.seed(seed)
        
        for d in range(self.out_dim):
            for i in range(n):
                self.frequencies[i] = fundamental * (i+1)
                
                if spectrum == 'uniform':
                    self.amplitudes[d][i] = random.random()
                elif spectrum == 'logarithmic':
                    self.amplitudes[d][i] = random.random() * fundamental / self.frequencies[i]
                else:
                    raise NotImplementedError("Noise type is invalid")
                
                self.phases[d][i] = -0.5 + 2.0 * random.random()
            
            sample_points = 500
            dx = (1.0 / fundamental) / sample_points
            sum_squared = 0.0
            
            for i in range(sample_points):
                result = 0.0
                
                for j in range(len(self.frequencies)):
                    component = 1.0
                    component *= math.sin(2.0*math.pi*(self.frequencies[j]*i*dx+self.phases[d][j]))
                    result += self.amplitudes[d][j] * component
                sum_squared += result * result
            
            unscaled_rms = math.sqrt(sum_squared / sample_points)
            
            for i in range(n):
                self.amplitudes[d][i] *= rms / unscaled_rms
   
    def white_X(self):
        """Varies X by getting the value of band-limited white
        noise for the current timestep.
        """
        t = self.t_start
        
        for d in range(self.out_dim):
            self._x[d] = 0.0
            
            for j in range(len(self.frequencies)):
                component = 1.0
                component *= math.sin(2.0*math.pi*(self.frequencies[j]*t+self.phases[d][j]))
                self._x[d] += self.amplitudes[d][j] * component
            
    def brown_X(self):
        """Varies X by adding a small amount of noise to the
        current value of X, sampled from a normal distribution.
        This is essentially a constrained random walk, hence
        Brownian noise.
        """
        for i in range(self.out_dim):
            self._x[i] += random.gauss(0.0,0.05)
            self._x[i] = max(self._x[i], -1.0)
            self._x[i] = min(self._x[i],  1.0)
    
    def origin_X(self):
        """A randomly varying vector of length self.out_dim."""
        self.noise_func()
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
            for i in range(self.in_dim): self.error_sum[i] = 0.0
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

def make_learn_network(net,func,in_dim,out_dim,train_len=2.0,NperD=35,rate=5e-7,
        learning=True,noise='brown',cutoff=15.0,**kwargs):
    """Creates a network that will learn the function passed as func.
    That function is some transformation from a vector of length in_dim to
    a vector of length out_dim.
    
    Keyword arguments:
    train_len -- length of the training phase. Learning only happens
      during the training phase.
    NperD -- number of neurons per dimension
    rate -- learning rate (kappa)
    learning -- set to True to have learning enabled, False to create
      a control network that can be used for comparison
    kwargs -- additional arguments to pass to nef.learn
    """
    
    pre_neurons = in_dim*NperD
    answer_neurons = out_dim*NperD
    post_neurons = out_dim*NperD
    err_neurons = out_dim*NperD
    radius = 1.1
    
    pre = net.make('pre',pre_neurons,in_dim,radius=radius)
    answer = net.make('answer',answer_neurons,out_dim,radius=radius)
    post = net.make('post',post_neurons,out_dim,radius=radius)
    error = net.make('error',err_neurons,out_dim,radius=radius)
    senses = SensoryInfo(name='SensoryInfo',in_dim=out_dim,
                         out_dim=in_dim,train_len=train_len,
                         func=func,post=post,noise=noise,cutoff=cutoff)    
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
        net.learn(post,'pre','error',rate=rate,**kwargs)
    
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
