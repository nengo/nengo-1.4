import module

import numeric as np

class Thalamus(module.Module):
    def __init__(self,bg,**params):
        module.Module.__init__(self,**params)
        self.bg=bg
    
    def init(self, rule_neurons=40, rule_threshold=0.2, mutual_inhibit=1, pstc_mutual=0.008):
        D=self.bg.rules.count()
        
        self.net.make_input('bias', [1])
        self.net.make_array('rule', rule_neurons, D, intercept=(rule_threshold, 1), encoders=[[1]])
        self.net.connect('bias', 'rule')
        
        if mutual_inhibit>0:
            self.net.connect('rule', 'rule', (np.eye(D)-1)*mutual_inhibit, pstc=pstc_mutual)       

    def connect(self, weight_GPi=-3, pstc_GPi=0.008, pstc_output=0.01, neurons_gate=40, gate_threshold=0.3, pstc_to_gate=0.002, pstc_gate=0.008, channel_N_per_D=50, pstc_channel=0.01):
        self.bg.rules.initialize(self.spa)

        # Store rules in the documentation comment for this network for use in the interactive mode view    
        self.net.network.documentation = 'THAL: ' + ','.join(self.bg.rules.names)
                
        self.spa.net.connect(self.bg.name+'.GPi', self.name+'.rule', weight=weight_GPi, pstc=pstc_GPi, func=self.bg.get_output_function())


        # make direct outputs
        for name in self.spa.sinks.keys():
            t=self.bg.rules.rhs_direct(name)
            if t is not None:
                self.spa.net.connect(self.name+'.rule', 'sink_'+name, t, pstc_output)

        # make gated outputs
        for source, sink, conv, weight in self.bg.rules.get_rhs_routes():
            t=self.bg.rules.rhs_route(source,sink,conv, weight)
            
            gname='gate_%s_%s'%(source,sink)
            if weight!=1: gname+='(%1.1f)'%weight
            gname=gname.replace('.','_')
            
            self.net.make(gname, neurons_gate, 1, encoders=[[1]], intercept=(gate_threshold, 1))
            self.net.connect('rule', gname, transform=t, pstc=pstc_to_gate)
            self.net.connect('bias', gname)
            
            cname='channel_%s_%s'%(source,sink)
            if weight!=1: cname+='(%1.1f)'%weight
            cname=cname.replace('.','_')
            
            vocab1=self.spa.sources[source]
            vocab2=self.spa.sinks[sink]
            
            self.net.make(cname, channel_N_per_D*vocab2.dimensions, vocab2.dimensions)
            
            if vocab1 is vocab2: 
                transform=None            
            else:
                transform=vocab1.transform_to(vocab2)
                
            if conv is None:
                transform2=np.eye(vocab2.dimensions)*weight
            else:
                transform2=vocab2.parse(conv).get_transform_matrix()*weight    
                
            self.spa.net.connect('source_'+source, self.name+'.'+cname, transform=transform, pstc=pstc_channel)        
            self.spa.net.connect(self.name+'.'+cname, 'sink_'+sink, pstc=pstc_channel, transform=transform2)
            
        
            self.net.connect(gname, cname, encoders=-10, pstc=pstc_gate)


