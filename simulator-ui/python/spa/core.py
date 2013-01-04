import nef
import hrr
import ca.nengo


import spa.module
import inspect
class SPA:
    dimensions=16
    align_hrrs=False
    subdimensions=None
    
    def __init__(self,name=None, vocabs=[]):
        if name is None: name=self.__class__.__name__

        self.net=nef.Network(name)
        self.sources={}
        self.sinks={}
        self.sink_modules={}
        self.source_modules={}
        self.vocabs={}
        self.default_vocabs={}
        for v in vocabs:
            self.default_vocabs[(v.dimensions,not v.randomize)]=v        
        self.params={}
        self.modules={}

        self.init()
        self.create()
        self.connect()
        self.net.add_to_nengo()

    def has_sink(self,k):
        return self.sinks.has_key(k)
    def has_source(self,k):
        return self.sources.has_key(k)

    def add_source(self,name,source,module):
        self.sources[name]=source
        if source.dimensions>1:
            self.ensure_vocab(name,module)
        self.source_modules[name]=module

            
        
    def add_sink(self,name,sink,module):
        self.sinks[name]=sink
        self.sink_modules[name]=module

        self.ensure_vocab(name,module)

    def ensure_vocab(self,name,module):
        d=module.get_param('dimensions')
        align=module.get_param('align_hrrs')
        if not self.default_vocabs.has_key((d,align)):
            self.default_vocabs[(d,align)]=hrr.Vocabulary(d,randomize=not align,max_similarity=0.04)
        self.vocabs[name]=self.default_vocabs[(d,align)]
        

    def vocab(self,obj):
        return self.vocabs[obj]

    def add_module(self,name,module,create=False,connect=False):
        module.name=name
        module.spa=self
        module.net=nef.Network(name)
        self.net.add(module.net.network)
        self.modules[name]=module

        if create: self.create([module])
        if connect: self.connect([module])

    def init(self):
        for k,v in inspect.getmembers(self):
          if not k.startswith('_'):
            if isinstance(v,spa.module.Module):
                self.add_module(k,v)
            elif isinstance(v,(int,float,str)):
                self.params[k]=v
        
    def create(self,modules=None):
        if modules is None: modules=self.modules.values()
        for m in modules:
            args,vargs,kw,defaults=inspect.getargspec(m.create)
            a=[m.get_param(arg) for arg in args[1:]]
            m.create(*a)
    def connect(self,modules=None):
        if modules is None: modules=self.modules.values()
        for m in modules:
            m.connect()

    def connect_to_sink(self,origin,sink_name,transform,pstc,termination_name=None):
        sink=self.sinks[sink_name]
        o,t=self.net.connect(origin,sink,transform=transform,pstc=pstc,
                             create_projection=False)
        if termination_name is None: termination_name=o.node.name
        if self.sink_modules[sink_name].name!=sink_name:
            termination_name=termination_name+'_'+sink_name
        self.sink_modules[sink_name].net.network.exposeTermination(t,termination_name)
        self.net.network.addProjection(o,self.sink_modules[sink_name].net.network.getTermination(termination_name))
        
    def getModuleOrigin(self, module, origin_name):
        if( isinstance(module, str) ):
            module_name = module
        else:
            module_name = module.name
        return self.modules[module_name].net.network.getOrigin(origin_name)
        
    def getModuleTermination(self, module, term_name):
        if( isinstance(module, str) ):
            module_name = module
        else:
            module_name = module.name
        return self.modules[module_name].net.network.getTermination(term_name)
        

"""
class SPA:
    def __init__(self,name=None):
        if name is None: name=self.__class__.__name__
        self._net=nef.Network(name)
        self._net.add_to()
        self._scalar_sources={}
        self._sources={}
        self._sinks={}
        self._sink_parents={}
        self._components={}
        self._vocab={}
        self._vocab_default={}

        self.structure()
        self.initialize()
        self.connect()
    def __setattr__(self,k,v):
        self.__dict__[k]=v
        if not k.startswith('_') and isinstance(v,ca.nengo.model.Node):
            if hasattr(v,'setName'): v.setName(k)
            self.add(v)
    def structure(self):
        pass
    def connect(self):
        for c in self._components.values():
            if hasattr(c,'connect_NCA'):
                c.connect_NCA(self)
    def initialize(self):
        for c in self._components.values():
            if hasattr(c,'init_NCA'):
                c.init_NCA(self)

    def set_vocab(self,name,vocab=None,aligned=False):
        if hasattr(name,'name'): name=name.name
        if vocab is None:
            dim=self.get_dimension(name)
            if not self._vocab_default.has_key(dim):
                self._vocab_default[dim]=hrr.Vocabulary(dim,randomize=not aligned)
            vocab=self._vocab_default[dim]
        self._vocab[name]=vocab

    def get_dimension(self,name):
        if name in self._sources.keys():
            return self._sources[name].dimensions
        if name in self._sinks.keys():
            return self._sinks[name].dimensions
        raise Exception('Unknown name: '+name)
        
                
    def vocab(self,name):
        if name not in self._vocab.keys():
            self.set_vocab(name)
        return self._vocab[name]

    def add_scalar_source(self,name,origin,transform=None):
        self._scalar_sources[name]=origin,transform
        
    def add(self,component):
        self._net.add(component)
        self._components[component.name]=component

        sources=component.getOrigins()
        for s in sources:
            if s.name=='value':
                sname=component.name
            else:
                sname='%s_%s'%(component.name,s.name)
                
            if s.dimensions==1:
                self.add_scalar_source(sname,s)
            else:
                self._sources[component.name]=s

        if hasattr(component,'get_sinks'):
            items=component.get_sinks().items()
            for k,v in items:
                self._sinks['%s_%s'%(component.name,k)]=v
                self._sink_parents['%s_%s'%(component.name,k)]=component
            if len(items)==1:
                self._sinks[component.name]=v
                self._sink_parents[component.name]=component
        elif hasattr(component,'addDecodedTermination'):
            self._sinks[component.name]=component
            self._sink_parents[component.name]=component

    def connect_to_sink(self,origin,key,transform,pstc):
        o,t=self._net.connect(origin,self._sinks[key],
                         transform=transform,create_projection=False,pstc=pstc)
        if self._sink_parents[key] is not self._sinks[key]:
            self._sink_parents[key].exposeTermination(t,t.name)
        self._net.network.addProjection(o,self._sink_parents[key].getTermination(t.name))
        
   """     
            
