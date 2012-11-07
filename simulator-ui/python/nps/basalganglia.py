from numeric import array

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

le=0.2
lg=0.2

def make_basal_ganglia(net,input,output, dimensions, neurons=100,tau_ampa=0.002,tau_gaba=0.008,input_transform=None,output_weight=1,noise=None,same_neurons=True,radius=1.5,learn=False,bistable=False,bistable_gain=1.0,quick=True):

        # create the necessary neural ensembles
        if same_neurons: code=''
        else: code='%d'
        if bistable:
            from ca.nengo.model.neuron.impl import GruberNeuronFactory
            from ca.nengo.math.impl import IndicatorPDF
            str_factory=GruberNeuronFactory(IndicatorPDF(1*bistable_gain,3*bistable_gain),IndicatorPDF(-1*bistable_gain, 0.5*bistable_gain))
            code+='bsg%1.2f'%bistable_gain
        else:
            str_factory=None
            
        StrD1=net.make_array('StrD1',neurons,dimensions,intercept=(e,1),encoders=[[1]],quick=quick,noise=noise,storage_code='bgStrD1'+code,radius=radius,node_factory=str_factory,decoder_sign=1)
        StrD2=net.make_array('StrD2',neurons,dimensions,intercept=(e,1),encoders=[[1]],quick=quick,noise=noise,storage_code='bgStrD2'+code,radius=radius,node_factory=str_factory,decoder_sign=1)
        
        if bistable:
            from ca.nengo.model.impl import EnsembleTermination
            dopD1 = EnsembleTermination(StrD1,'dopamine',[n.getTermination('dopamine') for n in StrD1.getNodes()])
            StrD1.exposeTermination(dopD1,'dopamine')
            dopD2 = EnsembleTermination(StrD2,'dopamine',[n.getTermination('dopamine') for n in StrD2.getNodes()])
            StrD2.exposeTermination(dopD2,'dopamine')
        
        
        STN=net.make_array('STN',neurons,dimensions,intercept=(ep,1),encoders=[[1]],quick=quick,noise=noise,storage_code='bgSTN'+code,radius=radius,decoder_sign=1)
        GPi=net.make_array('GPi',neurons,dimensions,intercept=(eg,1),encoders=[[1]],quick=quick,noise=noise,storage_code='bgGPi'+code,radius=radius,decoder_sign=1)
        GPe=net.make_array('GPe',neurons,dimensions,intercept=(ee,1),encoders=[[1]],quick=quick,noise=noise,storage_code='bgGPe'+code,radius=radius,decoder_sign=1)

        # connect the input to the striatum and STN (excitatory)
        if not isinstance(input,(list,tuple)):
            input=[input]
        for i in range(len(input)):
            if input_transform is None:
                net.connect(input[i],StrD1,weight=ws*(1+lg),pstc=tau_ampa,plastic_array=learn)
                net.connect(input[i],StrD2,weight=ws*(1-le),pstc=tau_ampa,plastic_array=learn)
                net.connect(input[i],STN,weight=wt,pstc=tau_ampa)
            else:
                net.connect(input[i],StrD1,transform=ws*(1+lg)*array(input_transform[i]),pstc=tau_ampa,plastic_array=learn)
                net.connect(input[i],StrD2,transform=ws*(1-le)*array(input_transform[i]),pstc=tau_ampa,plastic_array=learn)
                net.connect(input[i],STN,transform=array(input_transform[i])*wt,pstc=tau_ampa)


        # connect the striatum to the GPi and GPe (inhibitory)
        def func_str(x):
            if x[0]<e: return 0
            return mm*(x[0]-e)
        net.connect(StrD1,GPi,func=func_str,weight=-wm,pstc=tau_gaba)
        net.connect(StrD2,GPe,func=func_str,weight=-wm,pstc=tau_gaba)

        # connect the STN to GPi and GPe (broad and excitatory)
        def func_stn(x):
            if x[0]<ep: return 0
            return mp*(x[0]-ep)
    
        tr=[[wp]*dimensions for i in range(dimensions)]    
        net.connect(STN,GPi,func=func_stn,transform=tr,pstc=tau_ampa)
        net.connect(STN,GPe,func=func_stn,transform=tr,pstc=tau_ampa)        

        # connect the GPe to GPi and STN (inhibitory)
        def func_gpe(x):
            if x[0]<ee: return 0
            return me*(x[0]-ee)
        net.connect(GPe,GPi,func=func_gpe,weight=-we,pstc=tau_gaba)
        net.connect(GPe,STN,func=func_gpe,weight=-wg,pstc=tau_gaba)

        #connect GPi to output (inhibitory)
        def func_gpi(x):
            if x[0]<eg: return 0
            return mg*(x[0]-eg)
        net.connect(GPi,output,func=func_gpi,pstc=tau_gaba,weight=output_weight)


