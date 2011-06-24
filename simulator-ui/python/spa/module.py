import inspect

class Params:
    def __init__(self,module):
        self.module=module
    def __getattr__(self,key):
        return self.module.get_param(key)

class Module:
    def __init__(self,**params):
        self.params=params
        self.p=Params(self)
    def create(self):
        pass
    def connect(self):
        pass
    def init(self):
        pass
    def add_source(self,origin,name=None):
        if name is None: name=self.name
        else: name='%s_%s'%(self.name,name)
        self.net.network.exposeOrigin(origin,name)
        self.spa.add_source(name,self.net.network.getOrigin(name),self)
    def add_sink(self,node,name=None):
        if name is None: name=self.name
        else: name='%s_%s'%(self.name,name)
        self.spa.add_sink(name,node,self)
    def has_param(self,key):
        if self.params.has_key(key):
            return True
        if self.spa.params.has_key(key):
            return True
        args,vargs,kw,defaults=inspect.getargspec(self.create)
        if key in args:
            i=args.index(key)+len(defaults)-len(args)
            if i>=0:
                return True
        return False
        
    def get_param(self,key):
        if self.params.has_key(key):
            return self.params[key]
        if self.spa.params.has_key(key):
            return self.spa.params[key]
        args,vargs,kw,defaults=inspect.getargspec(self.create)
        if key in args:
            i=args.index(key)+len(defaults)-len(args)
            if i>=0:
                return defaults[i]
        raise Exception('Module %s=%s requires parameter %s'%(self.name,self.__class__.__name__,key))
    def connect_to_sink(self,origin,sink_name,transform,pstc):
        self.net.network.exposeOrigin(origin,origin.name)
        self.spa.connect_to_sink(self.net.network.getOrigin(origin.name),origin.name,
                                 transform,pstc)
        
    def exposeTermination(self, term, name):
        self.net.network.exposeTermination(term, name)
    
    def exposeOrigin(self, origin, name):
        self.net.network.exposeOrigin(origin, name)