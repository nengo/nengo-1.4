N_per_D=30

import ca.nengo
import nef

class Buffer(ca.nengo.model.impl.NetworkImpl):
    def __init__(self,dimensions,name='Buffer',feedback=1,pstc_feedback=0.01):
        ca.nengo.model.impl.NetworkImpl.__init__(self)
        self.name=name
        net=nef.Network(self)

        #self.buffer=net.make_array('buffer',N_per_D,dimensions,quick=True)
        self.buffer=net.make('buffer',N_per_D*dimensions,dimensions,quick=True)

        if feedback>0:
            net.connect(self.buffer,self.buffer,weight=feedback,pstc=pstc_feedback)

        self.exposeOrigin(self.buffer.getOrigin('X'),'value')
    def get_sinks(self):
        return dict(buffer=self.buffer)
