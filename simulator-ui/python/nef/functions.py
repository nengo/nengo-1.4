from ca.nengo.math.impl import AbstractFunction, PiecewiseConstantFunction
import inspect
import tokenize
import StringIO

class PythonFunction(AbstractFunction):
    serialVersionUID=1
    def __init__(self,func,dimensions=1,time=False,index=None,use_cache=False):
        AbstractFunction.__init__(self,dimensions)
        PythonFunctionCache.transientFunctions[self]=func
        self.time=time
        self.index=index
        self.use_cache=use_cache

        code = inspect.getsource(func)
        self.simple=self.checkIfSimple(code)
        
        if self.simple:
          self.setCode(code)
          self.setName(func.func_name)

    def checkIfSimple(self, code):
        #S = StringIO.StringIO(code)
        #T = tokenize.tokenize(S.readline)
        #now we have a bunch of tokens, now what to do with them?
        
        return True

    def map(self,x):
        func=PythonFunctionCache.transientFunctions.get(self,None)
        if func is not None:
            if self.time: x=x[0]

            if self.use_cache:
                prev_x,prev_v=PythonFunctionCache.cache.get(func,(None,None))
                if prev_x==x:
                    v=prev_v
                else:
                    v=func(x)
                    PythonFunctionCache.cache[func]=x,v
            else:
                v=func(x)

            if self.index is not None:
                v=v[self.index]

            return v
        else:
            raise Exception('Python Functions are not kept when saving/loading networks')


class Interpolator:
    def __init__(self,data):
        self.data=[]
        N=None
        
        if isinstance(data,str):
            self.init_from_file(data)
        else:
            self.init_from_dict(data)
            
    def init_from_dict(self,data):        
        length=None
        for k,v in sorted(data.items()):
            if not hasattr(v,'__len__'):
                v=[v]
            if length is not None and length!=len(v):
                raise Exception('invalid data for defining function (time %4g has %d items instead of %d)'%(k,len(v),length))
            length=len(v)   
            row=[float(x) for x in [k]+list(v)]
            self.data.append(row)
            
    def init_from_file(self,filename):
        for line in open(filename):
            line=line.strip()
            if len(line)>0 and line[0]!='#':
                row=[float(x) for x in line.strip().split(',')]
                if N is None: N=len(row)
                while len(row)<N: row.append(0.0)  
                self.data.append(row)
    def create_function_list(self):
        funcs=[]
        for i,d in enumerate(self.data):
            print i,len(d),d
        
        for i in range(len(self.data[0])-1):
            f=PiecewiseConstantFunction([x[0] for x in self.data],[0]+[x[i+1] for x in self.data])
            funcs.append(f)
        return funcs        
                
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
            
#if these are within the PythonFunction class it causes problems with serialization
class PythonFunctionCache:
    transientFunctions={}
    cache={}
