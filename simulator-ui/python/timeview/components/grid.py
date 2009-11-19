from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

import core
import neuronmap
reload(neuronmap)

from math import sqrt
class Grid(core.DataViewComponent):
    def __init__(self,view,name,func,sfunc=None,min=0,max=1,rows=None,filter=False):
        core.DataViewComponent.__init__(self)
        self.view=view
        self.name=name
        self.func=func
        self.sfunc=sfunc
        self.data=self.view.watcher.watch(name,func)
        if sfunc is not None:
            self.sdata=self.view.watcher.watch(name,sfunc)
        self.rows=rows
        self.margin=10
        self.min=min
        self.max=max
        self.map=None
        self.requested_improvements=0
        
        self.popup.add(JPopupMenu.Separator())
        self.popup.add(JMenuItem('improve layout',actionPerformed=self.improve_layout))
        self.filter=filter
        
    def improve_layout(self,event):
        self.requested_improvements+=20
        
        
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)
        x0=self.margin/2.0
        y0=self.margin/2.0
        g.color=Color.black
        g.drawRect(int(x0)-1,int(y0)-1,int(self.size.width-self.margin)+1,int(self.size.height-self.margin)+1)
        
        dt_tau=None
        if self.filter and self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter
        try:    
            data=self.data.get(start=self.view.current_tick,count=1,dt_tau=dt_tau)[0]
        except:
            return
        if self.sfunc is not None:
            sdata=self.sdata.get(start=self.view.current_tick,count=1)[0]
        else:
            sdata=None


        
        if data is None: 
            return
        
        if self.rows is None:
            rows=int(sqrt(len(data)))
        else:
            rows=self.rows    
        cols=len(data)/rows
        if rows*cols<len(data): cols+=1
            
        if self.map is None:
            self.map=neuronmap.get(self.view.watcher.objects[self.name],rows,cols)
        

        max=self.max
        if callable(max): max=max()
        min=self.min
        if callable(min): min=min()
            
        dx=float(self.size.width-self.margin)/cols
        dy=float(self.size.height-self.margin)/rows
        for y in range(rows):
            for x in range(cols):                
                if x+y*cols<len(data):
                    index=self.map.map[x+y*cols]
                    if sdata is not None and self.view.current_tick>0 and sdata[index]:
                        g.color=Color.yellow
                    else:
                        c=(float(data[index])-min)/(max-min)
                        if c<0: c=0.0
                        if c>1: c=1.0
                        g.color=Color(c,c,c)
                    g.fillRect(int(x0+dx*x),int(y0+dy*y),int(dx+1),int(dy+1))
        g.color=Color.black
        g.drawRect(int(x0)-1,int(y0)-1,int(self.size.width-self.margin)+1,int(self.size.height-self.margin)+1)

        if self.requested_improvements>1:    
            self.map.improve()
            self.requested_improvements-=1
            self.parent.repaint()
