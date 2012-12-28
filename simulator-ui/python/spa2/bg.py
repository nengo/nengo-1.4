import module
import bgrules

# connection weights from (Gurney, Prescott, & Redgrave, 2001)
mm=1
mp=1
me=1
mg=1
ws=1
wt=1
wm=1
wg=1
wp=0.9
we=0.3
e=0.2
ep=-0.25
ee=-0.2
eg=-0.2


class BasalGanglia(module.Module):
    def __init__(self,rules,**params):
        module.Module.__init__(self,**params)
        self.rules=bgrules.Rules(rules)
    def init(self, N=100, radius=1.5, pstc_gaba=0.008, pstc_ampa=0.002, verbose=False):
        D=self.rules.count()
        
        if verbose: print '  creating %s.StrD1'%self.name
        self.net.make_array('StrD1', N, D, intercept=(e,1), encoders=[[1]], radius=radius, decoder_sign=1)
        if verbose: print '  creating %s.StrD2'%self.name
        self.net.make_array('StrD2', N, D, intercept=(e,1), encoders=[[1]], radius=radius, decoder_sign=1)
        
        if verbose: print '  creating %s.STN'%self.name
        self.net.make_array('STN', N, D, intercept=(ep,1), encoders=[[1]], radius=radius, decoder_sign=1)
        if verbose: print '  creating %s.GPi'%self.name
        self.net.make_array('GPi', N, D, intercept=(eg,1), encoders=[[1]], radius=radius, decoder_sign=1)
        if verbose: print '  creating %s.GPe'%self.name
        self.net.make_array('GPe', N, D, intercept=(ee,1), encoders=[[1]], radius=radius, decoder_sign=1)
        

        # connect the striatum to the GPi and GPe (inhibitory)
        def func_str(x):
            if x[0]<e: return 0
            return mm*(x[0]-e)
        if verbose: print '  connecting StrD1 to GPi'
        self.net.connect('StrD1', 'GPi', func=func_str, weight=-wm, pstc=pstc_gaba)
        if verbose: print '  connecting StrD2 to GPe'
        self.net.connect('StrD2', 'GPe', func=func_str, weight=-wm, pstc=pstc_gaba)

        # connect the STN to GPi and GPe (broad and excitatory)
        def func_stn(x):
            if x[0]<ep: return 0
            return mp*(x[0]-ep)
    
        tr=[[wp]*D for i in range(D)]    
        if verbose: print '  connecting STN to GPi'
        self.net.connect('STN', 'GPi', func=func_stn, transform=tr, pstc=pstc_ampa)
        if verbose: print '  connecting STN to GPe'
        self.net.connect('STN', 'GPe', func=func_stn, transform=tr, pstc=pstc_ampa)        

        # connect the GPe to GPi and STN (inhibitory)
        def func_gpe(x):
            if x[0]<ee: return 0
            return me*(x[0]-ee)
        if verbose: print '  connecting GPe to GPi'
        self.net.connect('GPe', 'GPi', func=func_gpe, weight=-we, pstc=pstc_gaba)
        if verbose: print '  connecting GPe to STN'
        self.net.connect('GPe', 'STN', func=func_gpe, weight=-wg, pstc=pstc_gaba)
        
    def get_output_function(self):    
        def func_gpi(x):
            if x[0]<eg: return 0
            return mg*(x[0]-eg)
        return func_gpi    
        
        
    def connect(self, lg=0.2, pstc_input=0.002, verbose=False):
        if verbose: print '  parsing rules'
        self.rules.initialize(self.spa)

        # Store rules in the documentation comment for this network for use in the interactive mode view  
        # TODO: Figure out a different way to do this, as this line is pretty much the only Nengo-specific
        #       bit of code in here.  
        self.net.network.documentation = 'BG: ' + ','.join(self.rules.names)
        
        # TODO: add support for matches (do this with a subnetwork, not a separate module)
        #if len(self.rules.get_lhs_matches())>0:
        #    self.match=spa.match.Match(self,pstc_match=self.p.pstc_input/2)
        #    self.spa.add_module(self.name+'_match',self.match,create=True,connect=True)
            
        for source in self.spa.sources.keys():
            if verbose: print '  connecting core inputs from',source 
            transform=self.rules.lhs(source)
            if transform is None: continue
            
            self.spa.net.connect('source_'+source, self.name+'.StrD1', transform=(1+lg)*transform, pstc=pstc_input)
            self.spa.net.connect('source_'+source, self.name+'.StrD2', transform=(1-lg)*transform, pstc=pstc_input)
            self.spa.net.connect('source_'+source, self.name+'.STN', transform=transform, pstc=pstc_input)
            
            
            
            # TODO: add learning
            #self.add_index_input(source,self.rules.get_learns(name),learn=True)


        
"""
def make_basal_ganglia(net,input,output, dimensions, neurons=100,tau_ampa=0.002,tau_gaba=0.008,input_transform=None,output_weight=1,noise=None,same_neurons=True,radius=1.5,learn=False,bistable=False,bistable_gain=1.0,quick=True):


        #connect GPi to output (inhibitory)
        def func_gpi(x):
            if x[0]<eg: return 0
            return mg*(x[0]-eg)
        net.connect(GPi,output,func=func_gpi,pstc=tau_gaba,weight=output_weight)

"""
