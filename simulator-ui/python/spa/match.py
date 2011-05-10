import ca.nengo
import nef
import numeric

import spa.module

class Match(spa.module.Module):
    def __init__(self,bg,**params):
        spa.module.Module.__init__(self,**params)
        self.bg=bg
    def create(self,match_neurons=60,pstc_match=0.002):
        self.bg=self.spa.modules[self.bg.name]
    def connect(self):
        self.bg.rules.initialize(self.spa)
        N=self.p.match_neurons
        for (a,b) in self.bg.rules.get_lhs_matches():
            t=self.bg.rules.lhs_match(a,b)
            name='%s_%s'%(a,b)
            assert self.spa.sources[a].dimensions==self.spa.sources[b].dimensions
            dim=self.spa.sources[a].dimensions
            
            m=self.net.make_array(name,N,dim,dimensions=2,encoders=[[1,1],[1,-1],[-1,-1],[-1,1]],quick=True)
            def product(x): return x[0]*x[1]
            m.addDecodedOrigin('product',[nef.functions.PythonFunction(product,dim)],'AXON')
            self.net.network.exposeOrigin(m.getOrigin('product'),name)

            t1=numeric.zeros((dim*2,dim),typecode='f')
            t2=numeric.zeros((dim*2,dim),typecode='f')
            for i in range(dim):
                t1[i*2,i]=1.0
                t2[i*2+1,i]=1.0
            m.addDecodedTermination(a,t1,self.p.pstc_match,False)
            m.addDecodedTermination(b,t2,self.p.pstc_match,False)
            self.net.network.exposeTermination(m.getTermination(a),name+'_1')
            self.net.network.exposeTermination(m.getTermination(b),name+'_2')

            self.spa.net.connect(self.spa.sources[a],self.net.network.getTermination(name+'_1'))
            self.spa.net.connect(self.spa.sources[b],self.net.network.getTermination(name+'_2'))

            transform=[t for i in range(dim)]

            self.bg.add_input(self.net.network.getOrigin(name),numeric.array(transform).T)
            
            
        
