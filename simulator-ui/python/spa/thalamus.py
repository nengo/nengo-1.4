import nef
import ca.nengo
import numeric
import spa.view
import spa.module

class Thalamus(spa.module.Module):
    def __init__(self,bg,**params):
        spa.module.Module.__init__(self,**params)
        self.bg=bg


    def create(self,rule_neurons=40,rule_threshold=0.2,bg_output_weight=-3,
               pstc_output=0.015,mutual_inhibit=1,pstc_inhibit=0.008,
               pstc_to_gate=0.002,pstc_gate=0.008,N_per_D=30,
               pstc_route_input=0.002,pstc_route_output=0.002,neurons_gate=25,
               route_scale=1,pstc_input=0.01):
        D=self.bg.rules.rule_count
        
        self.bias=self.net.make_input('bias',[1])
        self.rules=self.net.make_array('rules',rule_neurons,D,intercept=(rule_threshold,1),encoders=[[1]],quick=True,storage_code="%d")
        self.net.connect(self.bias,self.rules)



        self.net.network.exposeOrigin(self.rules.getOrigin('X'),'rules')

        if mutual_inhibit>0:
            self.net.connect(self.rules,self.rules,(numeric.eye(D)-1)*mutual_inhibit,pstc=pstc_inhibit)


    def connect(self):
        o,t=self.net.connect(self.bg.net.network.getOrigin('output'),self.rules,
                        weight=self.get_param('bg_output_weight'),create_projection=False,pstc=self.p.pstc_input)
        self.net.network.exposeTermination(t,'bg')
        self.spa.net.network.addProjection(self.bg.net.network.getOrigin('output'),self.net.network.getTermination('bg'))

        self.bg.rules.initialize(self.spa)

        # Store rules in the documentation comment for this network for use in the interactive mode view    
        self.net.network.documentation = 'THAL: ' + ','.join(self.bg.rules.names)
        
        for name,source in self.spa.sinks.items():
            t=self.bg.rules.rhs_direct(name)
            if t is not None:
                self.spa.connect_to_sink(self.net.network.getOrigin('rules'),
                                         name,t,self.get_param('pstc_output'))

        for source_name,sink_name,weight in self.bg.rules.get_rhs_routes():
            t=self.bg.rules.rhs_route(source_name,sink_name,weight)

            gname='gate_%s_%s'%(source_name,sink_name)
            if weight!=1: gname+='(%1.1f)'%weight
            gate=self.net.make(gname,self.p.neurons_gate,1,
                               quick=True,encoders=[[1]],intercept=(0.3,1))
            self.net.connect(self.rules,gate,transform=t,pstc=self.p.pstc_to_gate)
            self.net.connect(self.bias,gate)

            source=self.spa.sources[source_name]
            sink=self.spa.sinks[sink_name]
            cname='channel_%s_%s'%(source_name,sink_name)
            if weight!=1: cname+='(%1.1f)'%weight

            sink_module=self.spa.sink_modules[sink_name]
            source_module=self.spa.source_modules[source_name]

            if sink_module.p.dimensions<=source_module.p.dimensions:
                module=sink_module
                use_sink=True
            else:
                module=source_module
                use_sink=False
            
            if module.has_param('subdimensions') and module.p.subdimensions is not None:
                channel=self.net.make_array(cname,module.p.N_per_D*module.p.subdimensions,module.p.dimensions/module.p.subdimensions,dimensions=module.p.subdimensions,quick=True)
            else:
                channel=self.net.make(cname,module.p.N_per_D*module.p.dimensions,module.p.dimensions,quick=True)
            #channel=self.net.make_array(cname,self.p.N_per_D,sink.dimension,quick=True)

            self.net.network.exposeOrigin(channel.getOrigin('X'),cname)
            

            v1=self.spa.vocab(source_name)
            v2=self.spa.vocab(sink_name)
            if v1 is v2: transform=None
            else: transform=v1.transform_to(v2)


            if use_sink:
                tr1=transform
                tr2=None
            else:
                tr1=None
                tr2=transform
            
            self.spa.connect_to_sink(self.net.network.getOrigin(cname),sink_name,tr2,
                                     self.p.pstc_output,termination_name=cname)


            o1,t1=self.net.connect(source,channel,pstc=self.p.pstc_route_input,weight=weight*self.p.route_scale,transform=tr1,create_projection=False)
            self.net.network.exposeTermination(t1,cname)
            self.spa.net.network.addProjection(o1,self.net.network.getTermination(cname))

            channel.addTermination('gate',[[-10.0]]*(module.p.N_per_D*module.p.dimensions),self.p.pstc_gate,False)
            self.net.connect(gate,channel.getTermination('gate'))

        """

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
        """

        
    
