import pylab
import numpy
import matplotlib
import random
import scipy.cluster.hierarchy
import scipy.ndimage

colors_bw=['0.0','0.4','0.2','0.6']

class TimePlot:
    def __init__(self,times,color=True,graph_spacing_top=0.1,graph_spacing_bottom=0.1,figure_spacing_left=0.1):
        self.times=times
        self.color=color
        self.rows=[]
        self.params=[]
        self.graph_spacing_top=graph_spacing_top
        self.graph_spacing_bottom=graph_spacing_bottom
        self.figure_spacing_left=figure_spacing_left
    def add(self,*data,**params):
        self.rows.append(data)
        self.params.append(params)
    def generate(self):
        fig=pylab.figure()
        fig.subplots_adjust(hspace=0,left=self.figure_spacing_left,right=0.95,bottom=0.1,top=0.95)
        
        for i,row in enumerate(self.rows):
            axes=fig.add_subplot(len(self.rows),1,i+1)
            maxv=None
            minv=None
            line_index=0
            for j,data in enumerate(row):
                if isinstance(data,str):
                    axes.set_ylabel(data)
                elif hasattr(data,'dtype') and issubclass(data.dtype.type,numpy.integer):
                    
                    sample=self.params[i].get('sample',0)
                    if sample>0 and sample<len(data.T):
                        filter_width=20
                        dd=scipy.ndimage.gaussian_filter1d(data.astype(float).T,filter_width,axis=1)
                        vard=numpy.var(dd,axis=1)
                        
                        threshold=sorted(vard)[-sample]                        
                        index=[k for k,v in enumerate(vard) if v>=threshold]
                        #data=dd[index].T
                        data=data[:,index]

                        #data=data[:,random.sample(range(len(data.T)),sample)]
                        
                    
                    
                    
                    if self.params[i].get('cluster',False):
                        filter_width=2
                        dd=scipy.ndimage.gaussian_filter1d(data.astype(float).T,filter_width,axis=1)
                        z=scipy.cluster.hierarchy.linkage(dd)
                        tree=scipy.cluster.hierarchy.to_tree(z)
                        order=tree.pre_order()
                        #data=dd[order].T
                        data=data[:,order]
                        
                        

                    minv=0
                    maxv=len(data.T)

                    merge=self.params[i].get('merge',0)
                    if merge>0:
                        stepsize=len(data.T)/merge
                        data2=[]
                        for k in range(merge):
                            v=numpy.sum(data[:,k*stepsize:(k+1)*stepsize],axis=1)
                            data2.append(v)
                        data=numpy.array(data2).T    
                            
                        
                    
                    imgplt=axes.imshow(data.T,aspect='auto',cmap=matplotlib.cm.gray_r,interpolation='nearest',extent=(self.times[0][0],self.times[-1][0],minv,maxv))
                    contrast=1.0
                    imgplt.set_clim(0.0,numpy.max(data)*contrast)
                    
                
                else:    
                    if maxv is None or max(data)>maxv:
                        maxv=numpy.max(data)
                    if minv is None or min(data)<minv:    
                        minv=numpy.min(data)
                    if len(data.shape)<2:
                        data=numpy.array([data])
                    offset=0    
                    for line in data.T:
                        if not self.color:
                            axes.plot(self.times,line+offset,colors_bw[line_index%len(colors_bw)])
                        else:    
                            axes.plot(self.times,line+offset)
                        line_index+=1    
                        offset+=self.params[i].get('separate_by',0)
                    offset-=self.params[i].get('separate_by',0)    
                    if offset>0: 
                        maxv+=offset    
                    elif offset<0:
                        minv+=offset
                        
            if minv<maxv:        
                delta=(maxv-minv)
                axes.set_ylim(minv-delta*self.graph_spacing_bottom,maxv+delta*self.graph_spacing_top)
                    
            axes.set_xlim(self.times[0][0],self.times[-1][0])    
            if i<len(self.rows)-1:
                axes.set_xticklabels(['']*len(axes.get_xticks()))  
            else:
                axes.set_xlabel('time (s)')    
                
            if not self.params[i].get('yticks',True):
                axes.set_yticklabels(['']*len(axes.get_xticks()))  
  
                
                
        return fig        
        
    def show(self):
        fig=self.generate()
        pylab.show()
        
    
