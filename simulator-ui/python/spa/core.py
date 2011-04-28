import nef
import hrr
import ca.nengo


class SPA:
    def __init__(self,name=None):
        if name is None: name=self.__class__.__name__
        self._net=nef.Network(name)
        self._net.add_to()
        self._sources={}
        self._sinks={}
        self._sink_parents={}
        self._components={}
        self._vocab={}

        self.structure()
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

    def set_vocab(self,name,vocab=None,aligned=False):
        if hasattr(name,'name'): name=name.name
        if vocab is None:
            dim=self.get_dimension(name)
            vocab=hrr.Vocabulary(dim,randomize=not aligned)
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
        
    def add(self,component):
        self._net.add(component)
        self._components[component.name]=component

        sources=component.getOrigins()
        for s in sources:
            self._sources['%s_%s'%(component.name,s.name)]=s
            if s.name=='value' or len(sources)==1:
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
        
        
            
