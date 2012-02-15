
from ca.nengo.math.impl import AbstractFunction, PiecewiseConstantFunction


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


class Interpolator:
    def __init__(self,filename):
        self.data=[]
        N=None
        for line in open(filename):
            line=line.strip()
            if len(line)>0 and line[0]!='#':
                row=[float(x) for x in line.strip().split(',')]
                if N is None: N=len(row)
                while len(row)<N: row.append(0.0)  
                self.data.append(row)
    def load_into_function(self,input):
        funcs=[]
        for i in range(len(input.functions)):
            if i+1<len(self.data[0]):
                f=PiecewiseConstantFunction([x[0] for x in self.data],[0]+[x[i+1] for x in self.data])
            else:
                f=input.functions[i]    
            funcs.append(f)
        input.functions=funcs
                
    def __call__(self,t):
        value=None
        for row in self.data:
            if value is None or row[0]<=t:
                value=row[1:]
            else:
                break
        return value
            
