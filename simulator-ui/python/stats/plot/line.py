import core
import numpy
import matplotlib

class Line(core.Plot):
    def __init__(self,x=None,log_x=False,log_y=False,label=True,spaced=True,**args):
        core.Plot.__init__(self,**args)
        self.labels=[]
        self.xpts=x
        self.x_labels=x
        self.spaced=spaced
        if spaced:
            self.xpts=range(len(x))
            log_x=False
            
        self.line_count=0
        self.log_x=log_x
        self.log_y=log_y
        self.label=label
        if log_x: self.axes.set_xscale('log') 
        if log_y: self.axes.set_yscale('log') 
    def plot(self,data,ci=True,linewidth=2):
        if ci:
            data=self.flatten(data,3)
        else:
            data=self.flatten(data,2)    
        
        if self.xpts is None:
            self.xpts=range(len(data[0]))        

        for val in data:        
            c=self.theme.bar_color(self.line_count)
           
            
            if ci:
                self.axes.plot(self.xpts,val[:,1],color=c,linewidth=linewidth)
                self.axes.errorbar(self.xpts,val[:,1],yerr=[val[:,1]-val[:,0],val[:,2]-val[:,1]],color=c,ecolor='k',elinewidth=1,label='_nolegend_')
            else:    
                self.axes.plot(self.xpts,val,linewidth=linewidth,color=c)
            self.legend_item[self.line_count]=matplotlib.lines.Line2D([0],[0],linewidth=linewidth,color=c)
            self.line_count+=1 
                           
        if self.label:
            self.axes.set_xticks(self.xpts)        
            self.axes.set_xticklabels(self.x_labels)
        if self.spaced:
            self.axes.set_xlim(-0.2,len(self.xpts)-1+0.2)

