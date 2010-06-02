from ca.nengo.model.impl import NetworkImpl, EnsembleOrigin, EnsembleTermination
from ca.nengo.model import StructuralException
from ca.nengo.util.impl import TimeSeriesImpl


class NetworkArray(NetworkImpl):
    """Collects a set of NEFEnsembles into a single network."""
    serialVersionUID=1
    def __init__(self,name,nodes):
        """Create a network holding an array of the given nodes."""        
        NetworkImpl.__init__(self)
        self.name=name
        self.dimension=len(nodes)
        self._nodes=nodes
        self._origins={}
        for n in nodes:
            self.addNode(n)
        self.multidimensional=nodes[0].dimension>1
        self.createEnsembleOrigin('X')
    def createEnsembleOrigin(self,name):
        self._origins[name]=EnsembleOrigin(self,name,[n.getOrigin(name) for n in self._nodes])
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
        if self.multidimensional:
            terminations=[n.addDecodedTermination(name,matrix[i],tauPSC,isModulatory) for i,n in enumerate(self._nodes)]  
        else:
            terminations=[n.addDecodedTermination(name,[matrix[i]],tauPSC,isModulatory) for i,n in enumerate(self._nodes)]  
        termination=EnsembleTermination(self,name,terminations)
        self.exposeTermination(termination,name)
        return self.getTermination(name)
        
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

