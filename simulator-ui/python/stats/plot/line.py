import core
import numpy
import matplotlib

class Line(core.Plot):
    def __init__(self,x=None,log_x=False,log_y=False,label=True,spaced=True,area=False,**args):
        core.Plot.__init__(self,**args)
        self.legend_names=[]
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
        self.area=area
        if log_x: self.axes.set_xscale('log') 
        if log_y: self.axes.set_yscale('log') 
    def plot(self,name,data,ci=True,linewidth=2):
        if ci:
            data=self.flatten(data,3)
        else:
            data=self.flatten(data,2)    
        
        if self.xpts is None:
            self.xpts=range(len(data[0]))        

        for val in data:        
            c=self.theme.line_color(self.line_count)
                       
            self.legend_item[self.line_count]=matplotlib.lines.Line2D([0],[0],linewidth=linewidth,color=c)
            if ci:
                if self.area:
                    c2=self.theme.make_color(self.line_count,max_y=1.0,min_y=0.7)
                    self.axes.fill_between(self.xpts,y1=val[:,0],y2=val[:,2],color=c2)
                    self.legend_item[self.line_count]=matplotlib.patches.Rectangle([0,0],1,1,facecolor=c2,edgecolor='white')
                self.axes.plot(self.xpts,val[:,1],color=c,linewidth=linewidth)
                if not self.area:
                    self.axes.errorbar(self.xpts,val[:,1],yerr=[val[:,1]-val[:,0],val[:,2]-val[:,1]],color=c,ecolor='k',elinewidth=1,label='_nolegend_')
            else:    
                self.axes.plot(self.xpts,val,linewidth=linewidth,color=c)
            self.line_count+=1 
                           
        if self.label:
            self.axes.set_xticks(self.xpts)        
            self.axes.set_xticklabels(self.x_labels)
        if self.spaced:
            self.axes.set_xlim(-0.2,len(self.xpts)-1+0.2)
        
        self.legend_names.append(name)
        if len(self.legend_names)==1:
            self.axes.set_ylabel(name)
        else:
            self.axes.set_ylabel('')
            self.legend(self.legend_names)    

