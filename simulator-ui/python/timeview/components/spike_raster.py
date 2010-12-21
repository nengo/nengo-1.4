import core

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

import neuronmap
from math import sqrt

class SpikeRaster(core.DataViewComponent):
    def __init__(self,view,name,func,label=None,usemap=True):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func)
        self.label_height=10
        self.border_top=10
        self.border_left=30
        self.border_right=30
        self.border_bottom=20
        self.setSize(300,200)
        self.neurons=len(self.data.get_first())
        self.sample=10
        self.popup.add(JPopupMenu.Separator())
        self.popup.add(JMenuItem('show all',actionPerformed=lambda x: self.set_sample(1)))
        self.popup.add(JMenuItem('show 50%',actionPerformed=lambda x: self.set_sample(2)))
        self.popup.add(JMenuItem('show 33%',actionPerformed=lambda x: self.set_sample(3)))
        self.popup.add(JMenuItem('show 25%',actionPerformed=lambda x: self.set_sample(4)))
        self.popup.add(JMenuItem('show 20%',actionPerformed=lambda x: self.set_sample(5)))
        self.popup.add(JMenuItem('show 10%',actionPerformed=lambda x: self.set_sample(10)))
        self.popup.add(JMenuItem('show 5%',actionPerformed=lambda x: self.set_sample(20)))
        self.usemap=usemap
        
        
        self.map=None

    def set_sample(self,x):
        self.sample=x

    def save(self):
        d=core.DataViewComponent.save(self)
        d['sample']=self.sample
        return d
    
    def restore(self,d):
        core.DataViewComponent.restore(self,d)
        self.sample=d.get('sample',1)


    def initialize_map(self):
        data=self.data.get_first()
        rows=int(sqrt(len(data)))
        cols=len(data)/rows
        if rows*cols<len(data): cols+=1
            
        self.map=neuronmap.get(self.view.watcher.objects[self.name],rows,cols)
                
        
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)
        
        if self.usemap and self.map is None:
            self.initialize_map()
        
        border_top = self.border_top + self.label_offset
        
        g.color=Color(0.8,0.8,0.8)
        g.drawLine(self.border_left,self.height-self.border_bottom,self.size.width-self.border_right,self.size.height-self.border_bottom)

        pts=int(self.view.time_shown/self.view.dt)

        start=self.view.current_tick-pts+1
        if start<0: start=0
        if start<=self.view.timelog.tick_count-self.view.timelog.tick_limit:
            start=self.view.timelog.tick_count-self.view.timelog.tick_limit+1

        g.color=Color.black
        txt='%4g'%((start+pts)*self.view.dt)
        bounds=g.font.getStringBounds(txt,g.fontRenderContext)
        g.drawString(txt,self.size.width-self.border_right-bounds.width/2,self.size.height-self.border_bottom+bounds.height)

        txt='%4g'%((start)*self.view.dt)
        bounds=g.font.getStringBounds(txt,g.fontRenderContext)
        g.drawString(txt,self.border_left-bounds.width/2,self.size.height-self.border_bottom+bounds.height)


        data=self.data.get(start=start,count=pts)
        now=self.view.current_tick-start
        for i in range(now+1,len(data)):
            data[i]=None
        
        if self.neurons<self.sample:
            self.sample=self.neurons
        dy=float(self.size.height-self.border_bottom-border_top)/(self.neurons/self.sample)
        dx=float(self.size.width-self.border_left-self.border_right-1)/(pts-1)

        pdftemplate=getattr(self.view.area,'pdftemplate',None)
        if pdftemplate is not None:
            pdf,scale=pdftemplate
            pdf.setLineWidth(0)
        
        for i,d in enumerate(data):
            if d is None: continue
            for j in range(len(d)/self.sample):
                if self.usemap:
                    spike=d[self.map.map[j*self.sample]]
                else:
                    spike=d[j*self.sample]
                    
                if spike:
                    if pdftemplate is not None:
                        x=(self.x+i*dx+self.border_left)*scale
                        y=800-(self.y+j*dy+border_top)*scale
                        pdf.rectangle(x,y,dx*scale,-dy*scale)
                        pdf.fill()
                        
                    else:
                        x=int(i*dx+self.border_left)
                        y=int(j*dy+border_top)
                        
                        w=int(dx)-1
                        h=int(dy)-1
                        
                        if w<1: w=1
                        if h<1: h=1
                        
                        if w<=1 and h<=1:
                            g.drawLine(x,y,x,y)
                        else:
                            g.fillRect(x,y,w,h)
                        
                    
        if pdftemplate is not None:
            pdf.setLineWidth(1)
                    
        
        

