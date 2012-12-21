import module
import nef

class Input(module.Module):
    def __init__(self,time,**inputs):
        self.changes=[(time,inputs)]
        module.Module.__init__(self)
    def next(self,time,**inputs):
        self.changes.append((time+self.changes[-1][0],inputs))
     
    def connect(self):
        
        self.x=[]
        self.t=[]
        self.z={}
        origins=[]
        for t,inputs in self.changes:
            self.t.append(t)
            x=dict()
            for k,v in inputs.items():
                if k not in self.spa.sinks: raise KeyError('Could not find sink "%s"'%k)
                vocab=self.spa.sinks[k]
                x[k]=vocab.parse(v).v
                if k not in origins:
                    origins.append(k)
                    self.z[k]=[0]*vocab.dimensions
            self.x.append(x)

        for k in origins:
            def function(t, self=self, k=k):
                index=0
                while index<len(self.t) and t>self.t[index]:
                    index+=1
                if index>=len(self.t): return self.z[k]
                else: return list(self.x[index].get(k,self.z[k]))
        
            self.net.make_input(k, function)
            
            self.spa.net.connect('%s.%s'%(self.name,k),'sink_'+k,pstc=0.001)

