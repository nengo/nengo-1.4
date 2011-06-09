from javax.swing.event import *
from java.awt import *
from java.awt.event import *
import java
import java.awt.image.BufferedImage as BI
from java.awt.image.WritableRaster import setPixels
from numeric import array, dot, reshape, ones, transpose, concatenate, floor, zeros, repeat
# from java.lang.Math import floor, double

import timeview.components.core as core
import timeview.view

config={}

def define(obj,func, dimension = 1, minx=-1,maxx=1,miny=-1,maxy=1, params = None):
    config[obj]=(func,dimension,minx,maxx,miny,maxy, params)

class FuncRepWatch:
    def check(self,obj):
        return obj in config
    def value(self,obj):
        return obj.getOrigin('X').getValues().getValues()    
    def views(self,obj):
        return [('function',FunctionRepresentation,dict(func=self.value,label=obj.name,config=config[obj]))]
timeview.view.watches.append(FuncRepWatch())


class FunctionRepresentation(core.DataViewComponent):
    def __init__(self,view,name,config,func,args=(),label=None):
        core.DataViewComponent.__init__(self,label)
        self.view=view
        self.name=name
        self.func=func
        self.data=self.view.watcher.watch(name,func,args=args)

        self.border_top=37
        self.border_left=37
        self.border_right=37
        self.border_bottom=37
        self.config=config
        self.start = 0

        self.setSize(200,200)

        self.max = 0.0001
        self.min = -0.0001
        
    def paintComponent(self,g):
    
        f,dimension,minx,maxx,miny,maxy, params=self.config
        
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
        
        if dimension == 2: # currently only for 63*63 grid
            grid_size = 63
            # step_size = width / grid_size
            bi = BI(width, height, BI.TYPE_INT_ARGB)
            
            # construct input grid
            row = array([range(63)]) + 1
            gridX = dot(ones([grid_size,1]), row)
            gridY = transpose(gridX)
            
            # get function value
            x1 = reshape(gridX,[1, grid_size*grid_size])
            x2 = reshape(gridY,[1, grid_size*grid_size])
            x = concatenate([x1, x2], 0)
            coeffs=transpose(array([data]))
            basis = array(f(x, params))
            value = transpose(dot(transpose(basis),coeffs))
            maxv = max(value[0])
            minv = min(value[0])
            if maxv > self.max:
                self.max = maxv
            if minv < self.min:
                self.min = minv
    
            pvalue = (value - self.min) / (self.max - self.min) # normalized pixel value
            pvalue = map(int, array(pvalue[0]) * 255)
            pvalue = reshape(pvalue,[grid_size, grid_size]) 
            # print(pvalue)
            pvalue = pvalue * 256 * 256 + pvalue * 256 + pvalue #convert to R G B
            pvalue = 0xFF000000 + pvalue
            
            # expand pixel value from grid to raster size
            # ratio = float(width) / grid_size
            # indeces = map(int, (floor(array(range(width)) / ratio)))
            ## Tooooooo slow here!
            # for i, ii in enumerate(indeces): 
                # for j, jj in enumerate(indeces) :
                    # rvalue[i,j] = pvalue[ii,jj]
            rvalue = reshape(pvalue, [grid_size * grid_size, 1])
            rvalue = concatenate([rvalue, rvalue], 1)
            rvalue = reshape(rvalue, [grid_size, grid_size* 2])
            rvalue = repeat(rvalue, ones(grid_size) * 2)
            
            # draw image
            rvalue = reshape(rvalue, [1, width * height])
            bi.setRGB(0, 0, width, height, rvalue[0], 0, width)
            g.drawImage( bi, self.border_left, self.border_top, None)
            
        elif dimension == 1: 
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
"""               
            #############################################################################   
            ##obsolet method, too slow   
            # elif dimension == 2 : 
                
                max = 0.0001 #dynamically update max and min
                min = -0.0001
                dx = float(maxx-minx)/(width-1)
                dy = float(maxy-miny)/(height-1)
                
                for ix in range(width-1):
                    for iy in range(height-1):
                    
                        x = [minx+ix*dx , miny+iy*dy]
                        value = sum([f(j,x,params)*d for j,d in enumerate(data)])
                        if value > max :
                            max = value
                        elif value < min :
                            min = value
                        
                        rgb = (value-min)/(max-min)
                        xx = self.border_left + ix
                        yy = self.border_top + iy
                        g.color = Color(rgb, rgb, rgb)
                        g.fillRect(xx, yy, 1, 1)
"""                        