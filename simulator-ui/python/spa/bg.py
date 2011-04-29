lg=0.2
pstc_input=0.002

import ca.nengo
import nef
import numeric

class BasalGanglia(ca.nengo.model.impl.NetworkImpl):
    def __init__(self,rules,name='BG'):
        ca.nengo.model.impl.NetworkImpl.__init__(self)
        self.name=name
        self.rules=rules
        net=nef.Network(self)
        nef.templates.basalganglia.make(None,dimensions=len(self.rules._rules),netbg=net)
        self.hideOrigin('output')
        self.removeNode('output')
        self.hideTermination('input')
        self.removeNode('input')
        self.exposeOrigin(self.getNode('GPi').getOrigin('func_gpi'),'output')
    def add_input(self,nca,origin,transform,learn=False):
        net=nef.Network(self)

        o1,t1=net.connect(origin,self.getNode('StrD1'),transform=(1+lg)*numeric.array(transform),
                          pstc=pstc_input,plastic_array=learn,create_projection=False)
        tname=t1.name+'_D1'
        self.exposeTermination(t1,tname)
        nca._net.network.addProjection(o1,self.getTermination(tname))

        o1,t1=net.connect(origin,self.getNode('StrD2'),transform=(1-lg)*numeric.array(transform),
                          pstc=pstc_input,plastic_array=learn,create_projection=False)
        tname=t1.name+'_D2'
        self.exposeTermination(t1,tname)
        nca._net.network.addProjection(o1,self.getTermination(tname))

        o1,t1=net.connect(origin,self.getNode('STN'),transform=transform,
                          pstc=pstc_input,plastic_array=learn,create_projection=False)
        tname=t1.name+'_STN'
        self.exposeTermination(t1,tname)
        nca._net.network.addProjection(o1,self.getTermination(tname))

        
    def connect_NCA(self,nca):
        for k in self.rules._lhs_keys():
            if k in nca._sources.keys():
                self.add_input(nca,nca._sources[k],self.rules._make_lhs_transform(k,nca.vocab(k)))
            elif k in nca._scalar_sources.keys():
                s,t1=nca._scalar_sources[k]
                t=self.rules._make_lhs_scalar_transform(k)
                if t1 is not None:
                    t=numeric.dot(t,t1)
                self.add_input(nca,s,t)
