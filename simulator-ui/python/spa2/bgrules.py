import inspect
import numeric

class Match:
    def __init__(self,a,b,weight):
        self.a=a
        self.b=b
        self.weight=weight

class Sink:
    def __init__(self,name,weight=1,conv=None,add=None):
        self.name=name
        self.weight=weight
        self.conv=conv
        self.add=add
    def __add__(self,other):
        assert self.add is None
        return Sink(self.name,self.weight,self.conv,other)
    def __sub__(self,other):
        if isinstance(other,str):
            return self.__add__('-(%s)'%other)
        else:
            return self.__add__(self,-other)
    def __mul__(self,other):
        if isinstance(other,(float,int)):
            return Sink(self.name,self.weight*other,self.conv,self.add)
        elif isinstance(other,str):
            conv=other
            if self.conv is not None: conv='(%s)*(%s)'%(conv, self.conv)
            return Sink(self.name,self.weight,conv,self.add)
        elif isinstance(other,Sink):
            assert self.conv is None
            return Sink(self.name,self.weight,other,self.add)
    def __invert__(self):
        name=self.name
        if name.startswith('~'): name=name[1:]
        else: name='~'+name
        return Sink(name,self.weight,self.conv,self.add)
    def __neg__(self):
        return self.__mul__(-1)

    def __eq__(self,other):
        assert isinstance(other,Sink)
        assert self.conv is None
        assert other.conv is None
        assert self.add is None
        assert other.add is None
        return Match(self.name,other.name,1*self.weight*other.weight)
    def __ne__(self,other):
        assert isinstance(other,Sink)
        assert self.conv is None
        assert other.conv is None
        assert self.add is None
        assert other.add is None
        return Match(self.name,other.name,-1*self.weight*other.weight)
        
        


class Rule:
    def __init__(self,func,spa):
        self.func=func
        self.name=func.func_name
        args,varargs,keywords,defaults=inspect.getargspec(func)
        #if defaults is None or args is None or len(args)!=len(defaults):
        #    raise Exception('No value specified for match in rule '+self.name)
        self.scale=1.0

        globals={}

        self.lhs={}
        for i in range(len(args)):
            if args[i]=='scale':
                self.scale=defaults[i]
            elif callable(defaults[i]):
                globals[args[i]] = defaults[i]
            else:
                if args[i] not in spa.sources.keys():
                    print 'Warning: unknown source "%s" in rule %s'%(args[i],self.name)
                self.lhs[args[i]]=defaults[i]

        self.rhs_direct={}
        self.rhs_route={}
        self.rhs_route_conv={}
        self.lhs_match={}
        self.rhs_learn={}
        self.learn_error=[]

        for k in spa.sinks.keys():
            globals[k]=Sink(k)
        for k in spa.sources.keys():
            if k not in globals.keys():
                globals[k]=Sink(k)
        globals['set']=self.set
        globals['match']=self.match
        globals['learn']=self.learn
        eval(func.func_code,globals)

    def set(self,**args):
        for k,v in args.items():
            while v is not None:
                if isinstance(v,str):
                    if self.rhs_direct.has_key(k):
                        self.rhs_direct[k]='(%s)+(%s)'%(self.rhs_direct,v)
                    else:
                        self.rhs_direct[k]=v
                    v=None
                elif isinstance(v,Sink):
                    if isinstance(v.conv, Sink):
                        w = self.rhs_route_conv.get((v.name,k,v.conv.name),0)
                        self.rhs_route_conv[v.name,k,v.conv.name] = v.weight + w
                    else:
                        w=self.rhs_route.get((v.name,k,v.conv),0)
                        self.rhs_route[v.name,k,v.conv]=v.weight+w
                    v=v.add
    def match(self,*args):
        for m in args:
            assert isinstance(m,Match)
            self.lhs_match[m.a,m.b]=m.weight
    
    def learn(self, **args):
        for k,v in args.items():
            if( k == 'pred_error' ):
                self.learn_error.append(v)
            else:
                self.rhs_learn[k] = v
        if( len(self.learn_error) == 0 ):
            raise Exception('No prediction error source set for learn in rule %s' % self.name)

