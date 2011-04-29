import nef
import ca.nengo
import numeric

class Thalamus(ca.nengo.model.impl.NetworkImpl):
    def __init__(self,bg,name='BG',neurons=40,rule_threshold=0.2,bg_output_weight=-3,pstc_output=0.002,mutual_inhibit=1,pstc_inhibit=0.008):
        ca.nengo.model.impl.NetworkImpl.__init__(self)
        self.name=name
        self.bg=bg
        self.pstc_output=pstc_output

        net=nef.Network(self)
        D=bg.getOrigin('output').dimensions

        self.bias=net.make_input('bias',[1])
        self.rules=net.make_array('rules',neurons,D,intercept=(rule_threshold,1),encoders=[[1]],quick=True)
        net.connect(self.bias,self.rules)

        o,t=net.connect(self.bg.getOrigin('output'),self.rules,weight=bg_output_weight,create_projection=False)
        self.exposeTermination(t,'bg')

        self.exposeOrigin(self.rules.getOrigin('X'),'rules')

        if mutual_inhibit>0:
            net.connect(self.rules,self.rules,(numeric.eye(D)-1)*mutual_inhibit,pstc=pstc_inhibit)
            


    def connect_NCA(self,nca):
        nca._net.network.addProjection(self.bg.getOrigin('output'),self.getTermination('bg'))
        
        for k in self.bg.rules._rhs_set_keys():
            t=self.bg.rules._make_rhs_set_transform(k,nca.vocab(k))
            nca.connect_to_sink(self.getOrigin('rules'),k,t,self.pstc_output)
        
        

        
    
