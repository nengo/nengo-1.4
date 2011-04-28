import nef

class Input(nef.SimpleNode):
    def __init__(self,time,**inputs):
        self.time=time
        self.inputs=inputs
        nef.SimpleNode.__init__(self,'input')
        
    def connect_NCA(self,nca):
        for k,v in self.inputs.items():
            if k in nca._sinks.keys():
                x=nca.vocab(k).parse(v).v
                def origin(self=self,x=x,z=[0]*len(x)):
                    if self.t_start<self.time: return x
                    else: return z
                self.create_origin(k,origin)

                o,t=nca._net.connect(self.getOrigin(k),nca._sinks[k],
                                 create_projection=False)
                if nca._sink_parents[k] is not nca._sinks[k]:
                    nca._sink_parents[k].exposeTermination(t,t.name)
                nca._net.network.addProjection(o,nca._sink_parents[k].getTermination(t.name))
                
