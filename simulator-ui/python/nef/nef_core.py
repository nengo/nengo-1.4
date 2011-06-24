"""
Wrapper module for simplifying the creation of Nengo models via Jython.

This system is meant to allow short, concise code to create Nengo models.  The
goal is not to add functionality to Nengo, but rather to simplify the
syntax for common use cases.

For example, we can make a communication channel like this:

    import nef
    net=nef.network('Test Network')
    input=net.make_input('input',values=[0])
    A=net.make('A',neurons=100,dimensions=1)
    B=net.make('B',neurons=100,dimensions=1)
    net.connect(input,A)
    net.connect(A,B)
    net.add_to(world)

The system will automatically create the necessary origins, terminations,
ensemble factories, and so on.

"""

from ca.nengo.model.impl import NetworkImpl, NoiseFactory, FunctionInput
from ca.nengo.model import SimulationMode, Origin, Units, Termination, Network
from ca.nengo.model.nef.impl import NEFEnsembleFactoryImpl
from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model.neuron.impl import LIFNeuronFactory
from ca.nengo.model.plasticity.impl import ErrorLearningFunction, InSpikeErrorFunction, \
    OutSpikeErrorFunction, RealPlasticityRule, SpikePlasticityRule
from ca.nengo.util import MU
from ca.nengo.math.impl import IndicatorPDF,ConstantFunction,PiecewiseConstantFunction
from ca.nengo.math import Function
from ca.nengo.model import StructuralException
from ca.nengo.io import FileManager
import java

import pdfs
import generators
import functions
import array

import timeview

