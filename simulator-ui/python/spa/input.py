import nef
import spa.module


class Input(spa.module.Module):
    def __init__(self,time,**inputs):
        self.time=time
        self.changes=[(time,inputs)]
        spa.module.Module.__init__(self)
    def create(self):
        self.node=nef.SimpleNode('input')
        self.net.add(self.node)
        
    def next(self,time,**inputs):
        self.changes.append((time+self.changes[-1][0],inputs))

    def connect(self):
        self.x=[]
        self.t=[]
        self.z={}
        self.changes.sort()
        origins=[]
        for t,inputs in self.changes:
            self.t.append(t)
            x=dict()
            for k,v in inputs.items():
                if self.spa.has_sink(k):
                    x[k]=self.spa.vocab(k).parse(v).v
                    if k not in origins:
                        origins.append(k)
                        self.z[k]=[0]*self.spa.vocab(k).dimensions
            self.x.append(x)

        for k in origins:
            def origin(self=self,k=k):
                index=0
                while index<len(self.t) and self.node.t_start>self.t[index]:
                    index+=1
                if index>=len(self.t): return self.z[k]
                else: return self.x[index].get(k,self.z[k])
                
            self.node.create_origin(k,origin)
            self.connect_to_sink(self.node.getOrigin(k),k,transform=None,pstc=0.001)


    
            
