import ca.nengo
import nef

import spa.module
import numeric

class DirectInput(nef.SimpleNode):
    def __init__(self,name,dimensions,pstc,vocab):
        self.pstc=pstc
        self.dimension=dimensions
        self.input=numeric.zeros(dimensions,'f')
        self.vocab=vocab
        self.value=None
        nef.SimpleNode.__init__(self,name)
    def tick(self):
        dp=self.vocab.dot(self.input)
        m=max(dp)
        if m>0.3:
            self.value=self.vocab.keys[list(dp).index(m)]
        else:
            self.value=None
        self.input=numeric.zeros(self.dimension,'f')
            
    def addDecodedTermination(self,name,transform,pstc,modulatory):
        def termination(x,self=self,transform=numeric.array(transform)):
            self.input+=numeric.dot(transform,x)
        term=nef.simplenode.SimpleTermination(name,self,termination,tau=pstc,dimensions=len(transform[0]))
        self.addTermination(term)
        return term
    
class DirectStorage(nef.SimpleNode):
    def __init__(self,name,pstc,module,vocab):
        self.pstc=pstc
        self.vocab=vocab
        self.module=module
        self.mem={}
        self.ZERO=numeric.zeros(vocab.dimensions,'f')
        self.x=self.ZERO
        nef.SimpleNode.__init__(self,name)
    def tick(self):

        self.store(self.module.A.value,self.module.B.value)

        recall=self.mem.get(self.module.request.value,None)
        if recall is not None:
            self.x=self.vocab.parse(recall).v
        else:
            self.x=self.ZERO
    def origin_recall(self):
        return self.x

    def store(self,A,B):
        if A is None or B is None: return
        if self.mem.get(A,None)!=B: print 'store',A,B
        self.mem[A]=B
        self.mem[B]=A


class DirectMemory(spa.module.Module):
    def create(self,dimensions,subdimensions=None,N_per_D=30,pstc=0.01):
        self.spa.ensure_vocab(self.name,self)
        vocab=self.spa.vocab(self.name)
        
        self.A=DirectInput('A',dimensions,pstc,vocab)
        self.net.add(self.A)
        self.add_sink(self.A,'A')
        
        self.B=DirectInput('B',dimensions,pstc,vocab)
        self.net.add(self.B)
        self.add_sink(self.B,'B')

        self.request=DirectInput('request',dimensions,pstc,vocab)
        self.net.add(self.request)
        self.add_sink(self.request,'request')

        
        self.mem=DirectStorage('mem',pstc,self,vocab)
        self.net.add(self.mem)
        self.add_source(self.mem.getOrigin('recall'),'recall')
        self.add_source(self.mem.getOrigin('recall'),'clean')


        
        
