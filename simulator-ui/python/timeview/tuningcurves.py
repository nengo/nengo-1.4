from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java

from ca.nengo.model.nef import NEFEnsemble
from ca.nengo.model import SimulationMode

import timeview.components.core as core
from timeview.components import Graph
import timeview.view
import math

class TuningCurveWatch:
    def check(self,obj):
        return isinstance(obj,NEFEnsemble) and obj.dimension<=2
    def views(self,obj):
        pts=100

        def makedata(obj=obj,pts=pts):
            encoders=obj.getEncoders()
            x=[-1+i/float(pts) for i in range(pts*2+1)]

            mode=obj.mode
            obj.setMode(SimulationMode.CONSTANT_RATE)
            noises=[nn.noise for nn in obj.nodes]
            for nn in obj.nodes:
                nn.noise=None
            data=[]

            for xx in x:
                rate=[]
                for i,n in enumerate(obj.nodes):
                    if obj.dimension==1:
                        input=xx*encoders[i][0]
                    elif obj.dimension==2:
                        theta=xx*math.pi
                        input=math.sin(theta)*encoders[i][0]+math.cos(theta)*encoders[i][1]
                    n.radialInput=input    
                    n.run(0,0)
                    output=n.getOrigin('AXON').getValues().getValues()[0]
                    rate.append(output)
                    n.reset(False)
                data.append(rate)

            obj.setMode(mode)
            for i,nn in enumerate(obj.nodes):
                nn.noise=noises[i]

            return data

        labels=dict()
        labels[pts]='0'

        if obj.dimension==1:
            labels[0]='%4g'%(-obj.radii[0])
            labels[pts*2]='%4g'%(obj.radii[0])
        elif obj.dimension==2:    
            labels[0]='-180'
            labels[pts/2]='-90'
            labels[3*pts/2]='90'
            labels[pts*2]='180'
        
        return [('tuning curves',Graph,
                 dict(func=None,label=obj.name,data=makedata,x_labels=labels,show_negative=False))]

timeview.view.watches.append(TuningCurveWatch())


        
        
    
    
