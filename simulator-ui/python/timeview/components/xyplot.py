from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java

import core
from graph import round

from math import sqrt
class XYPlot(core.DataViewComponent):
    def __init__(self,view,name,func,args=(),filter=None,label=None):
        core.DataViewComponent.__init__(self)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func,args=args)
        self.margin=10
        self.label=label
        
        self.filter=filter
        self.setSize(200,200)
        
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)
        
        xc=self.width/2
        yc=self.height/2
        x0=self.margin/2.0
        y0=self.margin/2.0
        g.color=Color(0.8,0.8,0.8)
        g.drawRect(int(x0)-1,int(y0)-1,int(self.size.width-self.margin)+1,int(self.size.height-self.margin)+1)
        g.drawLine(xc,self.margin,xc,self.height-self.margin)
        g.drawLine(self.margin,yc,self.width-self.margin,yc)
        
        dt_tau=None
        if self.filter and self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter
            
        pts=int(self.view.time_shown/self.view.dt)
        try:    
            data=self.data.get(start=self.view.current_tick-pts+1,count=pts,dt_tau=dt_tau)
        except:
            return
        
        if data is None: 
            return

        xs=[d[0] for d in data if d is not None]
        ys=[d[1] for d in data if d is not None]
        if len(xs)>0:
            mx=max(max(xs),max(ys),-min(xs),-min(ys))
            mx=round(mx)[1]
            if mx<1.0: mx=1.0
        else:
            mx=1.0    
            
            
        txt='%g'%mx
        bounds=g.font.getStringBounds(txt,g.fontRenderContext)
        g.color=Color.black

        g.drawString(txt,xc+self.margin,bounds.height)
        g.drawString(txt,self.width-self.margin-bounds.width,yc+bounds.height)
        
        #g.drawString(txt,self.size.width-self.border_right-bounds.width/2,self.size.height-self.border_bottom+bounds.height)

        txt='%g'%(-mx)
        g.drawString(txt,xc+self.margin,self.height-self.margin)
        g.drawString(txt,self.margin,yc+bounds.height)
            
        
        sx=(self.width/2-self.margin)/mx
        sy=(self.height/2-self.margin)/mx
        for i in range(pts-1):
            if data[i] is not None and data[i+1] is not None:
                x0,y0=data[i]
                x1,y1=data[i+1]
                
                c=1.0-i/float(pts-1)
                g.color=Color(c,c,c)
                
                g.drawLine(int(xc+x0*sx),int(yc-y0*sy),int(xc+x1*sx),int(yc-y1*sy))