class Network:
    """Wraps a Nengo network with a set of helper functions."""
    serialVersionUID=1
    def __init__(self,name,quick=False):
        if isinstance(name,NetworkImpl):
            self.network=name
        else:
            self.network=NetworkImpl()
            self.network.name=name
        self.defaults=dict(quick=quick)

    def add_to(self,world=None):
        """Add the network to the given Nengo world object.  If there is a
        network with that name already there, remove the old one.  If world is
        None, it will attempt to find a running version of Nengo to add to.
        """

        if world is None:
            import ca.nengo.ui.NengoGraphics
            ng=ca.nengo.ui.NengoGraphics.getInstance()
            world=ca.nengo.ui.util.ScriptWorldWrapper(ng)
        if world is None: return

        
        n=world.getNode(self.network.name)
        if n is not None: world.remove(n)
        world.add(self.network)

    def view(self,play=False):
        timeview.View(self.network,play=play)
        

    def add(self,node):
        """Add the node to the network.

        This is only used for manually created nodes, not FunctionInputs or
        NEFEnsembles.
        """
        self.network.addNode(node)
        return node

    def get(self,name):
        """Returns the node with <name> from the network
        """
        return self.network.getNode(name)

    def make_array(self,name,neurons,length,dimensions=1,**args):
        """Create and return an array of ensembles.  All of the parameters
        from Network.make() can be
        used."""
        nodes=[]
        storage_code=args.get('storage_code','')
        for i in range(length):
            if '%' in storage_code: args['storage_code']=storage_code%i
            n=self.make('%d'%i,neurons,dimensions,add_to_network=False,**args)
            nodes.append(n)
        ensemble=array.NetworkArray(name,nodes)
        self.network.addNode(ensemble)
        ensemble.mode=ensemble.nodes[0].mode
        return ensemble

    def make(self,name,neurons,dimensions,
                  tau_rc=0.02,tau_ref=0.002,
                  max_rate=(200,400),intercept=(-1,1),
                  radius=1,encoders=None,
                  decoder_noise=0.1,
                  eval_points=None,
                  noise=None,noise_frequency=1000,
                  mode='spike',add_to_network=True,
                  node_factory=None,
                  quick=None,storage_code=''):
        """Create and return an ensemble of LIF neurons.

        Keyword arguments:
        tau_rc -- membrane time constant
        tau_ref -- refractory period
        max_rate -- range for uniform selection of maximum firing rate in Hz (as a 2-tuple)
                    or a list of maximum rate values to use
        intercept -- normalized range for uniform selection of tuning curve x-intercept (as 2-tuple)
                     or a list of intercept values to use
        radius -- representational range
        encoders -- list of encoder vectors to use (if None, uniform distribution around unit sphere)
        decoder_noise -- amount of noise to assume when calculating decoders
        eval_points -- list of points within unit sphere to do optimization over
        noise -- current noise to inject, chosen uniformly from (-noise,noise)
        noise_frequency -- sampling rate (how quickly the noise changes)
        mode -- simulation mode ('direct', 'rate', or 'spike')
        """
        if quick is None: quick=self.defaults['quick']
        if quick:
            storage_name='quick_%s_%d_%d_%1.3f_%1.3f'%(storage_code,neurons,dimensions,tau_rc,tau_ref)
            if type(max_rate) is tuple and len(max_rate)==2:
                storage_name+='_%1.1f_%1.1f'%max_rate
            else:
                storage_name+='_%08x'%hash(tuple(max_rate))
            if type(intercept) is tuple and len(intercept)==2:
                storage_name+='_%1.3f_%1.3f'%intercept
            else:
                storage_name+='_%08x'%hash(tuple(intercept))
            if isinstance(radius,list):
                storage_name+='_(%s)_%1.3f'%(''.join(['%1.3f'%x for x in radius]),decoder_noise)
            else:
                storage_name+='_%1.3f_%1.3f'%(radius,decoder_noise)
            if encoders is not None:
                storage_name+='_enc%08x'%hash(tuple([tuple(x) for x in encoders]))
            if eval_points is not None:
                storage_name+='_eval%08x'%hash(tuple([tuple(x) for x in eval_points]))
            if not java.io.File(storage_name+'.'+FileManager.ENSEMBLE_EXTENSION).exists():
                dir=java.io.File('quick')
                if not dir.exists(): dir.mkdirs()
                storage_name='quick'+java.io.File.separator+storage_name
        else:
            storage_name=''
        ef=NEFEnsembleFactoryImpl()
        if node_factory is not None:
            ef.nodeFactory=node_factory
        else:
            if type(max_rate) is tuple and len(max_rate)==2:
                mr=IndicatorPDF(max_rate[0],max_rate[1])
            else:
                mr=pdfs.ListPDF(max_rate)
            if type(intercept) is tuple and len(intercept)==2:
                it=IndicatorPDF(intercept[0],intercept[1])
            else:
                it=pdfs.ListPDF(intercept)
            ef.nodeFactory=LIFNeuronFactory(tauRC=tau_rc,tauRef=tau_ref,maxRate=mr,intercept=it)
        if encoders is not None:
            try:
                ef.encoderFactory=generators.FixedVectorGenerator(encoders)
            except:
                raise Exception('encoders must be a matrix where each row is a non-zero preferred direction vector')
        ef.approximatorFactory.noise=decoder_noise
        if eval_points is not None:
            ef.evalPointFactory=generators.FixedEvalPointGenerator(eval_points)
        if isinstance(radius,list):
            r=radius
        else:
            r=[radius]*dimensions
        n=ef.make(name,neurons,r,storage_name,False)
        if noise is not None:
            for nn in n.nodes:
                nn.noise=NoiseFactory.makeRandomNoise(noise_frequency,IndicatorPDF(-noise,noise))

        if mode=='rate' or mode==SimulationMode.RATE:
            n.mode=SimulationMode.RATE
        elif mode=='direct' or mode==SimulationMode.DIRECT:
            n.mode=SimulationMode.DIRECT
        if add_to_network: self.network.addNode(n)
        return n

    def make_input(self,name,values,zero_after_time=None):
        """Create and return a FunctionInput of dimensionality len(values)
        with those values as its constants.  Python functions can be provided
        instead of values (either as a single function that returns a value or
        array of values, or an array of functions).

        Keyword arguments:
        zero_after_time -- sets constant values to zero after this
                           amount of time has elapsed
        """

        funcs=[]
        if callable(values):
            d=values(0)
            if isinstance(d,(tuple,list)):
                for i in range(len(d)):
                    funcs.append(functions.PythonFunction(lambda x,i=i:values(x)[i],time=True))
            else:
                funcs.append(functions.PythonFunction(lambda x:values(x),time=True))
        else:
            for v in values:
                if callable(v):
                    f=functions.PythonFunction(v,time=True)
                elif zero_after_time is None:
                    f=ConstantFunction(1,v)
                else:
                    f=PiecewiseConstantFunction([zero_after_time],[v,0])
                funcs.append(f)
        input=FunctionInput(name,funcs,Units.UNK)
        self.network.addNode(input)
        return input


    def _parse_pre(self,pre,func,origin_name):
        if isinstance(pre,Origin):
            if func is not None:
                raise Exception('Cannot compute a function from a specified Origin')
            return pre
        elif isinstance(pre,FunctionInput):
            if func is not None:
                raise Exception('Cannot compute a function from a FunctionInput')
            return pre.getOrigin('origin')
        elif isinstance(pre,NEFEnsemble) or (hasattr(pre,'getOrigin') and hasattr(pre,'addDecodedOrigin')):
            if func is not None:
                if isinstance(func,Function):
                    if origin_name is None:
                        fname=func.__class__.__name__
                        if '.' in fname: fname=fname.split('.')[-1]
                    else: fname=origin_name
                    origin=pre.addDecodedOrigin(fname,[func],'AXON')
                else:
                    if origin_name is None: fname=func.__name__
                    else: fname=origin_name
                    try:
                        origin=pre.getOrigin(fname)
                    except StructuralException:
                        origin=None
                    if origin is None:
                        if isinstance(pre,array.NetworkArray):
                            dim=pre._nodes[0].dimension
                        else:
                            dim=pre.dimension

                        value=func([0]*dim)
                        if isinstance(value,(int,float)):
                            origin=pre.addDecodedOrigin(fname,[functions.PythonFunction(func,dim)],'AXON')
                        else:
                            funcs=[functions.PythonFunction(func,dim,use_cache=True,index=i) for i in range(len(value))]
                            origin=pre.addDecodedOrigin(fname,funcs,'AXON')
                            
                return origin
            else:
                return pre.getOrigin('X')
        else:
            raise Exception('Unknown object to connect from')

    def compute_transform(self,dim_pre,dim_post,
                          weight=1,index_pre=None,index_post=None):
        """Create a dim_pre x dim_post matrix.  All values are either 0 or
        weight.  index_pre and index_post are used to determine which
        values are non-zero, and indicate which dimensions of the pre-synaptic
        neuron should be routed to which dimensions of the post-synaptic.

        For example, with dim_pre=2 and dim_post=3:
        index_pre=[0,1],index_post=[0,1] means "take the first two dimensions
        of pre and send them to the first two dimensions of post, giving
        [[1,0],[0,1],[0,0]]
        If an index is ommitted, the full range [0,1,2,...,N] is assumed,
        so the above example could just be index_post=[0,1]

        """
        t=[[0]*dim_pre for i in range(dim_post)]
        if index_pre is None: index_pre=range(dim_pre)
        elif isinstance(index_pre,int): index_pre=[index_pre]
        if index_post is None: index_post=range(dim_post)
        elif isinstance(index_post,int): index_post=[index_post]

        for i in range(max(len(index_pre),len(index_post))):
            pre=index_pre[i%len(index_pre)]
            post=index_post[i%len(index_post)]
            t[post][pre]=weight
        return t

    def connect(self,pre,post,
                transform=None,weight=1,index_pre=None,index_post=None,
                pstc=0.01,func=None,weight_func=None,origin_name=None,
                modulatory=False,plastic_array=False,create_projection=True):
        """Connect two nodes in the network.

        pre and post can be strings giving the names of the nodes, or they
        can be the nodes themselves (FunctionInputs and NEFEnsembles are
        supported).  They can also be actual Origins or Terminations, or any
        combinaton of the above. If post is set to an integer or None, an origin
        will be created on the pre population, but no other action will be taken.

        pstc is the post-synaptic time constant of the new Termination

        If transform is not None, it is used as the transformation matrix for
        the new termination.  You can also use weight, index_pre, and index_post
        to define a transformation matrix instead.  weight gives the value,
        and index_pre and index_post identify which dimensions to connect (see
        compute_transform for more details).  For example,
            net.connect(A,B,weight=5)
        with both A and B as 2-dimensional ensembles, will use [[5,0],[0,5]] as
        the transform.  Also, you can do
            net.connect(A,B,index_pre=2,index_post=5)
        to connect the 3rd element in A to the 6th in B.  You can also do
            net.connect(A,B,index_pre=[0,1,2],index_post=[5,6,7])
        to connect multiple elements.

        If func is not None, a new Origin will be created on the pre-synaptic
        ensemble that will compute the provided function.  This must be an
        N->1 function at the moment (a single output value, as many
        input values as desired).  The name of this origin will taken from
        the name of the function, or origin_name, if provided.  If an
        origin with that name already exists, it will be used, rather than
        creating a new one.

        If weight_func is not None, the connection will be made using a
        synaptic connection weight matric rather than a DecodedOrigin and
        a Decoded Termination.  The computed weight matrix will be passed
        to the provided function, which is then free to modify any values in
        that matrix, returning a new one that will actually be used.
        """

        if isinstance(pre,str):
            pre=self.network.getNode(pre)
        if isinstance(post,str):
            post=self.network.getNode(post)

        # determine the origin and its dimensions
        origin=self._parse_pre(pre,func,origin_name)
        dim_pre=origin.dimensions

        # check for the special case of being given a pre-existing termination
        if isinstance(post,Termination):
            self.network.addProjection(origin,post)
            return

        if isinstance(post, int):
            dim_post=post
        elif post is None:
            dim_post = 1
        else:
            dim_post=post.dimension

        if transform is None:
            transform=self.compute_transform(dim_pre,dim_post,weight,index_pre,index_post)
        else:
            # handle 1-d transform vectors by changing to 1xN or Nx1
            if isinstance(transform[0],(int,float)):
                if dim_pre==1: transform=[[x] for x in transform]
                elif dim_post==1: transform=[transform]
                else:
                    raise Exception("Don't know how to turn %s into a %sx%s matrix"%(transform,dim_post,dim_pre))
            elif len(transform)!=dim_post and len(transform[0])!=dim_pre:
                raise Exception("transform must be a %dx%d matrix"%(dim_post,dim_pre))
        
        if plastic_array:
            suffix = ''
            attempts = 1
            while attempts < 100:
                try:
                    if hasattr(origin,'decoders'):
                        term = post.addPlasticTermination(pre.name + suffix,transform,pstc,origin.decoders,weight_func)
                    else:
                        term = post.addPlasticTermination(pre.name + suffix,transform,pstc,
                                                          [[0.0]*pre.dimension]*pre.neurons,weight_func)
                    break
                except StructuralException,e:
                    exception = e
                    attempts += 1
                    suffix = '(%d)' % attempts
            else:
                raise exception#StructuralException('cannot create termination %s'%pre.name)
            return self.network.addProjection(pre.getOrigin('AXON'),term)
        
        if weight_func is not None:
            # calculate weights and pass them to the given function
            decoder=origin.decoders
            encoder=post.encoders
            w=MU.prod(encoder,MU.prod(transform,MU.transpose(decoder)))   #gain is handled elsewhere
            w=weight_func(w)
            term=post.addTermination(pre.name,w,pstc,False)
            if not create_projection: return pre.getOrigin('AXON'),term
            self.network.addProjection(pre.getOrigin('AXON'),term)
        elif (post is not None) and (not isinstance(post, int)):
            suffix=''
            attempts=1
            while attempts<100:
                try:
                    term=post.addDecodedTermination(pre.name+suffix,transform,pstc,modulatory)
                    break
                except StructuralException,e:
                    exception=e
                    attempts+=1
                    suffix='(%d)'%attempts
            else:
                raise exception#StructuralException('cannot create termination %s'%pre.name)


            if post is None or isinstance(post, int): return origin
            if not create_projection: return origin,term
            return self.network.addProjection(origin,term)
    
    def learn(self,post,learn_term,mod_term,rate=5e-7,stdp=False,**kwargs):
        """Apply a learning rule to a termination of a population.
        
        post can be either a string or an actual PlasticEnsemble.
        It is the population whose termination will be learned.
        
        learn_term can be either a string or a Termination. It
        is the termination whose transformation will be modified
        by the learning rule.
        
        mod_term can be either a string or a Termination. It is
        the modulatory input to the learning rule; while this
        is technically not required by the plasticity functions
        in Nengo, currently there are no learning rules implemented
        that do not require modulatory input. If this changes,
        this function should change to reflect that.
        
        rate is the learning rate that will be used in the learning
        fuctions. (Possible enhancement: make this 2D for stdp
        mode, different rates for in_fcn and out_fcn?)
        
        stdp is a boolean that signifies whether to use the STDP
        based error-modulated learning rule. If it is True, then
        the SpikePlasticityRule will be used, and the post
        ensemble must be in DEFAULT (spiking) mode.
        If it is False, then the RealPlasticityRule will be used,
        and post can be either in RATE or DEFAULT mode.
        
        **kwargs are any addition arguments to be passed to the
        error functions and/or plasticity rules.
        """
        
        if isinstance(post,str):
            post=self.network.getNode(post)
        if isinstance(learn_term,Termination):
            learn_term=learn_term.getName()
        if isinstance(mod_term,Termination):
            mod_term=mod_term.getName()
        
        if stdp:
            in_args = {'a2Minus':  5.0e-3, #1.0e-1,
                       'a3Minus':  5.0e-3,
                       'tauMinus': 70.0, #120.0,
                       'tauX':     70.0, #140.0
                       }
            for key in in_args.keys():
                if kwargs.has_key(key):
                    in_args[key] = kwargs[key]
            
            inFcn = InSpikeErrorFunction([n.scale for n in post.nodes],post.encoders,
                                         in_args['a2Minus'],in_args['a3Minus'],in_args['tauMinus'],in_args['tauX']);
            inFcn.setLearningRate(rate)
            
            out_args = {'a2Plus':  5.0e-3, #1.0e-2,
                        'a3Plus':  5.0e-3, #5.0e-8,
                        'tauPlus': 70.0, #3.0,
                        'tauY':    70.0, #150.0
                        }
            for key in out_args.keys():
                if kwargs.has_key(key):
                    out_args[key] = kwargs[key]
            
            outFcn = OutSpikeErrorFunction([n.scale for n in post.nodes],post.encoders,
                                           out_args['a2Plus'],out_args['a3Plus'],out_args['tauPlus'],out_args['tauY']);
            outFcn.setLearningRate(rate)
            rule=SpikePlasticityRule(inFcn, outFcn, 'AXON', mod_term)
            
            if kwargs.has_key('decay') and kwargs['decay'] is not None:
                rule.setDecaying(True)
                rule.setDecayScale(kwargs['decay'])
            else:
                rule.setDecaying(False)
            
            if kwargs.has_key('homeostasis') and kwargs['homeostasis'] is not None:
                rule.setHomestatic(True)
                rule.setStableVal(kwargs['homeostasis'])
            else:
                rule.setHomestatic(False)
            
            post.setPlasticityRule(learn_term,rule)
        else:
            oja = True
            if kwargs.has_key('oja'):
                oja = kwargs['oja']
            
            learnFcn = ErrorLearningFunction([n.scale for n in post.nodes],post.encoders,oja)
            learnFcn.setLearningRate(rate)
            rule=RealPlasticityRule(learnFcn, 'X', mod_term)
            post.setPlasticityRule(learn_term,rule)

    def learn_array(self,array,learn_term,mod_term,rate=5e-7,stdp=False,**kwargs):
        """Apply a learning rule to a termination of a NetworkArray.
        
        array is the NetworkArray to have learning applied to it.
        The nodes contained within must all be PlasticEnsembleImpls.
        Or a string with its name, either way.
        
        learn_term can be either a string or a Termination. It
        is the termination whose transformation will be modified
        by the learning rule.
        
        mod_term can be either a string or a Termination. It is
        the modulatory input to the learning rule; while this
        is technically not required by the plasticity functions
        in Nengo, currently there are no learning rules implemented
        that do not require modulatory input. If this changes,
        this function should change to reflect that.
        
        rate is the learning rate that will be used in the learning
        fuctions. (Possible enhancement: make this 2D for stdp
        mode, different rates for in_fcn and out_fcn?)
        
        stdp is a boolean that signifies whether to use the STDP
        based error-modulated learning rule. If it is True, then
        the SpikePlasticityRule will be used, and the post
        ensemble must be in DEFAULT (spiking) mode.
        If it is False, then the RealPlasticityRule will be used,
        and post can be either in RATE or DEFAULT mode.
        
        kwargs are any additional arguments that should be passed
        to the plasticity rule or error functions.
        """

        if isinstance(array,str):
            array = self.network.getNode(array)
        if isinstance(learn_term,Termination):
            learn_term = learn_term.getName()
        if isinstance(mod_term,Termination):
            mod_term = mod_term.getName()
        
        array.setPlasticityRule(learn_term,mod_term,rate,stdp,**kwargs)

def test():
    net=Network('Test')
    m=net.compute_transform(3,3,weight=1)
    assert m==[[1,0,0],[0,1,0],[0,0,1]]
    m=net.compute_transform(2,3,weight=0.5)
    assert m==[[0.5,0],[0,0.5],[0.5,0]]
    m=net.compute_transform(2,4,index_pre=1,index_post=3)
    assert m==[[0,0],[0,0],[0,0],[0,1]]
    m=net.compute_transform(2,4,index_post=2)
    assert m==[[0,0],[0,0],[1,1],[0,0]]
    m=net.compute_transform(2,4,index_pre=0)
    assert m==[[1,0],[1,0],[1,0],[1,0]]
    m=net.compute_transform(2,4,index_post=[2,1])
    assert m==[[0,0],[0,1],[1,0],[0,0]]
    m=net.compute_transform(3,4,index_pre=[1,2],index_post=[2,1])
    assert m==[[0,0,0],[0,0,1],[0,1,0],[0,0,0]]
    print 'tests passed'

