import inspect
import numeric

class Sink:
    def __init__(self,name,weight=None):
        self.name=name
        self.weight=weight
    def __mul__(self,other):
        assert isinstance(other,Sink)   # haven't coded these yet
        assert self.weight==None        # haven't coded these yet
        if self.weight is None:
            w=other
        elif isinstance(self.weight,str) or isinstance(other,str):
            w='%s*%s'%(self.weight,other)
        else:
            w=self.weight*other        
        return Sink(self.name,weight=w)
    def __invert__(self):
        return Sink('~'+self.name,weight=self.weight)
    def __div__(self,other):
        assert isinstance(other,Sink)   # haven't coded these yet
        assert self.weight==None        # haven't coded these yet
        return Sink(self.name,weight=Sink('~'+other.name))        
        

class Rule:
    def __init__(self,func,sources,sinks):
        self.func=func
        self.name=func.func_name
        args,varargs,keywords,defaults=inspect.getargspec(func)
        if defaults is None or args is None or len(args)!=len(defaults):
            raise Exception('No value specified for match in rule '+self.name)

        self.lhs={}
        for i in range(len(args)):
            self.lhs[args[i]]=defaults[i]

        self.rhs_set={}
        self.rhs_route={}
        globals={}
        for k in sources:
            globals[k]=Sink(k)
        globals['set']=self.set
        globals['route']=self.route
        eval(func.func_code,globals)

    def route(self,**args):
        for k,v in args.items():
            if isinstance(v.weight,Sink):
                self.rhs_route[v.name,v.weight.name,k]=v.weight.weight
            else:
                self.rhs_route[v.name,k]=v.weight
        
    def set(self,**args):
        for k,v in args.items():
            self.rhs_set[k]=v
        
        


class Rules:
    def __init__(self):
        self._rules={}
        self._names=[]
        self._rule_count=0
        for name,func in inspect.getmembers(self):
            if inspect.ismethod(func):
                if not name.startswith('_'):
                    self._rule_count+=1
        
    def _initialize(self,sources,sinks):
        for name,func in inspect.getmembers(self):
            if inspect.ismethod(func):
                if not name.startswith('_'):
                    self._rules[name]=Rule(func,sources=sources,sinks=sinks)
                    self._names.append(name)
    def _lhs_keys(self):
        keys=[]
        for r in self._rules.values():
            for k in r.lhs.keys():
                if k not in keys: keys.append(k)
        return keys
    def _rhs_set_keys(self):
        keys=[]
        for r in self._rules.values():
            for k in r.rhs_set.keys():
                if k not in keys: keys.append(k)
        return keys
    def _rhs_route_keys(self):
        keys=[]
        for r in self._rules.values():
            for k in r.rhs_route.keys():
                if k not in keys and len(k)==2 and r.rhs_route[k] is None: keys.append(k)
        return keys
    def _rhs_route_conv_keys(self):
        keys=[]
        for r in self._rules.values():
            for k in r.rhs_route.keys():
                if k not in keys and isinstance(r.rhs_route[k].weight,str): keys.append(k)
        return keys
    def _rhs_route_conv2_keys(self):
        keys=[]
        for r in self._rules.values():
            for k in r.rhs_route.keys():
                if k not in keys and len(k)==3: keys.append(k)
        return keys

    def _make_lhs_transform(self,key,vocab):
        t=[]
        for n in self._names:
            lhs=self._rules[n].lhs
            if key not in lhs: t.append([0]*vocab.dimensions)
            else: t.append(vocab.parse(lhs[key]).v)
        return t

    def _make_rhs_set_transform(self,key,vocab):
        t=[]
        for n in self._names:
            rhs=self._rules[n].rhs_set
            if key not in rhs: t.append([0]*vocab.dimensions)
            else: t.append(vocab.parse(rhs[key]).v)
        return numeric.array(t).T

    def _make_lhs_scalar_transform(self,key):
        t=[]
        for n in self._names:
            lhs=self._rules[n].lhs
            if key not in lhs: t.append([0])
            else: t.append([lhs[key]])
        return t
            
    def _make_rhs_route_transform(self,key1,key2,key3=None):
        t=[]
        for n in self._names:
            rhs=self._rules[n].rhs_route
            if key3 is not None:
                k=(key1,key2,key3)
            else:
                k=(key1,key2)
            if not rhs.has_key(k):
                w=0
            else:
                v=rhs[k]
                if v==None: w=-1
                else: w=0                
            t.append([w])
        return numeric.array(t).T

    def _make_rhs_route_conv_transform(self,key1,key2,vocab):
        t=[]
        for n in self._names:
            rhs=self._rules[n].rhs_route
            k=(key1,key2)
            if not rhs.has_key(k):
                w=[0]*vocab.dimensions
            else:
                v=rhs[k]
                if isinstance(v,str):
                    w=vocab.parse(v).v
                else:
                    w=[0]*vocab.dimensions                
            t.append([w])
        return numeric.array(t).T
