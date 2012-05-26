import core
import numpy



class Bar(core.Plot):
    def __init__(self,**args):
        core.Plot.__init__(self,**args)
        self.labels=[]
    
    def bar(self,label,value,width=0.8,scatter=None,ci=True):
        if ci:
            value=self.flatten(value,2)
        else:
            value=self.flatten(value,1)    
        if scatter is not None: scatter=self.flatten(scatter,2)

        x=len(self.labels)
        bars=len(value)
        if bars>0:
            barwidth=width/bars
            space=(1.0-width)/2
        for i,val in enumerate(value):
            c=self.theme.bar_color(i)
            if ci:
                if val[1] is None:
                    val=[0,0,0]
                self.legend_item[i]=self.axes.bar([x+i*barwidth+space],val[1],width=barwidth,color=c)
                self.axes.errorbar(x+(i+0.5)*barwidth+space,val[1],yerr=[[val[1]-val[0]],[val[2]-val[1]]],ecolor='k',elinewidth=1)
            else:    
                if val is None: val=0
                self.legend_item[i]=self.axes.bar([x+i*barwidth+space],val,width=barwidth,color=c)
            if scatter is not None:
                data=scatter[i]
                minx=x+i*barwidth+space+barwidth/4
                if len(data)==1: 
                    xx=[x+0.5]
                elif len(data)>1:               
                    xx=numpy.arange(len(data))*barwidth/2/(len(data)-1)+minx
                    self.axes.scatter(xx,data,color='k',edgecolors='none',zorder=10)
                
                
        self.labels.append(label)
        self.axes.set_xticks([x+0.5 for x in range(len(self.labels))])        
        self.axes.set_xticklabels(self.labels)
        self.axes.set_xlim(0,len(self.labels))
        
    def plot(self,stats,values=None,scatter=False,metric='mean'):
        if values is None: values=stats.data[0].computed.value_names()
        
            
        scatter_data=None
        metric_data=getattr(stats,metric)
        for k in values:
            if scatter: scatter_data=getattr(stats.data,k)
            self.bar(k,getattr(metric_data,k),scatter=scatter_data)

        if hasattr(stats,'parameter_names'):
            names=stats.parameter_names()
            legend=[]
            for setting in stats.settings.ravel():
                legend.append(','.join(['%s=%s'%(k,setting[k]) for k in names]))
            self.legend(legend)    
                

