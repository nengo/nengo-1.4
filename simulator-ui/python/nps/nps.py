import numeric
import hrr
import basalganglia
import nef.convolution
from math import sqrt


class Production:
    def __init__(self,lhs,rhs,lhs_scale):
        self.lhs=lhs
        self.rhs=rhs
        self.lhs_scale=lhs_scale
        
        
class ProductionSet:
    def __init__(self):
        self.productions=[]
    def add(self,lhs,rhs,lhs_scale=1,lhs_terms=1):
        p=Production(lhs,rhs,lhs_scale*1.0/numeric.sqrt(lhs_terms))
        self.productions.append(p)    
        
    def get_buffers(self):
        b=[]
        for p in self.productions:
            for k in p.lhs.keys():
                if '_sameas_' in k:
                    ks=k.split('_sameas_',1)
                    for kk in ks:
                        if kk not in b: b.append(kk)
                else:        
                    if k not in b: b.append(k)
            for k in p.rhs.keys():
                if '_to_' in k:
                    ks=k.split('_to_',1)
                    if '_conv_' in ks[0]:
                        ks=ks[0].split('_conv_',1)+ks[1:]
                    elif '_deconv_' in ks[0]:
                        ks=ks[0].split('_deconv_',1)+ks[1:]
                    for kk in ks:
                        if kk not in b: b.append(kk)
                else:
                    if k not in b: b.append(k)                        
        return b  
    def get_same_buffers(self):
        b=[]
        for p in self.productions:
            for k in p.lhs.keys():
                if '_sameas_' in k:
                    if k not in b: b.append(k)
        return b            
        
    def get_direct_actions(self):
        r=[]
        for p in self.productions:
            for k,v in p.rhs.items():
                if '_to_' not in k:
                    if k not in r: r.append(k)
        return r


    def get_transform_actions(self):
        r=[]
        for p in self.productions:
            for k,v in p.rhs.items():
                if '_to_' in k and '_conv_' not in k and '_deconv_' not in k and v is not True:                
                    if k not in r: r.append(k)
        return r
        

    def get_gate_actions(self):
        r=[]
        for p in self.productions:
            for k,v in p.rhs.items():
                if '_to_' in k and '_conv_' not in k and '_deconv_' not in k and v is True:                
                    if k not in r: r.append(k)
        return r
    def get_gate_deconv_actions(self):
        r=[]
        for p in self.productions:
            for k,v in p.rhs.items():
                if '_to_' in k and '_conv_' not in k and '_deconv_' in k and v is True:                
                    if k not in r: r.append(k)
        return r
    def get_gate_conv_actions(self):
        r=[]
        for p in self.productions:
            for k,v in p.rhs.items():
                if '_to_' in k and '_conv_' in k and '_deconv_' not in k and v is True:                
                    if k not in r: r.append(k)
        return r
                
                        
                        



    def calc_input_transform(self,buffer,vocab):
        r=[]
        for p in self.productions:
            v=p.lhs.get(buffer,None)
            if v is None:
                r.append([0]*vocab.dimensions)
            else:
                r.append(vocab.parse(v).v*p.lhs_scale)
        return numeric.array(r)        

    def calc_input_same_transform(self,buffer,vocab):
        r=[]
        for p in self.productions:
            v=p.lhs.get(buffer,None)
            if v is True:
                r.append([1*p.lhs_scale])
            elif v is False:
                r.append([-1*p.lhs_scale])
            elif isinstance(v,(float,int)):
                r.append([v*p.lhs_scale])
            else:    
                r.append([0])
                    
        return numeric.array(r)        



    def calc_output_transform(self,buffer,vocab):
        r=[]
        for p in self.productions:
            v=p.rhs.get(buffer,None)
            if v is None:
                r.append([0]*vocab.dimensions)
            else:
                r.append(vocab.parse(v).v)
        return numeric.array(r).T        

            
    def calc_output_gates(self,buffer,vocab):
        r=[]
        for p in self.productions:
            v=p.rhs.get(buffer,None)
            if v!=True:
                r.append([0])
            else:
                r.append([-1])
        return numeric.array(r).T        


