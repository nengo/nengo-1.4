import nef
import numeric

class DirectBuffer(nef.SimpleNode):
    input_threshold=0.3
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


