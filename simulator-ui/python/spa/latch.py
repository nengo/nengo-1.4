import ca.nengo
import nef

import spa.module
import math
import numeric
class Latch(spa.module.Module):
    def create(self,dimensions,N_per_D=30,pstc_feedback=0.01,latch_inhibit=2,
               pstc_latch_inhibit=0.006,neurons_detect=100,latch_detect_threshold=0.7,use_array=True,
               pstc_latch=0.01,feedback=1,compete=0,input_weight=1,subdimensions=None):

        #TODO: support subdimensions
        input=self.net.make('input',N_per_D*dimensions,dimensions,quick=True)
        if use_array:
            buffer=self.net.make_array('buffer',N_per_D,dimensions,encoders=[[1]],intercept=(0,1),quick=True)
        else:    
            buffer=self.net.make('buffer',N_per_D*dimensions,dimensions,encoders=numeric.eye(dimensions),intercept=(0,1),quick=True)
        feed=self.net.make('feedback',N_per_D*dimensions,dimensions,quick=True)
        feed.addTermination('gate',[[-latch_inhibit]]*feed.neurons,pstc_latch_inhibit,False)
        detect=self.net.make('detect',neurons_detect,1,intercept=(latch_detect_threshold,1),encoders=[[1]],quick=True)
        def length(x):
            s=sum([xx*xx for xx in x])
            return math.sqrt(s)
        self.net.connect(input,detect,func=length,pstc=pstc_latch)
        self.net.connect(detect,feed.getTermination('gate'))
        self.net.connect(buffer,feed,pstc=pstc_latch)
        self.net.connect(input,buffer,pstc=pstc_latch,weight=input_weight)

        t=numeric.eye(dimensions)*feedback
        if compete>0:
            t2=numeric.eye(dimensions)-1
            t2=t2*(compete*feedback)
            t=t+t2
        self.net.connect(feed,buffer,pstc=pstc_latch,transform=t)

        self.add_source(buffer.getOrigin('X'))
        self.add_sink(input)

