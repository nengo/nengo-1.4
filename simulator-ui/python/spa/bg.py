
import ca.nengo
import nef
import numeric

import spa.view
import spa.module
import spa.rule
import spa.match

class BasalGanglia(spa.module.Module):
    def __init__(self,rules,**params):
        spa.module.Module.__init__(self,**params)
        self.rules=spa.rule.Rules(rules)
        
    def create(self,lg=0.2,pstc_input=0.002):

        nef.templates.basalganglia.make(None,dimensions=self.rules.count(),netbg=self.net)
        self.net.network.hideOrigin('output')
        self.net.network.removeNode('output')
        self.net.network.hideTermination('input')
        self.net.network.removeNode('input')
        self.net.network.exposeOrigin(self.net.network.getNode('GPi').getOrigin('func_gpi'),'output')

        spa.view.utility_watch.add(self.net.network,self.rules.names)

    def connect(self):
        self.rules.initialize(self.spa)
        if len(self.rules.get_lhs_matches())>0:
            self.spa.add_module(self.name+'_match',spa.match.Match(self),create=True,connect=True)
            
        for name,source in self.spa.sources.items():
            self.add_input(source,self.rules.lhs(name))
        
    def add_input(self,origin,transform,learn=False):
        if transform is None: return

        lg=self.get_param('lg')
        pstc_input=self.get_param('pstc_input')
        
        o1,t1=self.net.connect(origin,self.net.network.getNode('StrD1'),transform=(1+lg)*numeric.array(transform),
                          pstc=pstc_input,plastic_array=learn,create_projection=False)
        tname=t1.name+'_D1'
        self.net.network.exposeTermination(t1,tname)
        self.spa.net.network.addProjection(o1,self.net.network.getTermination(tname))

        o1,t1=self.net.connect(origin,self.net.network.getNode('StrD2'),transform=(1-lg)*numeric.array(transform),
                          pstc=pstc_input,plastic_array=learn,create_projection=False)
        tname=t1.name+'_D2'
        self.net.network.exposeTermination(t1,tname)
        self.spa.net.network.addProjection(o1,self.net.network.getTermination(tname))

        o1,t1=self.net.connect(origin,self.net.network.getNode('STN'),transform=transform,
                          pstc=pstc_input,plastic_array=learn,create_projection=False)
        tname=t1.name+'_STN'
        self.net.network.exposeTermination(t1,tname)
        self.spa.net.network.addProjection(o1,self.net.network.getTermination(tname))



