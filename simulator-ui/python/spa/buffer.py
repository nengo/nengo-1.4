import ca.nengo
import nef

import spa.module

class Buffer(spa.module.Module):
    def create(self,dimensions,feedback=1,N_per_D=30,pstc_feedback=0.01,subdimensions=None,intercept=None, radius=1, max_rate=(200,400)):
        #TODO: handle subdimensions
        buffer=self.net.make('buffer',N_per_D*dimensions,dimensions,quick=True,intercept=intercept, radius=radius, max_rate=max_rate)
        if feedback>0:
            self.net.connect(buffer,buffer,weight=feedback,pstc=pstc_feedback)
        self.add_source(buffer.getOrigin('X'))
        self.add_sink(buffer)

