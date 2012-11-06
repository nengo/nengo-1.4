import nef

from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java

import timeview.components.core as core
import timeview.view

#this is here to provide the functionality of a global variable without cluttering the global namespace
class EncoderMapWatchConfig:
    config={}
    
    @classmethod
    def define(cls,obj,func,minx=-1,maxx=1,miny=-1,maxy=1,min_rate=0,max_rate=0.2,size=0.05):
        cls.config[obj]=(func,minx,maxx,miny,maxy,min_rate,max_rate,size)

class EncoderMapWatch:
    def check(self,obj):
        return obj in EncoderMapWatchConfig.config
    def views(self,obj):
        return [('encoder map',EncoderMap,dict(func=timeview.view.ensembleWatch.spikes,label=obj.name,config=EncoderMapWatchConfig.config[obj],encoders=obj.encoders))]
timeview.view.watches.append(EncoderMapWatch())


class EncoderMap(core.DataViewComponent):
    def __init__(self,view,name,config,func,args=(),label=None,encoders=None):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func,args=args)
        self.margin=10
        self.min=min
        self.max=max        
        self.setSize(200,200)
        self.config=config
        self.encoders=encoders
        
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)

        x0=0
        y0=self.label_offset
        g.color=Color(0.8,0.8,0.8)
        g.fillRect(int(x0)-1,int(y0)-1,int(self.size.width)+1,int(self.size.height-self.label_offset)+1)
        
        
        dt_tau=None
        if self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter
        try:    
            data=self.data.get(start=self.view.current_tick,count=1,dt_tau=dt_tau)[0]
        except:
            return
        if data is None: 
            return
        
        func,minx,maxx,miny,maxy,min_rate,max_rate,size=self.config
        size=size*min(self.size.width,self.size.height)
                
        xscale=float(self.size.width-self.margin*2)/(maxx-minx)
        yscale=float(self.size.height-self.label_offset-self.margin*2)/(maxy-miny)
        for i,encoder in enumerate(self.encoders):
        
            c=(float(data[i])-min_rate)/(max_rate-min_rate)
            if c>1.0: c=1.0
            if c<0.0: c=0.0
            g.color=Color(c,c,c)
            
            x,y=func(encoder)
            x=xscale*(x-minx)+self.margin
            y=yscale*(y-miny)+self.margin
            
            
            g.fillRect(int(x-size/2),self.size.height-int(y-size/2),int(size),int(size))
                   