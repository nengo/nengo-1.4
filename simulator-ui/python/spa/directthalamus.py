tau_input=0.008
tau_output=0.01

import nef

class DirectThalamus(nef.SimpleNode):    
    def __init__(self,bg=None,name='Thalamus'):
        self.pstc=tau_input
        self.threshold=0.05
        prod_count=bg.getOrigin('output').dimensions
        self.prod=[0]*prod_count
        self.bg=bg
        nef.SimpleNode.__init__(self,name)
        self.getTermination('bg_input').setDimensions(prod_count)


    def origin_prod(self):
        return self.prod
    def termination_bg_input(self,x):
        for i,v in enumerate(x):
            if v<self.threshold: self.prod[i]=1
            else: self.prod[i]=0

        

    def connect_NCA(self,nca):
        nca._net.connect(self.bg.getOrigin('output'),self.getTermination('bg_input'))

        for k in self.bg.rules._lhs_keys():
            t=self.bg.rules._make_rhs_set_transform(k,nca.vocab(k))
            nca.connect_to_sink(self.getOrigin('prod'),k,t,tau_output)
