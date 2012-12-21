import nef
import hrr

import module

import inspect

class SPA:
    randomize_vectors=True

    def __init__(self,network):
        self.net=network
        self.params={}
        self.modules={}
        self.vocabs={}
        self.sinks={}
        self.sources={}
        self.init()
        self.connect()
        
    def init(self):
        for k,v in inspect.getmembers(self):
          if not k.startswith('_'):
            if isinstance(v,module.Module):
                self.add_module(k,v)
            elif isinstance(v,(int,float,str)):
                self.params[k]=v
    
    def add_module(self, name, module):
        self.modules[name]=module
        net=self.net.make_subnetwork(name)
        module.net = net
        module.spa = self
        module.name = name
        module.init()

    def connect(self):
        for module in self.modules.values():
            module.connect()

    
    def get_module_name(self, module):
        for k,v in self.modules.items():
            if v is module: return k
        raise KeyError('Module %s not found'%module)    
        
    def get_param_value(self, param, module):
        if param in module.params: return module.params[param]
        if param in self.params: return self.params[param]
        raise KeyError('No parameter %s found in %s'%(param, module))
        
        
    def add_sink(self, module, node, name=None, vocab=None):
        module_name = self.get_module_name(module)
        if name is None: 
            name = module_name
        if vocab is None:
            dims = self.get_param_value('dimensions', module)
            rand = self.get_param_value('randomize_vectors', module)
            key=(dims, rand)
            vocab=self.vocabs.get(key, None)
            if vocab is None:
                vocab=hrr.Vocabulary(dims, randomize=rand)
                self.vocabs[key]=vocab
        alias='sink_%s'%name
        self.net.set_alias(alias, '%s.%s'%(module_name, node))
        self.sinks[name]=vocab

    def add_source(self, module, node, name=None, vocab=None):
        module_name = self.get_module_name(module)
        if name is None: 
            name = module_name
        if vocab is None:
            dims = self.get_param_value('dimensions', module)
            rand = self.get_param_value('randomize_vectors', module)
            key=(dims, rand)
            vocab=self.vocabs.get(key, None)
            if vocab is None:
                vocab=hrr.Vocabulary(dims, randomize=rand)
                self.vocabs[key]=vocab
        alias='source_%s'%name
        self.net.set_alias(alias, '%s.%s'%(module_name, node))
        self.sources[name]=vocab
            
        
    