class Rules:
    def __init__(self,ruleclass):
        self.rules={}
        self.names=[]
        self.rule_count=0
        self.ruleclass=ruleclass
        self.spa=None
        for name,func in inspect.getmembers(ruleclass):
            if inspect.ismethod(func):
                if not name.startswith('_'):
                    self.rule_count+=1

    def count(self):
        return self.rule_count
        
    def initialize(self,spa):
        if self.spa is not None: return     # already initialized
        self.spa=spa
        for name,func in inspect.getmembers(self.ruleclass):
            if inspect.ismethod(func):
                if not name.startswith('_'):
                    self.rules[name]=Rule(func,spa)
                    self.names.append(name)

    def lhs(self,source_name):
        m=[]
        dim=None
        for n in self.names:
            rule=self.rules[n]
            row=rule.lhs.get(source_name,None)
            if isinstance(row,str):
                vocab=self.spa.sources[source_name]
                row=vocab.parse(row).v*rule.scale
            m.append(row)
            if row is not None:                
                if dim is None: dim=len(row)
                elif len(row)!=dim:
                    raise Exception('Rows of different lengths connecting from %s'%source_name)
        if dim is None: return None
        for i in range(len(m)):
            if m[i] is None: m[i]=[0]*dim
            
        return numeric.array(m)
            

    def rhs_direct(self,sink_name):
        t=[]
        vocab=self.spa.sinks[sink_name]
        for n in self.names:
            rule=self.rules[n]
            row=rule.rhs_direct.get(sink_name,None)
            if row is None: row=[0]*vocab.dimensions
            else: row=vocab.parse(row).v
            t.append(row)
        return numeric.array(t).T

    def get_rhs_routes(self):
        routes=[]
        for rule in self.rules.values():
            for (source,sink,conv),w in rule.rhs_route.items():
                k=(source,sink,conv,w)
                if k not in routes: routes.append(k)
        return routes

    def get_rhs_route_convs(self):
        routes=[]
        for rule in self.rules.values():
            for (source,sink,conv),w in rule.rhs_route_conv.items():
                k=(source,sink,conv,w)
                if k not in routes: routes.append(k)
        return routes
        
        
    def get_lhs_matches(self):
        match=[]
        for rule in self.rules.values():
            for k in rule.lhs_match.keys():
                if k not in match:match.append(k)
        return match

    def rhs_route(self,source,sink,conv,weight):
        t=[]
        vocab=self.spa.sinks[sink]
        for n in self.names:
            rule=self.rules[n]
            if rule.rhs_route.get((source,sink,conv),None)==weight:
                t.append([-1])
            else:
                t.append([0])
        return numeric.array(t).T

    def rhs_route_conv(self,source,sink,conv,weight):
        t=[]
        vocab=self.spa.sinks[sink]
        for n in self.names:
            rule=self.rules[n]
            if rule.rhs_route_conv.get((source,sink,conv),None)==weight:
                t.append([-1])
            else:
                t.append([0])
        return numeric.array(t).T

        
    def lhs_match(self,a,b):
        t=[]
        for n in self.names:
            rule=self.rules[n]
            t.append(rule.lhs_match.get((a,b),0)*rule.scale)
        return t
        
    def get_learns(self, sink_name):
        transforms = []
        indexes = []
        pred_errors = []
        for i,n in enumerate(self.names):
            rule = self.rules[n]
            for source,transform in rule.rhs_learn.items():
                if( source == sink_name ):
                    indexes.append(i)
                    transforms.append(transform)
                    for pred_error in rule.learn_error:
                        if( not pred_error in pred_errors ):    
                            pred_errors.append(pred_error)
        return (indexes, transforms, pred_errors)
