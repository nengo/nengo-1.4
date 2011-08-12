import ca.nengo
import nef
import numeric

import spa.module

class Match(spa.module.Module):
    def __init__(self,bg,**params):
        spa.module.Module.__init__(self,**params)
        self.bg=bg
    def create(self,match_neurons=150,pstc_match=0.005):
        self.bg=self.spa.modules[self.bg.name]
    def connect(self):
        self.bg.rules.initialize(self.spa)
        N=self.p.match_neurons
        for (a,b) in self.bg.rules.get_lhs_matches():
            t=self.bg.rules.lhs_match(a,b)
            name='%s_%s'%(a,b)
            dim=self.spa.sources[a].dimensions

            if N==0:
                m=self.net.make(name,1,dim*2,quick=True,mode='direct')
                def dotproduct(x):
                    return sum([x[2*i]*x[2*i+1] for i in range(len(x)/2)])
                funcs=[nef.functions.PythonFunction(dotproduct)]                
                m.addDecodedOrigin('product',funcs,'AXON')
            else:            
                m=self.net.make_array(name,N,dim,dimensions=2,encoders=[[1,1],[1,-1],[-1,-1],[-1,1]],quick=True,radius=1.4,storage_code="%d")
                def product(x): return x[0]*x[1]
                m.addDecodedOrigin('product',[nef.functions.PythonFunction(product,dim)],'AXON')
            self.net.network.exposeOrigin(m.getOrigin('product'),name)

            t1=numeric.zeros((dim*2,dim),typecode='f')
            t2=numeric.zeros((dim*2,dim),typecode='f')
            for i in range(dim):
                t1[i*2,i]=1.0
                t2[i*2+1,i]=1.0

            va=self.spa.vocab(a)
            vb=self.spa.vocab(b)
            if va is not vb:
                t2=numeric.dot(t2,vb.transform_to(va))
            m.addDecodedTermination(a,t1,self.p.pstc_match,False)
            m.addDecodedTermination(b,t2,self.p.pstc_match,False)
            self.net.network.exposeTermination(m.getTermination(a),name+'_1')
            self.net.network.exposeTermination(m.getTermination(b),name+'_2')

            self.spa.net.connect(self.spa.sources[a],self.net.network.getTermination(name+'_1'))
            self.spa.net.connect(self.spa.sources[b],self.net.network.getTermination(name+'_2'))

            if N==0:
                transform=[t for i in range(1)]
            else:
                transform=[t for i in range(dim)]

            self.bg.add_input(self.net.network.getOrigin(name),numeric.array(transform).T)
            
            
        
