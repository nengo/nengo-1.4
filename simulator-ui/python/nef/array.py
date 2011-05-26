from ca.nengo.model import StructuralException, Origin
from ca.nengo.model.impl import BasicOrigin, NetworkImpl, EnsembleTermination, PreciseSpikeOutputImpl, SpikeOutputImpl, RealOutputImpl
from ca.nengo.model.plasticity.impl import ErrorLearningFunction, InSpikeErrorFunction, \
    OutSpikeErrorFunction, RealPlasticityRule, SpikePlasticityRule
from ca.nengo.model.nef.impl import DecodedTermination
from ca.nengo.model.impl import PlasticEnsembleTermination
from ca.nengo.util import MU
from ca.nengo.util.impl import TimeSeriesImpl

class ArrayOrigin(BasicOrigin):
    serialVersionUID=1
    def __init__(self,parent,name,origins):
        self._parent=parent
        self._name=name
        self._origins=origins
        self._dimensions=sum([o.getDimensions() for o in origins])

    def getName(self):
        return self._name

    def getDimensions(self):
        return self._dimensions

    def setValues(self,values):
        t=values.getTime()
        u=values.getUnits()
        v=values.getValues()

        d=0
        for o in self._origins:
            dim=o.getDimensions()
            o.setValues(RealOutputImpl(v[d:d+dim],u,t))
            d+=dim
        

    def getValues(self):
        v0=self._origins[0].getValues()
        
        units=v0.getUnits()

        v=[]
        if isinstance(v0,PreciseSpikeOutputImpl):
            for o in self._origins: v.extend(o.getValues().getSpikeTimes())
            return PreciseSpikeOutputImpl(v,units,v0.getTime())
        elif isinstance(v0,SpikeOutputImpl):    
            for o in self._origins: v.extend(o.getValues().getValues())
            return SpikeOutputImpl(v,units,v0.getTime())
        elif isinstance(v0,RealOutputImpl):    
            for o in self._origins: v.extend(o.getValues().getValues())
            return RealOutputImpl(v,units,v0.getTime())

    def getNode(self):
        return self._parent

    def clone(self):
        return ArrayOrigin(self._parent,self._name,self._origins)
    


