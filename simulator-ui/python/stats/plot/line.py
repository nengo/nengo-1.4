import core
import numpy
import matplotlib

class Line(core.Plot):
    def __init__(self,x=None,log_x=False,log_y=False,label=True,spaced=True,**args):
        core.Plot.__init__(self,**args)
        self.legend_names=[]
        self.xpts=x
        self.x_labels=x
        self.spaced=spaced
        if spaced: log_x=False
        if spaced and x is not None:
            self.xpts=range(len(x))
            log_x=False
            
        self.line_count=0
        self.log_x=log_x
        self.log_y=log_y
        self.label=label
        if log_x: self.axes.set_xscale('log') 
        if log_y: self.axes.set_yscale('log') 
    def set_x_axis(self,x):
        self.xpts=x
        self.x_labels=x
        if self.spaced:
            self.xpts=range(len(x))
                
    def line(self,name,data,ci=True,linewidth=2,area=False):
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
                for i,v in enumerate(val):
                    if v[1] is None:
                        val[i][0]=0
                        val[i][1]=0
                        val[i][2]=0
                if area:
                    c2=self.theme.make_color(self.line_count,max_y=1.0,min_y=0.7)
                    self.axes.fill_between(self.xpts,y1=val[:,0],y2=val[:,2],color=c2)
                    self.legend_item[self.line_count]=matplotlib.patches.Rectangle([0,0],1,1,facecolor=c2,edgecolor='white')
                self.axes.plot(self.xpts,val[:,1],color=c,linewidth=linewidth)
                if not area:
                    self.axes.errorbar(self.xpts,val[:,1],yerr=[val[:,1]-val[:,0],val[:,2]-val[:,1]],color=c,ecolor='k',elinewidth=1,label='_nolegend_')
            else:    
                self.axes.plot(self.xpts,val,linewidth=linewidth,color=c)
            self.line_count+=1 
                           
        if self.label and self.x_labels is not None:
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

    def plot(self,stats,values=None,metric='mean',area=False):
        if values is None: values=stats.data[0].computed.value_names()
        
        if hasattr(stats,'parameter_names'):
            names=stats.parameter_names()
            key=names[-1]
            settings=stats.settings
            count=settings.shape[-1]
            
            self.set_x_axis([s[key] for s in settings.ravel()[-count:]])
            self.axes.set_xlabel(key)
        
        metric_data=getattr(stats,metric)
        for k in values:
            self.line(k,getattr(metric_data,k),area=area)

            

