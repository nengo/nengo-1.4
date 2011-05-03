import nef
import ca.nengo
import numeric

class Thalamus(ca.nengo.model.impl.NetworkImpl):
    def __init__(self,bg,name='BG',neurons=40,rule_threshold=0.2,bg_output_weight=-3,pstc_output=0.002,mutual_inhibit=1,pstc_inhibit=0.008,pstc_to_gate=0.002,pstc_gate=0.008,N_per_D=30,pstc_route_input=0.002):
        ca.nengo.model.impl.NetworkImpl.__init__(self)
        self.name=name
        self.bg=bg
        self.pstc_output=pstc_output
        self.pstc_to_gate=pstc_to_gate
        self.pstc_gate=pstc_gate
        self.pstc_route_input=pstc_route_input
        self.N_per_D=30

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

        # sending a specific value to a specific sink
        for k in self.bg.rules._rhs_set_keys():
            t=self.bg.rules._make_rhs_set_transform(k,nca.vocab(k))
            nca.connect_to_sink(self.getOrigin('rules'),k,t,self.pstc_output)

        # route from a source to a sink
        net=nef.Network(self)
        for k1,k2 in self.bg.rules._rhs_route_keys():
            t=self.bg.rules._make_rhs_route_transform(k1,k2)

            gate=net.make('gate_%s_%s'%(k1,k2),25,1,quick=True,encoders=[[1]],intercept=(0.3,1))
            net.connect(self.rules,gate,transform=t,pstc=self.pstc_to_gate)
            net.connect(self.bias,gate)

            source=nca._sources[k1]
            sink=nca._sinks[k2]
            cname='channel_%s_%s'%(k1,k2)
            channel=net.make(cname,self.N_per_D*sink.dimension,sink.dimension,quick=True)
            self.exposeOrigin(channel.getOrigin('X'),cname)
            nca.connect_to_sink(self.getOrigin(cname),k2,None,self.pstc_output)

            o1,t1=net.connect(source,channel,pstc=self.pstc_route_input,create_projection=False)
            net.network.exposeTermination(t1,cname)
            nca._net.network.addProjection(o1,net.network.getTermination(cname))
            
            channel.addTermination('gate',[[-10.0]]*channel.neurons,self.pstc_gate,False)
            net.connect(gate,channel.getTermination('gate'))


        # route from a source to a sink, convolving with another source
        for k1,k2,k3 in self.bg.rules._rhs_route_conv2_keys():
            t=self.bg.rules._make_rhs_route_transform(k1,k2,k3)

            gate=net.make('gate_%s_%s_%s'%(k1,k2,k3),25,1,quick=True,encoders=[[1]],intercept=(0.3,1))
            net.connect(self.rules,gate,transform=t,pstc=self.pstc_to_gate)
            net.connect(self.bias,gate)

            if k2.startswith('~'):
                k2=k2[1:]
                invert_second=True
            else:
                invert_second=False

            if k1.startswith('~'):
                k1=k1[1:]
                invert_first=True
            else:
                invert_first=False

            source1=nca._sources[k1]
            source2=nca._sources[k2]
            cname='conv_%s_%s'%(k1,k2)
            vocab=nca.vocab(k3)

            conv=nef.convolution.DirectConvolution(cname,vocab.dimensions,invert_first=invert_first,invert_second=invert_second)
            #TODO: add option to use real convolution instead of direct
            
            net.add(conv)
            net.connect(gate,conv.getTermination('gate'))
            net.network.exposeOrigin(conv.getOrigin('C'),cname)
            net.network.exposeTermination(conv.getTermination('A'),cname+'1')
            net.network.exposeTermination(conv.getTermination('B'),cname+'2')
            nca._net.network.addProjection(source1,net.network.getTermination(cname+'1'))
            nca._net.network.addProjection(source2,net.network.getTermination(cname+'2'))
            nca.connect_to_sink(self.getOrigin(cname),k3,None,self.pstc_output)        
        

        
    
