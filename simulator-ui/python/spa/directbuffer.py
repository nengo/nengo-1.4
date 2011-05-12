import nef
import numeric

class DirectBuffer(nef.SimpleNode):
    input_threshold=0.7
    pstc=0.01
    def __init__(self,dimensions,name='Buffer'):
        self.input=numeric.zeros(dimensions)
        self.x=[0]*dimensions
        self.dimension=dimensions
        nef.SimpleNode.__init__(self,name)
    def origin_value(self):
        return self.x
    def tick(self):
        length=numeric.norm(self.input)
        if length>self.input_threshold:
            self.x=[xx/length for xx in self.input]
        self.input=numeric.zeros(self.dimension)
        
    def addDecodedTermination(self,name,transform,pstc,modulatory):
        def termination(x,self=self,transform=numeric.array(transform)):
            self.input+=numeric.dot(transform,x)
        term=nef.simplenode.SimpleTermination(name,self,termination,tau=pstc,dimensions=len(transform[0]))
        self.addTermination(term)
        return term

import spa.module
class DirectLatch(spa.module.Module):
    def create(self,dimensions=16):
        self.node=DirectBuffer(dimensions)
        self.net.add(self.node)
        self.net.network.exposeOrigin(self.node.getOrigin('value'),self.name)
        self.spa.add_source(self.name,self.net.network.getOrigin(self.name),self)
        self.spa.add_sink(self.name,self.node,self)
        
