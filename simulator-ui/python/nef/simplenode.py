from ca.nengo.model import Termination, Origin, Probeable, Node, SimulationMode
from ca.nengo.model.impl import BasicOrigin, RealOutputImpl
#from ca.nengo.model.nef.impl import *
#from ca.nengo.model.neuron.impl import *
#from ca.nengo.math.impl import *
from ca.nengo.util import VisiblyMutableUtils
#from ca.nengo.util.impl import *
#from ca.nengo.sim.impl import *
#from ca.nengo.sim import *
import java
import inspect

class SimpleTermination(Termination):
    def __init__(self,name,node,func,tau=0,dimensions=1):
        self._name=name
        self._node=node
        self._tau=tau
        self._modulatory=False
        self._func=func
        self.setDimensions(dimensions)

    def getName(self):
        return self._name
    def setDimensions(self,dimensions):
        self._values=[0]*dimensions
        self._filtered_values=[0]*dimensions
        self._dimensions=dimensions
    def getDimensions(self):
        return self._dimensions
    def getNode(self):
        return self._node
    def getTau(self):
        return self._tau
    def setTau(self,tau):
        self._tau=tau
    def getModulatory(self):
        return self._modulatory
    def setModulatory(self,modulatory):
        self._modulatory=modulatory

    def setValues(self,values):
        self._values=values.values
    def getOutput(self):
        return self._values

    def reset(self):
        self._values = [0] * self._dimensions
        self._filtered_values = [0] * self._dimensions

    def run(self,start,end):
        dt=end-start
        v=self.getOutput()
        if self.tau<dt:
            self._filtered_values=v
        else:    
            for i in range(self._dimensions):
                self._filtered_values[i]*=(1.0-dt/self.tau)
                self._filtered_values[i]+=v[i]*dt/self.tau
        if self._dimensions==1:        
            self._func(self._filtered_values[0])
        else:
            self._func(self._filtered_values)
            

class SimpleOrigin(BasicOrigin):
    def __init__(self,name,node,func):
        BasicOrigin.__init__(self,node,name,len(func()),Units.UNK)
        self.func=func
    def run(self,start,end):
        self.values=RealOutputImpl(self.func(),Units.UNK,end)

class SimpleNode(Node,Probeable):
    pstc=0
    def __init__(self,name):
        self._name=name
        self.listeners=[]
        self._origins={}
        self._terminations={}
        self._states=java.util.Properties()
        self.setMode(SimulationMode.DEFAULT)
        self.t_start=0
        self.t_end=0

        for name,method in inspect.getmembers(self,inspect.ismethod):
            if name.startswith('origin_'):
                self.create_origin(name[7:],method)
            elif name.startswith('termination_'):
                self.create_termination(name[12:],method)

    def create_origin(self,name,func):
        self.addOrigin(SimpleOrigin(name,self,func))
    def create_termination(self,name,func):
        dimensions=1
        a,va,k,d=inspect.getargspec(func)
        if 'dimensions' in a:
            index=a.index('dimensions')-len(a)
            dimensions=d[index]
        self.addTermination(SimpleTermination(name,self,func,tau=self.pstc,dimensions=dimensions))
            
        
        
    def reset(self,randomize=False):
        for termination in self.getTerminations():
            termination.reset()
        
    def getName(self):
        return self._name
    def setName(self,name):
        VisiblyMutableUtils.nameChanged(self, self.getName(), name, self.listeners)
        self._name=name

    def addChangeListener(self,listener):
        self.listeners.append(listener)
    def removeChangeListener(self,listener):
        self.listeners.remove(listener)

    def tick(self):
        pass
    def run(self,start,end):
        if start<self.t_start: self.reset()
        self.t_start=start
        self.t_end=end
        for t in self.getTerminations():
            t.run(start,end)
        self.tick()    
        for o in self.getOrigins():
            o.run(start,end)

    def getOrigins(self):
        return self._origins.values()
    def getOrigin(self,name):
        return self._origins[name]
    def addOrigin(self,origin):
        self._origins[origin.name]=origin
        self._states.setProperty(origin.name,"data")
    def removeOrigin(self, name):
        del self._origins[name]
        self._states.remove(name)

    def setTau(self,name,tau):
        self._terminations[name].setTau(tau)
    def getTerminations(self):
        return self._terminations.values()
    def getTermination(self,name):
        return self._terminations[name]
    def addTermination(self,termination):
        self._terminations[termination.name]=termination
    def removeTermination(self, name):
        del self._terminations[name]

    def getDocumentation(self):
        return ""

    def clone(self):
        raise java.lang.CloneNotSupportedException()

    def setMode(self,mode):
        self._mode=mode
    def getMode(self):
        return self._mode

    def listStates(self):
        return self._states




