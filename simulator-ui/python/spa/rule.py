import inspect
import numeric

class Rule:
    def __init__(self,func):
        self.func=func
        self.name=func.func_name
        args,varargs,keywords,defaults=inspect.getargspec(func)
        if len(args)!=len(defaults):
            raise Exception('No value specified for math in rule '+self.name)

        self.lhs={}
        for i in range(len(args)):
            self.lhs[args[i]]=defaults[i]

        self.rhs_set={}
        eval(func.func_code,dict(set=self.set))

    def set(self,**args):
        for k,v in args.items():
            self.rhs_set[k]=v
        
        


class Rules:
    def __init__(self):
        self._rules={}
        self._names=[]
        for name,func in inspect.getmembers(self):
            if inspect.ismethod(func):
                if not name.startswith('_'):
                    self._rules[name]=Rule(func)
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
            
