import os
import runner
import reader

from bootstrapci import bootstrapci

try:
    import numpy as np
except:
    import numeric as np    

class StatisticSample:
    def __init__(self,func,data):
        self.func=func
        self.data=data
    def __getattr__(self,key):
        if key.startswith('_'): return self.__dict__[key]    
        d=getattr(self.data,key)
        if len(d)==0: return None
        return self.func(d)

class StatisticPopulation:
    def __init__(self,func,data):
        self.func=func
        self.data=data
    def __getattr__(self,key):
        if key.startswith('_'): return self.__dict__[key]    
        d=getattr(self.data,key)
        if len(d)==0: return None,None,None
        low,high=bootstrapci(d,self.func)
        return low,self.func(d),high


class Data:
    def __init__(self,dir=None,readers=None):
        if readers is not None:
            self.readers=readers
        elif not os.path.exists(dir):
            self.readers=[]
        else:
            self.readers=[reader.Reader(fn,dir) for fn in sorted(os.listdir(dir)) if fn.endswith('.csv')]
    def __len__(self):
        return len(self.readers)         
    def __getattr__(self,key):
        if key.startswith('_'): return self.__dict__[key]
    
        d=[getattr(r,key) for r in self.readers]
        d=np.array(d)
        #if d.shape[-1]==1: d.shape=d.shape[:-1]   # TODO: should we do this collapse?  Should it happen in Reader instead?
        return d
    def __getitem__(self,key):
        return self.readers[key]
        #return Data(readers=[self.readers[key]])
    def __getslice__(self,i,j):
        return Data(readers=self.readers[i:j])        

class ArrayProxy:
    def __init__(self,items,depth=2):
        self._items=items
        self._depth=depth
    def __getattr__(self,key):
        if key.startswith('_'): return self.__dict__[key]
        values=[getattr(item,key) for item in self._items]
        if self._depth<=1:
            return np.array(values)
        else:
            return ArrayProxy(values,self._depth-1)
    def __call__(self,*args,**keys):
        return [item(*args,**keys) for item in self._items]        

def mean(x):
    return np.mean(x,axis=0) 
def std(x):
    return np.std(x,axis=0)           
        
class Stats:
    def __init__(self,name,parent=None,settings=None):
        self.name=name
        
        if parent is not None:
            self.params=parent.params
            self.settings=dict(parent.settings)
            self.defaults=parent.defaults
            for k,v in settings.items():
                self.settings[k]=`v`
        else:
            if name.endswith('.py'): name=name[:-3]
            if os.path.exists(name+'.py'):
                lines=open(name+'.py').readlines()
            elif os.path.exists(os.path.join(name,'code.py')):
                lines=open(os.path.join(name,'code.py')).readlines()
            self.params,self.defaults,self.core_code=runner.parse_code(lines)

            self.settings={}
            for k,v in self.defaults.items():
                self.settings[k]=v

        self.data=Data('%s/%s'%(self.name,runner.make_param_text(self.params,self.defaults,self.settings)))
        self.mean=StatisticPopulation(mean,self.data)
        self.mean_sample=StatisticSample(mean,self.data)
        self.sd=StatisticPopulation(std,self.data)
        self.sd_sample=StatisticSample(std,self.data)
    def compute(self,func):
        for reader in self.data.readers:
            func(reader)    
    def __call__(self,**params):
        for k in self.params:
            if k in params and isinstance(params[k],list):
                items=[]
                for vv in params[k]:
                    params[k]=vv
                    items.append(self(**params))
                return ArrayProxy(items)
        return Stats(self.name,self,params)
    def __getattr__(self,key):
        if key=='options' and key not in self.__dict__:
            self.__dict__['options']=Options(self)
        return self.__dict__[key]    

        
class Options:
    def __init__(self,stats):
        values={}
        for k,v in stats.defaults.items():
            values[k]=set()   
            values[k].add(v)
        if os.path.exists(stats.name):
            for f in os.listdir(stats.name):
                if '=' in f:
                    for part in f.split(' '):
                        if '=' in part:
                            k,v=part.split('=',1)
                            if k in values:
                                values[k].add(v)
        for k,v in values.items():
            values[k]=sorted([eval(vv) for vv in v])
            setattr(self,k,list(sorted([eval(vv) for vv in v])))
            