class DirectChannel(nef.simplenode.SimpleNode):
    def __init__(self,name,dimensions,pstc_gate,pstc_input,normalizing=False):
        self.X=[0]*dimensions
        self.gate=0
        self.normalizing=normalizing
        nef.simplenode.SimpleNode.__init__(self,name)
        self.getTermination('input').setDimensions(dimensions)
        self.getTermination('input').setTau(pstc_input)        
        self.getTermination('gate').setTau(pstc_gate)
    def termination_gate(self,value):
        self.gate=value[0]    
    def termination_input(self,value):
        if self.normalizing:
            h=hrr.HRR(data=value)
            length=h.length()
            if length>1.2: h=h*(1.2/length)
            value=h.v
        self.X=value
    def origin_X(self):
        if self.gate>0.1:
            return [0]*len(self.X)
        else:
            return self.X
    def reset(self,randomize=False):
        self.X=[0]*len(self.X)
        self.gate=0

                

class NPS:
    def __init__(self,net,productions,dimensions,neurons_buffer=40,neurons_bg=40,neurons_product=300,subdimensions=None,bg_radius=1.5,
                 tau_gaba=0.008,tau_ampa=0.002,noise=None,vocab=None,quick=True,bg_output_weight=-3,bg_same_neurons=True,
                 align_hrr=False,direct_convolution=False,direct_buffer=False,direct_gate=False,direct_same=False,buffer_mode='rate'):
        if vocab is None:
            if dimensions in hrr.Vocabulary.defaults and hrr.Vocabulary.defaults[dimensions].randomize!=align_hrr:
                vocab=hrr.Vocabulary.defaults[dimensions]
            else:
                vocab=hrr.Vocabulary(dimensions,randomize=not align_hrr)
        self.vocab=vocab
        self.net=net
        self.production_count=len(productions.productions)
        self.dimensions=dimensions
        
        self.direct_convolution=direct_convolution
        self.direct_buffer=direct_buffer
        self.direct_gate=direct_gate
        self.direct_same=direct_same

        D=len(productions.productions)        
        bias=net.make_input('prod_bias',[1])
        prod=net.make_array('prod',neurons_bg,D,intercept=(0.2,1),encoders=[[1]],quick=quick)
        net.connect(bias,prod)

        input=[]
        transform=[]
        for k in productions.get_buffers():    
            if self.direct_buffer is True or (isinstance(self.direct_buffer,list) and k in self.direct_buffer):
                buffer=net.make('buffer_'+k,1,dimensions,quick=True,mode='direct')
            else:
                if subdimensions!=None:
                    buffer=net.make_array('buffer_'+k,neurons_buffer*subdimensions,dimensions/subdimensions,dimensions=subdimensions,quick=quick,mode=buffer_mode)
                else:
                    buffer=net.make('buffer_'+k,neurons_buffer*dimensions,dimensions,quick=quick,mode=buffer_mode)
            input.append(buffer)
            transform.append(productions.calc_input_transform(k,vocab))        

        for k in productions.get_same_buffers():
            a,b=k.split('_sameas_',1)
            if self.direct_same:
                dp=net.make('dp_%s_%s'%(a,b),1,1,quick=quick,mode='direct')
            else:    
                dp=net.make('dp_%s_%s'%(a,b),neurons_buffer,1,quick=quick)
            transform.append(productions.calc_input_same_transform(k,vocab))        
            input.append(dp)

            
        basalganglia.make_basal_ganglia(net,input,prod,D,neurons=neurons_bg,input_transform=transform,output_weight=bg_output_weight,noise=noise,radius=bg_radius,same_neurons=bg_same_neurons)
        
        for k in productions.get_same_buffers():
            a,b=k.split('_sameas_',1)
            if self.direct_same:
                same=net.make_array('same_%s_%s'%(a,b),1,dimensions,dimensions=2,quick=quick,mode='direct')
            else:
                same=net.make_array('same_%s_%s'%(a,b),neurons_product*2,dimensions,dimensions=2,quick=quick,encoders=[[1,1],[1,-1],[-1,-1],[-1,1]])

            
            t1=[]
            t2=[]
            for i in range(dimensions):
                m1=numeric.zeros((2,dimensions),typecode='f')
                m2=numeric.zeros((2,dimensions),typecode='f')
                m1[0,i]=1.0
                m2[1,i]=1.0
                for row in m1: t1.append(row)
                for row in m2: t2.append(row)
            
            net.connect('buffer_'+a,same,transform=t1,pstc=tau_ampa)
            net.connect('buffer_'+b,same,transform=t2,pstc=tau_ampa)
            
            def product(x):
                return x[0]*x[1]
            net.connect(same,'dp_%s_%s'%(a,b),func=product,transform=[[1]*dimensions],pstc=tau_ampa)
            
        
        
        

        for k in productions.get_direct_actions():
            if self.direct_buffer:
                net.make('thal_'+k,1,dimensions,quick=True,mode='direct')
            else:    
                net.make('thal_'+k,neurons_buffer*dimensions,dimensions,quick=quick)
            net.connect('thal_'+k,'buffer_'+k,pstc=tau_ampa)
            net.connect(prod,'thal_'+k,transform=productions.calc_output_transform(k,vocab),pstc=tau_ampa)
            
            

        for k in productions.get_transform_actions():
            a,b=k.split('_to_',1)
            name='thal_%s_%s'%(a,b)
            net.make(name,neurons_buffer*dimensions,dimensions,quick=quick)
            net.connect(prod,name,transform=productions.calc_output_transform(k,vocab),pstc=tau_ampa)
            conv=nef.convolution.make_convolution(net,k,name,'buffer_'+a,'buffer_'+b,1,quick=True,mode='direct')


           
        for k in productions.get_gate_actions():
            a,b=k.split('_to_',1)

            if self.direct_gate:
                c=DirectChannel('channel_%s_to_%s'%(a,b),dimensions,pstc_gate=tau_gaba,pstc_input=tau_ampa)
                net.add(c)
                net.connect('buffer_'+a,c.getTermination('input'))
                net.connect(c.getOrigin('X'),'buffer_'+b,pstc=tau_ampa)
            else:
                c=net.make('channel_%s_to_%s'%(a,b),neurons_buffer*dimensions,dimensions,quick=quick)
                net.connect('buffer_'+a,c,pstc=tau_ampa)
                net.connect(c,'buffer_'+b,pstc=tau_ampa)
                c.addTermination('gate',[[-10.0]]*(neurons_buffer*dimensions),tau_gaba,False)

            name='gate_%s_%s'%(a,b)
            net.make(name,neurons_buffer,1,quick=quick,encoders=[[1]],intercept=(0.3,1))
            net.connect('prod',name,transform=productions.calc_output_gates(k,vocab),pstc=tau_ampa)
            net.connect(bias,name)        
            net.connect(name,c.getTermination('gate'))

        for k in productions.get_gate_deconv_actions():
            a,c=k.split('_to_',1)
            a,b=a.split('_deconv_',1)
            
            if self.direct_convolution:
                conv=nef.convolution.make_convolution(net,'%s_deconv_%s_to_%s'%(a,b,c),'buffer_'+a,'buffer_'+b,'buffer_'+c,1,quick=True,invert_second=True,mode='direct',pstc_in=tau_ampa,pstc_out=tau_ampa,pstc_gate=tau_gaba)
            else:
                conv=nef.convolution.make_convolution(net,'%s_deconv_%s_to_%s'%(a,b,c),'buffer_'+a,'buffer_'+b,'buffer_'+c,neurons_product,quick=quick,invert_second=True,pstc_in=tau_ampa,pstc_out=tau_ampa)
                conv.addTermination('gate',[[[-100.0]]*neurons_product]*conv.dimension,tau_gaba,False)

            name='gate_%s_%s_%s'%(a,b,c)
            net.make(name,neurons_buffer,1,quick=quick,encoders=[[1]],intercept=(0.3,1))
            net.connect('prod',name,transform=productions.calc_output_gates(k,vocab),pstc=tau_ampa)
            net.connect(bias,name)        
            net.connect(name,conv.getTermination('gate'))
        
    
    def set_initial(self,time,**args):
        for k,v in args.items():
            inp=self.net.make_input('initial_'+k,self.vocab.parse(v).v,zero_after_time=time)
            self.net.connect(inp,'buffer_'+k)
            
    
    def add_mutual_inhibition(self,weight=1):
        N=self.production_count
        self.net.connect('prod','prod',(numeric.eye(N)-1)*weight)
        
    
    def add_buffer_feedback(self,pstc=0.01,**args):
        for k,v in args.items():
            if self.direct_buffer is True or (isinstance(self.direct_buffer,list) and k in self.direct_buffer):
                feedback=DirectChannel('feedback_'+k,self.dimensions,pstc_gate=0.001,pstc_input=0.001,normalizing=True)
                self.net.add(feedback)
                self.net.connect('buffer_'+k,feedback.getTermination('input'))
                self.net.connect(feedback.getOrigin('X'),'buffer_'+k,weight=v,pstc=pstc)
            else:
                self.net.connect('buffer_'+k,'buffer_'+k,weight=v,pstc=pstc)

            
        
        


