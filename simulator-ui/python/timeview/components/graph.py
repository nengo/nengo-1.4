import core

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

def safe_get_index(x,i):
    if x is None: return None
    return x[i]


class Graph(core.DataViewComponent):
    def __init__(self,view,name,func):
        core.DataViewComponent.__init__(self)
        self.view=view
        self.name=name
        self.func=func
        self.indices=None
        self.data=self.view.watcher.watch(name,func)
        self.border_left=40
        self.border_right=20
        self.border_bottom=20
        
        
        
    def round(self,x):
        sign=1
        if x<0:
            sign=-1
            x=-x
        v=1e-30
        if v>x: mn,mx=0,v
        else:
            while v*10<x: 
                v*=10
            if v*2>x:
                mn,mx=v,2*v
            elif v*5>x:
                mn,mx=2*v,5*v
            else:        
                mn,mx=5*v,10*v
        if sign<0:
            mn,mx=-mx,-mn            
        return mn,mx
        
    def fix_popup(self):
        self.popup.add(JPopupMenu.Separator())
        for i,draw in enumerate(self.indices):
            if i<30:
                self.popup.add(JCheckBoxMenuItem('%s[%d]'%('v',i),draw,stateChanged=lambda x,index=i,self=self: self.indices.__setitem__(index,x.source.state)))
        
        
        
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)

        g.color=Color(0.8,0.8,0.8)
        g.drawLine(self.border_left,0,self.border_left,self.size.height-self.border_bottom)
        g.drawLine(self.border_left,self.height-self.border_bottom,self.size.width-self.border_right,self.size.height-self.border_bottom)

        pts=int(self.view.time_shown/self.view.dt)
        extrapts=int(self.view.tau_filter*5/self.view.dt)
        extrapts=0

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


        dt_tau=None
        if self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter

        data=self.data.get(start=start-extrapts,count=pts+extrapts,dt_tau=dt_tau)
        now=self.view.current_tick-start
        
        if self.indices is None:
            for x in data:
                if x is not None:
                    self.indices=[False]*len(x)
                    for i in range(5): 
                        if i<len(x): self.indices[i]=True
                    self.fix_popup()    
                    break
            else:
                return

        maxy=None
        miny=None
        dx=float(self.size.width-self.border_left-self.border_right-1)/(pts-1)
        filtered=[]
        for i,draw in enumerate(self.indices):
            if draw:
                fdata=[safe_get_index(x,i) for x in data]
                fdata=fdata[extrapts:]
                trimmed=[x for x in fdata if x is not None]
                if len(trimmed)==0: continue
                fmaxy=max(trimmed)
                fminy=min(trimmed)
                fmaxy=self.round(fmaxy)[1]
                fminy=self.round(fminy)[0]
                if maxy is None or fmaxy>maxy: maxy=fmaxy
                if miny is None or fminy<miny: miny=fminy
                filtered.append(fdata)
            
            
        if maxy<1: maxy=1.0
        if miny>-1: miny=-1.0    
        if maxy==miny: yscale=0
        else: yscale=float(self.size.height-self.border_bottom)/(maxy-miny)


        colors=[Color.black,Color.blue,Color.red,Color.green,Color.magenta,Color.cyan,Color.yellow]
        for i,fdata in enumerate(filtered):
            g.color=colors[i%len(colors)]
            for i in range(len(fdata)-1):
                if fdata[i] is not None and fdata[i+1] is not None:
                    y1=self.size.height-(fdata[i]-miny)*yscale-self.border_bottom
                    y2=self.size.height-(fdata[i+1]-miny)*yscale-self.border_bottom
                    g.drawLine(int(i*dx+self.border_left),int(y1),int((i+1)*dx+self.border_left),int(y2))


        if 0<=now<len(data) and data[now] is not None:
            g.color=Color.blue
            g.drawLine(int(now*dx+self.border_left),0,int(now*dx+self.border_left),self.size.height-self.border_bottom)
            """
            label="%6g"%data[now]
            if now<pts/2:
                g.drawString(label,int(now*dx+self.border_left)+5,int(self.size.height-(data[now]-miny)*yscale-self.border_bottom)+5)
            else:    
                bounds=g.font.getStringBounds(label,g.fontRenderContext)
                g.drawString(label,int(now*dx+self.border_left)-bounds.width-5,int(self.size.height-(data[now]-miny)*yscale-self.border_bottom)+5)
            """    

   
        if maxy is not None: g.drawString('%6g'%maxy,0,10)
        if miny is not None: g.drawString('%6g'%miny,0,self.size.height-self.border_bottom)


