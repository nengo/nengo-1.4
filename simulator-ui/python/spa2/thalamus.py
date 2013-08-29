import module
import convolution

import numeric as np

class Thalamus(module.Module):
    def __init__(self,bg,**params):
        module.Module.__init__(self,**params)
        self.bg=bg
    
    def init(self, rule_neurons=40, rule_threshold=0.2, mutual_inhibit=1, pstc_mutual=0.008):
        D=self.bg.rules.count()
        
        self.net.make_input('bias', [1])
        self.net.make_array('rules', rule_neurons, D, intercept=(rule_threshold, 1), encoders=[[1]])
        self.net.connect('bias', 'rules')
        
        if mutual_inhibit>0:
            self.net.connect('rules', 'rules', (np.eye(D)-1)*mutual_inhibit, pstc=pstc_mutual)       

    def connect(self, weight_GPi=-3, pstc_GPi=0.008, pstc_output=0.01, neurons_gate=40, gate_threshold=0.3, pstc_to_gate=0.002, pstc_gate=0.008, channel_N_per_D=30, pstc_channel=0.01, array_dimensions=16, verbose=False):
        self.bg.rules.initialize(self.spa)

        # Store rules in the documentation comment for this network for use in the interactive mode view    
        self.net.network.documentation = 'THAL: ' + ','.join(self.bg.rules.names)
                
        self.spa.net.connect(self.bg.name+'.GPi', self.name+'.rules', weight=weight_GPi, pstc=pstc_GPi, func=self.bg.get_output_function())


        if verbose: print '  making direct connections to:'    
        # make direct outputs
        for name in self.spa.sinks.keys():
            if verbose: print '      '+name    
            t=self.bg.rules.rhs_direct(name)
            if t is not None:
                self.spa.net.connect(self.name+'.rules', 'sink_'+name, t, pstc_output)

        used_names=[]        
        if verbose: print '  making gated connections:'    
        # make gated outputs
        for source, sink, conv, weight in self.bg.rules.get_rhs_routes():
            t=self.bg.rules.rhs_route(source,sink,conv, weight)
            if verbose: print '      %s->%s'%(source, sink)     
            
            index = 0
            name = '%s_%s'%(source,sink)
            if weight!=1: 
                name+='(%1.1f)'%weight
                name=name.replace('.','_')
            while name in used_names:
                index += 1
                name = '%s_%s_%d'%(source, sink, index)
                if weight!=1: 
                    name+='(%1.1f)'%weight
                    name=name.replace('.','_')
            used_names.append(name)    
            
            gname='gate_%s'%(name)
            
            self.net.make(gname, neurons_gate, 1, encoders=[[1]], intercept=(gate_threshold, 1))
            self.net.connect('rules', gname, transform=t, pstc=pstc_to_gate)
            self.net.connect('bias', gname)
            
            cname='channel_%s'%(name)
            
            vocab1=self.spa.sources[source]
            vocab2=self.spa.sinks[sink]
            
            if array_dimensions is None:
                self.net.make(cname, channel_N_per_D*vocab2.dimensions, vocab2.dimensions)
            else:
                self.net.make_array(cname, channel_N_per_D*array_dimensions, length=vocab2.dimensions/array_dimensions, dimensions=array_dimensions)    

            
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

        for source, sink, conv, weight in self.bg.rules.get_rhs_route_convs():
            t=self.bg.rules.rhs_route_conv(source,sink,conv, weight)
            if verbose: print '      %s*%s->%s'%(source, conv, sink) 

            index = 0
            name = '%s_%s_%s'%(source,conv,sink)
            if weight!=1: 
                name+='(%1.1f)'%weight
                name=name.replace('.','_')
            while name in used_names:
                index += 1
                name = '%s_%s_%d'%(source, sink, index)
                if weight!=1: 
                    name+='(%1.1f)'%weight
                    name=name.replace('.','_')
            used_names.append(name)    
            
            gname='gate_%s'%(name)
            
            self.net.make(gname, neurons_gate, 1, encoders=[[1]], intercept=(gate_threshold, 1))
            self.net.connect('rules', gname, transform=t, pstc=pstc_to_gate)
            self.net.connect('bias', gname)
            
            cname='channel_%s'%(name)
            
            inv1 = False
            if source[0]=='~':
                source = source[1:]
                inv1 = True
            inv2 = False
            if conv[0]=='~':
                conv = conv[1:]
                inv2 = True
            
            vocab1=self.spa.sources[source]
            vocab2=self.spa.sources[conv]
            vocab3=self.spa.sinks[sink]
            
            
            assert vocab1==vocab2
            assert vocab1==vocab3
            
            convolution.connect(self.spa.net, self.name+'.'+cname, vocab1.dimensions, 
                    'source_'+source, 'source_'+conv, 'sink_'+sink, invert1=inv1, invert2=inv2)
            
            self.net.connect(gname, cname, encoders=-10, pstc=pstc_gate)
            