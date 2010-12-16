from ca.nengo.model.impl import NetworkImpl, EnsembleTermination, PreciseSpikeOutputImpl, SpikeOutputImpl, RealOutputImpl
from ca.nengo.model import StructuralException, Origin
from ca.nengo.util.impl import TimeSeriesImpl


class ArrayOrigin(Origin):
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
        of the ensembles, which are then grouped together."""
        terminations=[n.addTermination(name,matrix[i],tauPSC,isModulatory) for i,n in enumerate(self._nodes)]  
        termination=EnsembleTermination(self,name,terminations)
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

