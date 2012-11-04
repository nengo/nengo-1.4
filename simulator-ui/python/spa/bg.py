
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
        self.same_neurons=params.get("same_neurons",True)
        self.rules=spa.rule.Rules(rules)
        self.match=None
                
    def create(self,lg=0.2,pstc_input=0.002):

        nef.templates.basalganglia.make(None,dimensions=self.rules.count(),netbg=self.net,same_neurons=self.same_neurons)
        self.net.network.hideOrigin('output')
        self.net.network.removeNode('output')
        self.net.network.hideTermination('input')
        self.net.network.removeNode('input')
        self.net.network.exposeOrigin(self.net.network.getNode('GPi').getOrigin('func_gpi'),'output')

    def connect(self):
        self.rules.initialize(self.spa)

        # Store rules in the documentation comment for this network for use in the interactive mode view    
        self.net.network.documentation = 'BG: ' + ','.join(self.rules.names)
        
        if len(self.rules.get_lhs_matches())>0:
            self.match=spa.match.Match(self,pstc_match=self.p.pstc_input/2)
            self.spa.add_module(self.name+'_match',self.match,create=True,connect=True)
            
        for name,source in self.spa.sources.items():
            self.add_input(source,self.rules.lhs(name))
            self.add_index_input(source,self.rules.get_learns(name),learn=True)

    def add_input(self,origin,transform,learn=False):
        if transform is None: return

        lg=self.get_param('lg')
        pstc_input=self.get_param('pstc_input')
        if self.match is not None and origin.node is self.match.net.network:
            pstc_input=pstc_input/2
        
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


    def add_mod_input(self,origin,index_post=None,connect_D1=True,connect_D2=True,connect_STN=True):
        if( connect_D1 ):
            o1,t1 = self.net.connect(origin, self.net.get('StrD1'), index_post = index_post,
                                     pstc = self.get_param('pstc_input'), modulatory = True,
                                     create_projection = False)
            tname = t1.name + '_D1'
            self.net.network.exposeTermination(t1,tname)
            self.spa.net.network.addProjection(o1, self.net.network.getTermination(tname))

        if( connect_D2 ):
            o1,t1 = self.net.connect(origin, self.net.get('StrD2'), index_post = index_post,
                                     pstc = self.get_param('pstc_input'), modulatory = True,
                                     create_projection = False)
            tname = t1.name + '_D2'
            self.net.network.exposeTermination(t1,tname)
            self.spa.net.network.addProjection(o1, self.net.network.getTermination(tname))

        if( connect_STN ):
            o1,t1 = self.net.connect(origin, self.net.get('StrSTN'), index_post = index_post,
                                     pstc = self.get_param('pstc_input'), modulatory = True,
                                     create_projection = False)
            tname = t1.name + '_D2'
            self.net.network.exposeTermination(t1,tname)
            self.spa.net.network.addProjection(o1, self.net.network.getTermination(tname))


    def add_index_input(self,origin,(indexes,transforms,pred_errors),learn=False):
        if( len(indexes) == 0 ):
            return
        D1_matrix = []
        D2_matrix = []
        pstc = self.get_param('pstc_input')

        # Generate transformation matrix
        for i in range(len(indexes)):
            if( callable(transforms[i]) ):
                temp_matrix = [[0] * origin.getDimensions() for _ in range(self.net.get('StrD1').neurons / len(self.net.get('StrD1')._nodes))]
                temp_matrix = transforms[i](temp_matrix)
                D1_matrix.extend(temp_matrix)
                temp_matrix = [[0] * origin.getDimensions() for _ in range(self.net.get('StrD2').neurons / len(self.net.get('StrD2')._nodes))]
                temp_matrix = transforms[i](temp_matrix)
                D2_matrix.extend(temp_matrix)
            else:
                D1_matrix.extend(transforms[i])
                D2_matrix.extend(transforms[i])

        t1 = self.net.get('StrD1').addIndexTermination(origin.getName(), D1_matrix, pstc, index = indexes)
        t1name = origin.getName() + '_D1'
        self.net.network.exposeTermination(t1, t1name)
        self.spa.net.network.addProjection(origin, self.net.network.getTermination(t1name))

        t2 = self.net.get('StrD2').addIndexTermination(origin.getName(), D2_matrix, pstc, index = indexes)
        t2name = origin.getName() + '_D2'
        self.net.network.exposeTermination(t2, t2name)
        self.spa.net.network.addProjection(origin, self.net.network.getTermination(t2name))

        if( learn ):
            for pred_error in pred_errors:
                pred_term = self.spa.sources[pred_error.name]
                self.add_mod_input(pred_term, index_post = indexes, connect_STN = False)
                m1 = self.net.get('StrD1').getTermination(pred_error.name)
                m2 = self.net.get('StrD2').getTermination(pred_error.name)
                self.net.learn_array(self.net.get('StrD1'),t1,m1,stpd = False,rate = 1e-7)
                self.net.learn_array(self.net.get('StrD2'),t2,m2,stpd = False,rate = 1e-7)
