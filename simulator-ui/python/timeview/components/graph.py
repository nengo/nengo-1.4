import core
import neuronmap

from javax.swing import *
from javax.swing.event import *
from java.awt import *
from java.awt.event import *

from math import sqrt
from math import log
from math import ceil

def safe_get_index(x,i):
    if x is None: return None
    return x[i]


class Graph(core.DataViewComponent):
    def __init__(self,view,name,func,args=(),filter=True,ylimits=(-1.0,1.0),split=False,neuronmapped=False,label=None):
        core.DataViewComponent.__init__(self)
        self.view=view
        self.name=name
        self.func=func
        self.indices=None
        self.data=self.view.watcher.watch(name,func,args=args)
        self.border_top=10
        self.border_left=30
        self.border_right=30
        self.border_bottom=20
        self.default_selected=5     # The default number of selected display dimensions
        self.max_show_dim=30        # The maximum number of display dimensions to show in the popup menu
        self.filter=filter
        self.setSize(300,200)
        self.ylimits=ylimits
        self.split=split
        self.neuronmapped=neuronmapped
        
        self.map=None
        self.label=label
        self.show_label=False
        if self.label is not None:
            self.popup.add(JCheckBoxMenuItem('label',False,stateChanged=self.toggle_label))


    def initialize_map(self):
        data=self.data.get_first()
        rows=int(sqrt(len(data)))
        cols=len(data)/rows
        if rows*cols<len(data): cols+=1
            
        self.map=neuronmap.get(self.view.watcher.objects[self.name],rows,cols)

    def save(self):
        save_info = core.DataViewComponent.save(self)
        
        sel_dim = []
        for n in range(len(self.indices)):          # Get the checkbox states
            if( self.indices[n] ):
                sel_dim += [n]
            
        save_info['sel_dim'] = sel_dim              # Save the checkbox states
        
        return save_info            
    
    def restore(self,d):
        core.DataViewComponent.restore(self,d)
        
        data_dim = len(self.data.get_first())       # Get dimensionality of data
        self.indices = [False] * data_dim
        
        if( 'sel_dim' in d.keys() ):
            sel_dim = d['sel_dim']
        else:
            sel_dim = range(min(data_dim, self.default_selected))
        
        for dim in sel_dim:                         # Iterate and restore the saved state
            if( dim < data_dim ):
                self.indices[dim] = True
                
        self.fix_popup()                            # Update the pop-up box
        
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
        
    def toggle_label(self,event):
        self.show_label=event.source.state
        
    def fix_popup(self):
        self.popup.add(JPopupMenu.Separator())
        
        # Calculate number of submenu layers needed
        max_ind = len(self.indices)
        num_sub = max(1,int(ceil(log(max_ind) / log(self.max_show_dim))))
        max_sub = [self.max_show_dim ** (num_sub - i) for i in range(num_sub)]
        sub_menus = [self.popup] * num_sub
        
        for i,draw in enumerate(self.indices):
            if( i % self.max_show_dim == 0 ):
                for n in range(num_sub-1):
                    if( i % max_sub[n+1] == 0 ):
                        new_menu = JMenu("%s[%d:%d]" % ('v', i, min(max_ind, i + max_sub[n+1]) - 1))
                        sub_menus[n].add(new_menu)
                        sub_menus[n+1] = new_menu
            sub_menus[num_sub-1].add(JCheckBoxMenuItem('%s[%d]'%('v',i),draw,stateChanged=lambda x,index=i,self=self: self.indices.__setitem__(index,x.source.state)))
        
    def paintComponent(self,g):
        core.DataViewComponent.paintComponent(self,g)

        if self.neuronmapped and self.map is None:
            self.initialize_map()

        g.color=Color(0.8,0.8,0.8)
        g.drawLine(self.border_left,self.border_top,self.border_left,self.size.height-self.border_bottom)
        g.drawLine(self.border_left,self.height-self.border_bottom,self.size.width-self.border_right,self.size.height-self.border_bottom)
        
        if self.show_label:
            g.color=Color(0.3,0.3,0.3)
            bounds=g.font.getStringBounds(self.label,g.fontRenderContext)
            g.drawString(self.label,self.size.width-bounds.width-5,bounds.height)

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



        dt_tau=None
        if self.filter and self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter

        data=self.data.get(start=start,count=pts,dt_tau=dt_tau)
        now=self.view.current_tick-start
        for i in range(now+1,len(data)):
            data[i]=None
        
        if self.indices is None:
            for x in data:
                if x is not None:
                    self.indices=[False]*len(x)
                    for i in range(self.default_selected): 
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
                if self.neuronmapped:
                    fdata=[safe_get_index(x,self.map.map[i]) for x in data]
                else:
                    fdata=[safe_get_index(x,i) for x in data]
                trimmed=[x for x in fdata if x is not None]
                if len(trimmed)==0: continue
                fmaxy=max(trimmed)
                fminy=min(trimmed)
                fmaxy=self.round(fmaxy)[1]
                fminy=self.round(fminy)[0]
                if maxy is None or fmaxy>maxy: maxy=fmaxy
                if miny is None or fminy<miny: miny=fminy
                filtered.append(fdata)
            
        if maxy is None: maxy=1.0
        if miny is None: miny=-1.0    
        if maxy<self.ylimits[1]: maxy=float(self.ylimits[1])
        if miny>self.ylimits[0]: miny=float(self.ylimits[0])
        if maxy==miny: yscale=0
        else: yscale=float(self.size.height-self.border_bottom-self.border_top)/(maxy-miny)
        if self.split and len(filtered)>0: 
            yscale=yscale/len(filtered)
            split_step=float(self.size.height-self.border_bottom-self.border_top)/len(filtered)
            


        colors=[Color.black,Color.blue,Color.red,Color.green,Color.magenta,Color.cyan,Color.yellow]
        g.color=Color.blue
        for j,fdata in enumerate(filtered):
            if (self.size.width-self.border_left-self.border_right)<=0:
                break
            skip=(len(fdata)/(self.size.width-self.border_left-self.border_right))-1
            if self.filter and self.view.tau_filter==0:
                skip-=1     # special case to make unfiltered recoded value graphs look as expected
            if skip<0: skip=0
            #skip=0
                        
            offset=start%(skip+1)            
            if not self.split:
                g.color=colors[j%len(colors)]
            for i in range(len(fdata)-1-skip):
                if skip==0 or (i+offset)%(skip+1)==0:
                  if fdata[i] is not None and fdata[i+1+skip] is not None:
                    y1=self.size.height-(fdata[i]-miny)*yscale-self.border_bottom
                    y2=self.size.height-(fdata[i+1+skip]-miny)*yscale-self.border_bottom
                    if self.split:
                        y1-=(len(filtered)-1-j)*split_step
                        y2-=(len(filtered)-1-j)*split_step
                    g.drawLine(int(i*dx+self.border_left),int(y1),int((i+1+skip)*dx+self.border_left),int(y2))
        if not self.split:
            g.color=Color.black   
            if maxy is not None: g.drawString('%6g'%maxy,0,10+self.border_top)
            if miny is not None: g.drawString('%6g'%miny,0,self.size.height-self.border_bottom)


