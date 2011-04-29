import nef

class Input(nef.SimpleNode):
    def __init__(self,time,**inputs):
        self.time=time
        self.changes=[(time,inputs)]
        
        nef.SimpleNode.__init__(self,'input')
    def next(self,time,**inputs):
        self.changes.append((time+self.changes[-1][0],inputs))

    def connect_NCA(self,nca):
        self.x=[]
        self.t=[]
        self.z={}
        self.changes.sort()
        origins=[]
        for t,inputs in self.changes:
            self.t.append(t)
            x=dict()
            for k,v in inputs.items():
                if k in nca._sinks.keys():
                    x[k]=nca.vocab(k).parse(v).v
                    if k not in origins:
                        origins.append(k)
                        self.z[k]=[0]*nca.vocab(k).dimensions
            self.x.append(x)

        for k in origins:
            def origin(self=self,k=k):
                index=0
                while index<len(self.t) and self.t_start>self.t[index]:
                    index+=1
                if index>=len(self.t): return self.z[k]
                else: return self.x[index].get(k,self.z[k])
                
            self.create_origin(k,origin)

            o,t=nca._net.connect(self.getOrigin(k),nca._sinks[k],
                             create_projection=False)
            if nca._sink_parents[k] is not nca._sinks[k]:
                nca._sink_parents[k].exposeTermination(t,t.name)
            nca._net.network.addProjection(o,nca._sink_parents[k].getTermination(t.name))

    
            
