from ca.nengo.model import StructuralException, Origin
from ca.nengo.model.impl import BasicOrigin, NetworkImpl, EnsembleTermination, PreciseSpikeOutputImpl, SpikeOutputImpl, RealOutputImpl 
from ca.nengo.model.plasticity.impl import PESTermination, STDPTermination
from ca.nengo.model.nef.impl import DecodedTermination
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

    def getRequiredOnCPU(self):
      for o in self._origins:
        if o.getRequiredOnCPU():
          return True;
      return False;
    
    def setRequiredOnCPU(self, val):
      for o in self._origins:
        o.setRequiredOnCPU(val);

    def clone(self):
        return ArrayOrigin(self._parent,self._name,self._origins)

    def getDecoders(self):
        neurons=self._parent.nodes[0].neurons
        decoders=MU.zero(neurons*len(self._origins),self._dimensions)
        for i,o in enumerate(self._origins):
            MU.copyInto(o.decoders,decoders,i*neurons,i*o.dimensions,neurons)       
        return decoders            
    


class NetworkArray(NetworkImpl):
    """Collects a set of NEFEnsembles into a single network."""
    serialVersionUID=1
    def __init__(self,name,nodes):
        """Create a network holding an array of nodes.  An 'X' Origin
        is automatically created which concatenates the values of each
        internal element's 'X' Origin.
        
        This object is meant to be created using :func:`nef.Network.make_array()`, allowing for the
        efficient creation of neural groups that can represent large vectors.  For example, the
        following code creates a NetworkArray consisting of 50 ensembles of 1000 neurons, each of 
        which represents 10 dimensions, resulting in a total of 500 dimensions represented::
        
          net=nef.Network('Example Array')
          A=net.make_array('A',neurons=1000,length=50,dimensions=10,quick=True)
          
        The resulting NetworkArray object can be treated like a normal ensemble, except for the
        fact that when computing nonlinear functions, you cannot use values from different
        ensembles in the computation, as per NEF theory.
        
        :param string name: the name of the NetworkArray to create
        :param nodes: the nodes to combine together
        :type nodes: list of NEFEnsembles
        """        
        NetworkImpl.__init__(self)
        self.name=name
        self.dimension=len(nodes)*nodes[0].dimension
        self._nodes=nodes
        self._origins={}
        self.neurons=0
        for n in nodes:
            self.addNode(n)
            self.neurons+=n.neurons
        self.multidimensional=nodes[0].dimension>1
        self.createEnsembleOrigin('X')
        self.setUseGPU(True)

    def createEnsembleOrigin(self,name):
        """Create an Origin that concatenates the values of internal Origins.
        
        :param string name: The name of the Origin to create.  Each internal node must
                            already have an Origin with that name.
        """
        self._origins[name]=ArrayOrigin(self,name,[n.getOrigin(name) for n in self._nodes])
        self.exposeOrigin(self._origins[name],name)
            
    def addDecodedOrigin(self,name,functions,nodeOrigin):
        """Create a new Origin.  A new origin is created on each of the 
        ensembles, and these are grouped together to create an output.
        
        This method uses the same signature as ca.nengo.model.nef.NEFEnsemble.addDecodedOrigin()
        
        :param string name: the name of the newly created origin
        :param functions: the functions to approximate at this origin
        :type functions: list of ca.nengo.math.Function objects
        :param string nodeOrigin: name of the base Origin to use to build this function approximation
                                  (this will always be 'AXON' for spike-based synapses)
        """
        origins=[n.addDecodedOrigin(name,functions,nodeOrigin) for n in self._nodes]
        self.createEnsembleOrigin(name)
        return self.getOrigin(name)

    def addTermination(self,name,matrix,tauPSC,isModulatory):
        """Create a new termination.  A new termination is created on each
        of the ensembles, which are then grouped together.  This termination
        does not use NEF-style encoders; instead, the matrix is the actual connection
        weight matrix.  Often used for adding an inhibitory connection that can turn
        off the whole array (by setting *matrix* to be all -10, for example).  
        
        :param string name: the name of the newly created origin
        :param matrix: synaptic connection weight matrix (NxM where M is the total number of neurons in the NetworkArray)
        :type matrix: 2D array of floats
        :param float tauPSC: post-synaptic time constant
        :param boolean isModulatory: False for normal connections, True for modulatory connections (which adjust neural
                                     properties rather than the input current)
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
        many neurons will be in the population projecting to the termination,
        by number of dimensions)."""
        terminations = []
        d = 0
        dd=self._nodes[0].dimension
        for n in self._nodes:
            encoder = n.encoders
            w = MU.prod(encoder,[MU.prod(matrix,MU.transpose(decoder))[d+i] for i in range(dd)])
            if weight_func is not None:
                w = weight_func(w)
            t = n.addPESTermination(name,w,tauPSC,False)
            terminations.append(t)
            d += dd
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

    def addIndexTermination(self,name,matrix,tauPSC,isModulatory=False,index=None):
        """Create a new termination.  A new termination is created on the specified ensembles, 
        which are then grouped together.  This termination does not use NEF-style encoders; instead, 
        the matrix is the actual connection weight matrix.  Often used for adding an inhibitory 
        connection that can turn off selected ensembles within the array (by setting *matrix* to be 
        all -10, for example).  
        
        :param string name: the name of the newly created origin
        :param matrix: synaptic connection weight matrix (NxM where M is the total number of neurons in the ensembles to be connected)
        :type matrix: 2D array of floats
        :param float tauPSC: post-synaptic time constant
        :param boolean isModulatory: False for normal connections, True for modulatory connections (which adjust neural
                                     properties rather than the input current)
        :param index: The indexes of the ensembles to connect to. If set to None, this function behaves exactly like :func:`nef.NetworkArray.addTermination()`.
        :type index_pre: list of integers
        """
        if( index is None ):
            index = range(len(self._nodes))
        terminations = []
        j = 0
        for i,node in enumerate(self._nodes):
            if( i in index ):
                t = node.addTermination(name,matrix[j*node.neurons:(j+1)*node.neurons],tauPSC,isModulatory)
                j = j + 1;
                terminations.append(t)
        
        termination = EnsembleTermination(self,name,terminations)
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
            baseTermination = term.getBaseTermination()
          else:
            baseTermination = term

          nodeTerminations = baseTermination.getNodeTerminations()

          if nodeTerminations and isinstance(nodeTerminations[0], DecodedTermination):
            decodedTerminations.append(term)
          elif nodeTerminations and isinstance(nodeTerminations[0], EnsembleTermination):
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
        return self._nodes[0].listStates()

    def getHistory(self,stateName):
        times=None
        values=[None]*len(self._nodes)
        units=[None]*len(self._nodes)
        for i,n in enumerate(self._nodes):
            data=n.getHistory(stateName)
            if i==0: times=data.getTimes()
            units[i]=data.getUnits()[0]
            values[i]=data.getValues()[0][0]
        return TimeSeriesImpl(times,[values],units)

    def learn(self,learn_term,mod_term,rate,**kwargs):
        in_args = {'a2Minus':  1.0e-1,
                   'a3Minus':  5.0e-3,
                   'tauMinus': 120.0,
                   'tauX':     140.0}
        for key in in_args.keys():
            if kwargs.has_key(key):
                in_args[key] = kwargs[key]
        
        out_args = {'a2Plus':  1.0e-2,
                    'a3Plus':  5.0e-8,
                    'tauPlus': 3.0,
                    'tauY':    150.0}
        for key in out_args.keys():
            if kwargs.has_key(key):
                out_args[key] = kwargs[key]

        for n in self._nodes:
            if( learn_term in [node.name for node in n.getTerminations()] ):
                term = n.getTermination(learn_term)

                if isinstance(term,STDPTermination):
                    inFcn = InSpikeErrorFunction([neuron.scale for neuron in n.nodes],n.encoders,
                                                 in_args['a2Minus'],in_args['a3Minus'],in_args['tauMinus'],in_args['tauX']);
                    inFcn.setLearningRate(rate)
                    outFcn = OutSpikeErrorFunction([neuron.scale for neuron in n.nodes],n.encoders,
                                                   out_args['a2Plus'],out_args['a3Plus'],out_args['tauPlus'],out_args['tauY']);                outFcn.setLearningRate(rate)
                    term.init(inFcn,outFcn,'AXON',mod_term)
                    
                    if kwargs.has_key('decay') and kwargs['decay'] is not None:
                        term.setDecaying(True)
                        term.setDecayScale(kwargs['decay'])
                    else:
                        term.setDecaying(False)
                    
                    if kwargs.has_key('homeostasis') and kwargs['homeostasis'] is not None:
                        term.setHomestatic(True)
                        term.setStableVal(kwargs['homeostasis'])
                    else:
                        term.setHomestatic(False)
                
                elif isinstance(term,PESTermination):
                    oja = True
                    if kwargs.has_key('oja'):
                        oja = kwargs['oja']
                    
                    term.setLearningRate(rate)
                    term.setOja(oja)
                    term.setOriginName('X')
                    term.setModTermName(mod_term)

    def setLearning(self,learn):
        for n in self._nodes:
            n.setLearning(learn)

    def releaseMemory(self):
        for n in self._nodes:
            n.releaseMemory()
            
    def getEncoders(self):  
        neurons=self.nodes[0].neurons
        encoders=MU.zero(self.neurons,self.dimension)
        for i,n in enumerate(self.nodes):
            MU.copyInto(n.encoders,encoders,i*neurons,i*n.dimension,neurons)       
        return encoders            
            
