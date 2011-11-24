import time
import numeric
from simplenode import SimpleNode
from nef_core import Network
 

class LogVector:
    def __init__(self,origin,tau):
        self.origin=origin
        self.tau=tau
        self.init()
    def init(self):
        self.value=numeric.array([0]*self.length(),typecode='f')
    def length(self):
        return len(self.data())
    def type(self):
        return 'v%d'%self.length()
    def data(self):
        return self.origin.getValues().getValues()
    def tick(self,dt):
        self.value*=(1.0-dt/self.tau)
        self.value+=numeric.array(self.data())*(dt/self.tau)    
    def text(self):
        return '[%s]'%(';'.join(['%1.3f'%x for x in self.value]))

class LogVocab(LogVector):
    def __init__(self,origin,tau,vocab,terms=None):
        LogVector.__init__(self,origin,tau)
        self.vocab=vocab
        self.terms=terms
    def text(self):
        return self.vocab.text(self.value,terms=self.terms)
        
class LogSpikeCount:
    def __init__(self,origin):
        self.origin=origin
        self.init()
    def init(self):
        self.value=numeric.array([0]*self.length(),typecode='d')
    def length(self):
        return len(self.data())
    def type(self):
        return 'sc%d'%self.length()
    def data(self):
        return self.origin.getValues().getValues()
    def tick(self,dt):
        for i,s in enumerate(self.data()):
            if s!=0: self.value[i]+=s
    def text(self):
        r='<%s>'%(';'.join(['%d'%x for x in self.value]))
        self.init()
        return r



class Log(SimpleNode):
    def __init__(self,network,name=None,filename=None,interval=0.1,tau=0.01):
        if not isinstance(network,Network):
            network=nef.Network(network)
        self.network=network

        if name is None: self.logname=self.network.network.name
        self.filename=None
        self.fileext='.csv'
        if not filename is None: 
            self.filename=filename
            self.fileext=''

        self.genheader=True
        
        self.tau=0.01
        self.log_names=[]
        self.logs={}
        self.interval=interval
        SimpleNode.__init__(self,'Log')
        self.network.add(self)
    
    def init(self):
        if self.filename is None: self.filename=self.make_filename()
        self.file=None
        for log in self.logs.values():
            log.init()
        self.next_time=0
        self.genheader=True

    def make_filename(self):
        t=time.strftime('%Y%m%d-%H%M%S')
        return '%s-%s'%(t,self.logname)
                    
    def tick(self):
        if len(self.logs)==0: return
        if(self.genheader): self.write_header()
        dt=self.t_end-self.t_start
        for log in self.logs.values():
            log.tick(dt)
        if self.t>=self.next_time:
            self.next_time+=self.interval
            self.write_to_log()    
        
    def add_spikes(self,source,name=None):    
        if name is None: name=source
        self.log_names.append(name)   
        if not source.endswith('.AXON'): source=source+'.AXON'     
        origin=self.network.get(source)
        self.logs[name]=LogSpikeCount(origin)
        
    def add(self,source,name=None,tau=None):
        if name is None: name=source
        if tau is None: tau=self.tau
        self.log_names.append(name)        
        origin=self.network.get(source,require_origin=True)
        self.logs[name]=LogVector(origin,tau)

    def add_vocab(self,source,vocab,name=None,tau=None,terms=None):
        if name is None: name=source
        if tau is None: tau=self.tau
        self.log_names.append(name)        
        origin=self.network.get(source,require_origin=True)
        self.logs[name]=LogVocab(origin,tau=tau,vocab=vocab,terms=terms)
        
        
    def write_header(self):
        fileobj=open(self.filename+self.fileext,'a')
        fileobj.write('time,%s\n'%(','.join(self.log_names)))
        types=[self.logs[n].type() for n in self.log_names]
        #self.file.write('float,%s\n'%(','.join(types)))
        fileobj.close()
        self.genheader=False
        
        
    def write_to_log(self):
        fileobj=open(self.filename+self.fileext,'a')
        data=[self.logs[n].text() for n in self.log_names]
        fileobj.write('%1.3f,%s\n'%(self.t,','.join(data)))
        fileobj.close()
        
        
           
