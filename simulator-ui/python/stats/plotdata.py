import pylab
import numpy
import matplotlib
import random

class Theme:
    def __init__(self):
        self.bar_colors=['0.4','0.8','0.6','1.0']
    def bar_color(self,i):
        return self.bar_colors[i%len(self.bar_colors)]

class ThemePhi:
    def __init__(self):
        self.phi=(1+numpy.sqrt(5))/2-1
        self.phi2=(1+numpy.sqrt(7))/2-1
        self.max_y=0.9
        self.min_y=0.1
        self.start_y=0.5
        self.start_theta=1.7
        #self.test_plot()
    def bar_color(self,i):
        y=((self.phi*i+self.start_y)%1)*(self.max_y-self.min_y)+self.min_y
        theta=self.phi*i*2.8+self.start_theta
        b=numpy.sin(theta)
        r=numpy.cos(theta)
        
        # move to extremes of color space
        if abs(r)>abs(b):
            if r>0:
                b=b/r
                r=1.0
            else:
                b=b/-r
                r=-1.0
        else:        
            if b>0:
                r=r/b
                b=1.0
            else:
                b=r/-b
                b=-1.0
        return self.ybr_to_color(y,b*0.5+0.5,r*0.5+0.5)
    
    def ybr_to_color(self,y,cb,cr):
        r=y+1.402*(cr-0.5)
        g=y-0.34414*(cb-0.5)-0.71414*(cr-0.5)
        b=y+1.772*(cb-0.5)
        r=int(r*255)
        g=int(g*255)
        b=int(b*255)
        if r<0: r=0
        if g<0: g=0
        if b<0: b=0
        if r>255: r=255
        if g>255: g=255
        if b>255: b=255
        return '#%02x%02x%02x'%(r,g,b)
    def test_plot(self):
        data=[]
        for i in range(20):
            y=((self.phi*i+self.start_y)%1)*(self.max_y-self.min_y)+self.min_y
            theta=(self.phi*i)+self.start_theta
            theta=theta%(2*numpy.pi)
            data.append((y,theta))
        import pylab
        pylab.scatter(numpy.array(data)[:,0],numpy.array(data)[:,1])
        pylab.show()            
            
    

class PlotData:
    def __init__(self,dpi=100,width=6,height=4):
        self.fig=pylab.figure(figsize=(width,height),dpi=dpi)
        self.theme=ThemePhi()
        self.axes=self.fig.add_subplot(1,1,1)
        self.labels=[]
        self.legend_bars=None
    def fix_shape(self,value):
        if not hasattr(value,'shape'):
            value=numpy.array(value)
        count=reduce(lambda x,y: x*y,value.shape)
        if value.shape[-1]==3:
            value.shape=count/3,3    
        else:
            value.shape=count,1
        return value        
    def plot(self,label,value,width=0.8):
        value=self.fix_shape(value)
        x=len(self.labels)
        bars=len(value)
        barwidth=width/bars
        space=(1.0-width)/2
        bars=[]
        for i,val in enumerate(value):
            c=self.theme.bar_color(i)
            if len(val)==3:
                if val[1] is not None:
                    bar=self.axes.bar([x+i*barwidth+space],val[1],width=barwidth,color=c)
                    self.axes.errorbar(x+(i+0.5)*barwidth+space,val[1],yerr=[[val[1]-val[0]],[val[2]-val[1]]],ecolor='k',elinewidth=1)
            else:    
                if val[0] is not None:
                    bar=self.axes.bar([x+i*barwidth+space],val[0],width=barwidth,color=c)
            bars.append(bar)
        if self.legend_bars is None: self.legend_bars=bars    
        self.labels.append(label)
        self.axes.set_xticks([x+0.5 for x in range(len(self.labels))])        
        self.axes.set_xticklabels(self.labels)
        self.axes.set_xlim(0,len(self.labels))
    def legend(self,labels):
        self.axes.legend(self.legend_bars,labels,loc='best')    
    def show(self):
        pylab.show()        
        