class NetworkArray(NetworkImpl):
    """Collects a set of NEFEnsembles into a single network."""
    serialVersionUID=1
    def __init__(self,name,nodes):
        """Create a network holding an array of the given nodes."""        
        NetworkImpl.__init__(self)
        self.name=name
        self.dimension=len(nodes)*nodes[0].dimension
        self._nodes=nodes
        self._origins={}
        for n in nodes:
            self.addNode(n)
        self.multidimensional=nodes[0].dimension>1
        self.createEnsembleOrigin('X')
        self.setUseGPU(True)

    def createEnsembleOrigin(self,name):
        self._origins[name]=ArrayOrigin(self,name,[n.getOrigin(name) for n in self._nodes])
        self.exposeOrigin(self._origins[name],name)
            
    def addDecodedOrigin(self,name,functions,nodeOrigin):
        """Create a new origin.  A new origin is created on each of the 
        ensembles, and these are grouped together to create an output. The 
        function must be one-dimensional."""
        if len(functions)!=1: raise StructuralException('Functions on EnsembleArrays must be one-dimensional')
        origins=[n.addDecodedOrigin(name,functions,nodeOrigin) for n in self._nodes]
        self.createEnsembleOrigin(name)
        return self.getOrigin(name)
    def addTermination(self,name,matrix,tauPSC,isModulatory):
        """Create a new termination.  A new termination is created on each
        of the ensembles, which are then grouped together.  Useful for adding
        inhibitory terminations to turn off the whole array.
        """

        try:        
            terminations = [n.addTermination(name,matrix[i],tauPSC,isModulatory) for i,n in enumerate(self._nodes)]
        except TypeError:
            terminations=[]
            for i,n in enumerate(self._nodes):
                t=n.addTermination(name,matrix[i*n.neurons:(i+1)*n.neurons],tauPSC,isModulatory)
                terminations.append(t)
                
        termination = EnsembleTermination(self,name,terminations)
        self.exposeTermination(termination,name)
        return self.getTermination(name)
    def addPlasticTermination(self,name,matrix,tauPSC,decoder,weight_func=None):
        """Create a new termination.  A new termination is created on each
        of the ensembles, which are then grouped together.
        
        If decoders are not known at the time the termination is created,
        then pass in an array of zeros of the appropriate size (i.e. however
        many neurons will be in the population projecting to the termination)."""
        terminations = []
        d = 0
        #w = MU.prod(encoder,MU.prod(matrix,MU.transpose(decoder)))
        for n in self._nodes:
            encoder = n.encoders
            #print "matrix",matrix
            #print "encoder",encoder

            #print "decoder_trans",MU.transpose(decoder)
            w = MU.prod(encoder,[MU.prod(matrix,MU.transpose(decoder))[d]])
            if weight_func is not None:
                w = weight_func(w)
            t = n.addTermination(name,w,tauPSC,False)
            terminations.append(t)
            d += 1
        termination = EnsembleTermination(self,name,terminations)
        self.exposeTermination(termination,name)
        return self.getTermination(name)
    def addDecodedTermination(self,name,matrix,tauPSC,isModulatory):
        """Create a new termination.  A new termination is created on each
        of the ensembles, which are then grouped together."""
        terminations=[]
        d=0
        for n in self._nodes:
            dim=n.getDimension()
            t=n.addDecodedTermination(name,matrix[d:d+dim],tauPSC,isModulatory)
            terminations.append(t)
            d+=dim
            
            
        #if self.multidimensional:
        #    terminations=[n.addDecodedTermination(name,matrix[i],tauPSC,isModulatory) for i,n in enumerate(self._nodes)]  
        #else:
        #    terminations=[n.addDecodedTermination(name,[matrix[i]],tauPSC,isModulatory) for i,n in enumerate(self._nodes)]  
        termination=EnsembleTermination(self,name,terminations)
        self.exposeTermination(termination,name)
        return self.getTermination(name)
    
    #gets the nodes in the proper order from the network array. The NetworkImpl version of this function relies on 
    #the nodeMap object which is sometimes out of order.
    def getNodes(self):
        return self._nodes

    # gets the terminations in the same order that they occur in an NEFEnsemble. that is, decoded terminations come last.
    def getTerminations(self):
        terminations = NetworkImpl.getTerminations(self)

        decodedTerminations = []
        nonDecodedTerminations = []
        for term in terminations:
          if  isinstance(term, NetworkImpl.TerminationWrapper):
            term = term.getBaseTermination()

          nodeTerminations = term.getNodeTerminations()

          if nodeTerminations and isinstance(nodeTerminations[0], DecodedTermination):
            decodedTerminations.append(term)
          elif nodeTerminations and isinstance(nodeTerminations[0], PlasticEnsembleTermination):
            nonDecodedTerminations.append(term)
        
        result = nonDecodedTerminations
        result.extend(decodedTerminations)

        return result

    # all the subnodes have to run on the GPU for the networkArray to run on the GPU
    def getUseGPU(self):
        for node in self._nodes:
          if not node.getUseGPU():
            return False

        return NetworkImpl.getUseGPU(self) 


    def exposeAxons(self):
        i=0
        for n in self._nodes:
            self.exposeOrigin(n.getOrigin('AXON'),'AXON_'+str(i))
            i+=1

    def listStates(self):
        """List the items that are probeable."""
        return self._nodes[0].listStates()

    def getHistory(self,stateName):
        """Extract probeable data from the sub-ensembles and combine them together."""
        times=None
        values=[None]*len(self._nodes)
        units=[None]*len(self._nodes)
        for i,n in enumerate(self._nodes):
            data=n.getHistory(stateName)
            if i==0: times=data.getTimes()
            units[i]=data.getUnits()[0]
            values[i]=data.getValues()[0][0]
        return TimeSeriesImpl(times,[values],units)
    
    def setPlasticityRule(self,learn_term,mod_term,rate,stdp,**kwargs):
        in_args = {'a2Minus':  6.6e-3,
                   'a3Minus':  3.1e-3,
                   'tauMinus': 33.7,
                   'tauX':     101.0}
        for key in in_args.keys():
            if kwargs.has_key(key):
                in_args[key] = kwargs[key]
        
        out_args = {'a2Plus':  8.8e-11,
                    'a3Plus':  5.3e-2,
                    'tauPlus': 16.8,
                    'tauY':    125.0}
        for key in out_args.keys():
            if kwargs.has_key(key):
                out_args[key] = kwargs[key]
        
        for n in self._nodes:
            if stdp:
                inFcn = InSpikeErrorFunction([n.scale for n in post.nodes],post.encoders,
                                             in_args['a2Minus'],in_args['a3Minus'],in_args['tauMinus'],in_args['tauX']);
                inFcn.setLearningRate(rate)
                outFcn = OutSpikeErrorFunction([n.scale for n in post.nodes],post.encoders,
                                               out_args['a2Plus'],out_args['a3Plus'],out_args['tauPlus'],out_args['tauY']);                outFcn.setLearningRate(rate)
                rule = SpikePlasticityRule(inFcn,outFcn,'AXON',mod_term)
                n.setPlasticityRule(learn_term,rule)
            else:
                learnFcn = ErrorLearningFunction([neuron.scale for neuron in n.nodes],n.encoders)
                learnFcn.setLearningRate(rate)
                rule = RealPlasticityRule(learnFcn,'X',mod_term)
                n.setPlasticityRule(learn_term,rule)

    def releaseMemory(self):
        for n in self._nodes:
            n.releaseMemory()