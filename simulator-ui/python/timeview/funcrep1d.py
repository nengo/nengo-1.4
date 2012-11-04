from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java
from numeric import array, dot, reshape, ones, transpose, concatenate, floor, zeros, repeat

import timeview.components.core as core
import timeview.view

#this is here to provide the functionality of a global variable without cluttering the global namespace
class FuncRepWatchConfig:
    config={}
    
    @classmethod
    def define(cls,obj,func,label='1D function',origin='X',minx=-1,maxx=1,miny=-1,maxy=1):
        cls.config[obj]=(origin,label,func,minx,maxx,miny,maxy)

class FuncRepWatch:
    def check(self,obj):
        return obj in FuncRepWatchConfig.config and obj.getOrigin(FuncRepWatchConfig.config[obj][0]) is not None
    def value(self,obj):
        return obj.getOrigin(FuncRepWatchConfig.config[obj][0]).getValues().getValues()    
    def views(self,obj):
        return [(FuncRepWatchConfig.config[obj][1],FunctionRepresentation,dict(func=self.value,label=obj.name,config=FuncRepWatchConfig.config[obj]))]
timeview.view.watches.append(FuncRepWatch())


class FunctionRepresentation(core.DataViewComponent):
    def __init__(self,view,name,config,func,args=(),label=None):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func,args=args)

        self.border_top=20
        self.border_left=20
        self.border_right=20
        self.border_bottom=20
        self.config=config
        self.start = 0

        self.setSize(240,240+self.label_offset)

        self.max = 0.0001
        self.min = -0.0001
            
    def paintComponent(self,g):
        
        origin,label,f,minx,maxx,miny,maxy = self.config
        
        core.DataViewComponent.paintComponent(self,g)

        width=self.size.width-self.border_left-self.border_right
        height=self.size.height-self.border_top-self.border_bottom-self.label_offset
            
        if width<2: return

        dt_tau=None
        if self.view.tau_filter>0:
            dt_tau=self.view.dt/self.view.tau_filter
        try:    
            data=self.data.get(start=self.view.current_tick,count=1,dt_tau=dt_tau)[0]
        except:
            return
        
        g.color=Color(0.8,0.8,0.8)
        g.drawRect(self.border_left,self.border_top+self.label_offset,width,height)
        
        g.color=Color.black
        txt='%4g'%maxx
        bounds=g.font.getStringBounds(txt,g.fontRenderContext)
        g.drawString(txt,self.size.width-self.border_right-bounds.width/2,self.size.height-self.border_bottom+bounds.height)

        txt='%4g'%minx
        bounds=g.font.getStringBounds(txt,g.fontRenderContext)
        g.drawString(txt,self.border_left-bounds.width/2,self.size.height-self.border_bottom+bounds.height)

        g.drawString('%6g'%maxy,0,10+self.border_top+self.label_offset)
        g.drawString('%6g'%miny,0,self.size.height-self.border_bottom)

        g.color=Color.black
        
        pdftemplate=getattr(self.view.area,'pdftemplate',None)
        if pdftemplate is not None:
            pdf,scale=pdftemplate
            pdf.setLineWidth(0.5)

            steps=100

            dx=float(maxx-minx)/(width*steps)

            for i in range(width*steps):
                x=minx+i*dx
                value=sum([f(j,x)*d for j,d in enumerate(data)])
                y=float((value-miny)*height/(maxy-miny))
                
                xx=self.border_left+i/float(steps)
                yy=self.height-self.border_bottom-y

                if i==0:
                    pdf.moveTo((self.x+xx)*scale,800-(self.y+yy)*scale)
                else:
                    if 0<y<height:
                        pdf.lineTo((self.x+xx)*scale,800-(self.y+yy)*scale)
            pdf.setRGBColorStroke(g.color.red,g.color.green,g.color.blue)        
            pdf.stroke()        
        else:                
            dx=float(maxx-minx)/(width-1)
            px,py=None,None
            for i in range(width):
                x=minx+i*dx
                value=sum([f(j,x)*d for j,d in enumerate(data)])

                y=int((value-miny)*height/(maxy-miny))

                xx=self.border_left+i
                yy=self.height-self.border_bottom-y

                if px is not None and miny<value<maxy:
                    g.drawLine(px,py,xx,yy)
                px,py=xx,yy

