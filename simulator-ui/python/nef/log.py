import time
import numeric
from simplenode import SimpleNode
from nef_core import Network
from array import NetworkArray
import hrr
from ca.nengo.model import Origin

class LogBasic:
    def __init__(self,name,origin):
        self.name=name
        self.origin=origin
        self.init()
    def init(self):
        self.value=numeric.array([0]*self.length(),typecode='f')
    def length(self):
        return len(self.data())
    def type(self):
        return 'v<%d>'%self.length()
    def data(self):
        if isinstance(self.origin,list):
            r=[]
            for o in self.origin: r.extend(o.getValues().getValues())
            return r
        else:    
            return self.origin.getValues().getValues()
    def tick(self,dt):
        self.value=self.data()
    def text(self):
        return ';'.join(['%1.3f'%x for x in self.value])
    def flush(self):
        pass    
 

class LogVector(LogBasic):
    def __init__(self,name,origin,tau):
        LogBasic.__init__(self,name,origin)
        self.tau=tau
    def init(self):
        self.value=numeric.array([0]*self.length(),typecode='f')
    def tick(self,dt):
        self.value*=(1.0-dt/self.tau)
        self.value+=numeric.array(self.data())*(dt/self.tau)    

class LogVocab(LogVector):
    def __init__(self,name,origin,tau,vocab,terms=None,pairs=True):
        LogVector.__init__(self,name,origin,tau)
        self.vocab=vocab
        self.terms=terms
        self.pairs=pairs
    def text(self):
        return self.vocab.text(self.value,terms=self.terms,include_pairs=self.pairs,join=';')
    def type(self):
        return 'vocab'
        
class LogSpikeCount(LogBasic):
    def __init__(self,name,origin,skip=0):
        self.skip=skip
        LogBasic.__init__(self,name,origin)
    def init(self):
        self.value=numeric.array([0]*self.length(),typecode='d')
    def length(self):
        length=LogBasic.length(self)
        scale=1+self.skip
        length2=length/scale
        if length%scale>0: length2+=1
        return length2        
    def type(self):
        return 's<%d>'%self.length()
    def tick(self,dt):
        data=self.data()
        if self.skip>0:
            data=[data[i] for i in range(0,len(data),self.skip+1)]
        for i,s in enumerate(data):
            if s!=0: self.value[i]+=s
    def text(self):
        return ';'.join(['%d'%x for x in self.value])
    def flush(self):
        self.value[:]=0



class Log(SimpleNode):
    def __init__(self,network,name=None,dir=None,filename='%(name)s-%(time)s.csv',interval=0.1,tau=0.01):
        if not isinstance(network,Network):
            network=Network(network)
        self.network=network

        if name is None: self.logname=self.network.network.name
        else: self.logname=name
        
        self.dir=dir
        if not filename.endswith('.csv'): filename+='.csv'
        self.filename_template=filename
        self.interval=interval
        self.tau=tau

        self.logs=[]
        SimpleNode.__init__(self,'Log')
        self.network.add(self)
    
    def init(self):
        self.filename=self.make_filename()
        for log in self.logs:
            log.init()
        self.next_time=0
        self.wrote_header=False

    def make_filename(self):
        t=time.strftime('%Y%m%d-%H%M%S')
        fn=self.filename_template%dict(time=t,name=self.logname)
        if self.dir is not None: fn='%s/%s'%(self.dir,fn)
        return fn
                    
    def tick(self):
        if len(self.logs)==0: return
        if not self.wrote_header: self.write_header()
        dt=self.t_end-self.t_start
        for log in self.logs:
            log.tick(dt)
        if self.t>=self.next_time:            
            self.next_time+=self.interval
            self.write_data()    
            for log in self.logs: log.flush()
        
    def add_spikes(self,source,name=None,skip=0):    
        if name is None: name=source
        
        node=self.network.get(source)
        if isinstance(node,Origin):
            origin=node
        elif isinstance(node,NetworkArray):
            origin=[n.getOrigin('AXON') for n in node.nodes]
        else:    
            origin=node.getOrigin('AXON')
        self.logs.append(LogSpikeCount(name,origin,skip=skip))
        
    def add(self,source,name=None,tau=None):
        if name is None: name=source
        if tau is None: tau=self.tau
        origin=self.network.get(source,require_origin=True)
        self.logs.append(LogVector(name,origin,tau))

    def add_vocab(self,source,vocab=None,name=None,tau=None,terms=None,pairs=True):
        if name is None: name=source
        if tau is None: tau=self.tau
        origin=self.network.get(source,require_origin=True)
        if vocab is None: 
            dim=origin.dimensions
            vocab=hrr.Vocabulary.defaults[dim]        
        self.logs.append(LogVocab(name,origin,tau=tau,vocab=vocab,terms=terms,pairs=pairs))
        
    def write_header(self):
        f=open(self.filename,'a')
        f.write('\n')
        f.write('time,%s\n'%(','.join([log.name for log in self.logs])))
        #f.write('    ,%s\n'%(','.join([log.type() for log in self.logs])))
        f.close()
        self.wrote_header=True
        
        
    def write_data(self):
        data=[log.text() for log in self.logs]
        self.write('%1.3f,%s\n'%(self.t,','.join(data)))
        
    def write(self,text):
        f=open(self.filename,'a')
        f.write(text)
        f.close()
        
        
           
