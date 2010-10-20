
from ca.nengo.math.impl import AbstractFunction


# keep the functions outside of the class, since they can't be serialized in the
#  current version of Jython

transientFunctions={}
cache={}

class PythonFunction(AbstractFunction):
    serialVersionUID=1
    def __init__(self,func,dimensions=1,time=False,index=None,use_cache=False):
        AbstractFunction.__init__(self,dimensions)
        transientFunctions[self]=func
        self.time=time
        self.index=index
        self.use_cache=use_cache
    def map(self,x):
        func=transientFunctions.get(self,None)
        if func is not None:
            if self.time: x=x[0]

            if self.use_cache:
                prev_x,prev_v=cache.get(func,(None,None))
                if prev_x==x:
                    v=prev_v
                else:
                    v=func(x)
                    cache[func]=x,v
            else:
                v=func(x)

            if self.index is not None:
                v=v[self.index]

            return v
        else:
            raise Exception('Python Functions are not kept when saving/loading networks')

    
