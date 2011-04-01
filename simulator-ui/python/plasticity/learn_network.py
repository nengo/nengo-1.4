import math
import nef
import random
import datetime
import os

class SensoryInfo(nef.SimpleNode):
    def __init__(self,name,in_dim,out_dim,train_len,func,post):
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
        for i in range(self.out_dim):
            self._x[i] += random.gauss(0.0,0.05)
            self._x[i] = max(self._x[i], -1.0)
            self._x[i] = min(self._x[i],  1.0)
        return self._x

    def origin_answer(self):
        return self.func(self._x)

    def termination_error(self,x):
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
        try:
            f = open(filename, 'w')
        except:
            print "Error opening %s" % filename
            return self.print_data_log()
        
        for line in self.data_log:
            f.write("%s\n" % line)
        f.close()
    
    def print_data_log(self):
        for line in self.data_log:
            print line

def make_learn_network(net,func,in_dim,out_dim,NperD=35,train_len=2.0,stdp=False,rate=1e-7):
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
                w[i][j] = random.uniform(-1e-4,1e-4)
        return w
    
    net.connect(senses.getOrigin('X'),pre)
    net.connect(senses.getOrigin('answer'),answer)   
    net.connect(pre,post,origin_name='AXON',weight_func=rand_weights)
    net.connect(answer,error,weight=1)
    net.connect(post,error,weight= -1)
    net.connect(error,post,modulatory=True)
    
    net.learn(post,'pre','error',rate=rate,stdp=stdp)
    senses.getTermination('error').setDimensions(out_dim)
    net.connect(error,senses.getTermination('error'))

def run_experiment(name,func,in_dim,out_dim,train_len=2.0,stdp=False,rate=1e-7,NperD=35,length=2.0,directory=None):
    net = nef.Network(name)
    make_learn_network(net,func,in_dim,out_dim,train_len=train_len,stdp=stdp,rate=rate,NperD=NperD)
    senses = net.network.getNode('SensoryInfo')
    
    if directory != None:
        # Translate 'length' into the full amount needed...
        num_phases = math.ceil(length / train_len) + 1
        run_length = num_phases * (train_len + senses.test_len)
        net.network.run(0,run_length)
        
        now = datetime.datetime.now()
        senses.write_data_log(os.path.join(directory,  name+'-'+now.strftime("%Y-%m-%d_%H-%M")+'.csv'))
    
    return net
