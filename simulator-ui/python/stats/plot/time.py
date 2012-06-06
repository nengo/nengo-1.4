import core
import numpy
import matplotlib
import scipy.ndimage

class Time(core.Plot):
    def __init__(self,time,time_range=None,**args):
        core.Plot.__init__(self,**args)
        self.time=time
        self.xlim=time_range
        self.subplots=[]
        self.heights=[]
    def _fix_plots(self):
        totalh=float(sum(self.heights))
        space=self.fig.subplotpars.top-self.fig.subplotpars.bottom
        y=self.fig.subplotpars.top
        for i,plot in enumerate(self.subplots):
            h=space/totalh*self.heights[i]
            plot.set_position((self.fig.subplotpars.left,y-h,self.fig.subplotpars.right-self.fig.subplotpars.left,h))
            y-=h

            if i<len(self.subplots)-1:
                plot.set_xticklabels(['']*len(plot.get_xticks()))  
                plot.set_xlabel('')
            else:
                plot.set_xlabel('time (s)')    
            
    def _create_axes(self,label,height):
        self.heights.append(height)
        if len(self.subplots)==0:
            axes=self.axes
        else:
            axes=self.fig.add_subplot(len(self.subplots)+1,1,len(self.subplots)+1)
        if self.xlim is not None: axes.set_xlim(self.xlim)    
        self.subplots.append(axes)
        self._fix_plots()
        
        axes.set_ylabel(label)
        return axes
            
            
    def add(self,label,data,height=1,linewidth=1,range=None):
        axes=self._create_axes(label,height)
                
        if len(data.shape)<2:
            data=numpy.array([data])
        for i,line in enumerate(data.T):
            c=self.theme.line_color(i)
            axes.plot(self.time,line,color=c,linewidth=linewidth)
        if range is not None:
            axes.set_ylim(range)    
            
    def add_spikes(self,label,data,height=1,sample_by_variance=None,sample=None,sample_filter_width=20,cluster=False,cluster_filter_width=2,merge=None,contrast_scale=1.0,yticks=None,style='image'):
        axes=self._create_axes(label,height)

        if sample_by_variance is not None and sample_by_variance<len(data.T):
            dd=scipy.ndimage.gaussian_filter1d(data.astype(float).T,sample_filter_width,axis=1)
            vard=numpy.var(dd,axis=1)
                        
            threshold=sorted(vard)[-sample_by_variance]                        
            index=[k for k,v in enumerate(vard) if v>=threshold]
            data=data[:,index]
        if sample is not None and sample<len(data.T):    
            stepsize=float(len(data.T))/sample
            data2=[]
            for k in range(sample):
                sub=data[:,int(k*stepsize):int((k+1)*stepsize)]
                count=numpy.sum(sub,axis=0)
                maxv=max(count)
                for i,v in enumerate(count):
                    if v==maxv: 
                        data2.append(sub[:,i])
                        break
            data=numpy.array(data2).T    
            
            
        if cluster:
            dd=scipy.ndimage.gaussian_filter1d(data.astype(float).T,cluster_filter_width,axis=1)
            z=scipy.cluster.hierarchy.linkage(dd)
            tree=scipy.cluster.hierarchy.to_tree(z)
            order=tree.pre_order()
            data=data[:,order]
        if merge is not None and merge<len(data.T):    
            stepsize=float(len(data.T))/merge
            data2=[]
            for k in range(merge):
                v=numpy.sum(data[:,int(k*stepsize):int((k+1)*stepsize)],axis=1)
                data2.append(v)
            data=numpy.array(data2).T    
                            
        if style=='image':                    
            imgplt=axes.imshow(data.T,aspect='auto',cmap=matplotlib.cm.gray_r,interpolation='nearest',extent=(self.time[0][0],self.time[-1][0],0,len(data.T)))
            imgplt.set_clim(0.0,numpy.max(data)*contrast_scale)
        elif style=='box':
            vmax=float(numpy.max(data))
            N=len(data.T)
            dt=self.time[1][0]-self.time[0][0]
            for i,t in enumerate(self.time):
                t=t[0]
                for j in range(N):
                    if data[i][j]>0:
                        c=(1.0-data[i][j]/(vmax*contrast_scale))
                        if c<0: c=0
                        c='%0.2f'%c
                       
                        axes.axvspan(t,t+dt,1.0-(j+1)/float(N),1.0-(j)/float(N),fc=c,linewidth=0)
            axes.set_ylim((0,N))            
        elif style=='dot':
            vmax=float(numpy.max(data))
            N=len(data.T)
            for i,t in enumerate(self.time):
                t=t[0]
                neurons=[]
                colors=[]
                for j in range(N):
                    if data[i][j]>0:
                        c=(1.0-data[i][j]/(vmax*contrast_scale))
                        if c<0: c=0
                        c='%0.2f'%c
                        colors.append(c)
                        neurons.append(N-j-1)
                if len(neurons)>0:        
                    axes.scatter([t]*len(neurons),neurons,s=1,c=colors,linewidths=0,marker='o')
            axes.set_ylim((0,N))            
            
            
        
        if yticks is None:
            axes.set_yticklabels(['']*len(axes.get_yticks()))  
        else:
            delta=float(len(data.T))/len(yticks)
            vals=[(len(yticks)-i-0.5)*delta for i in range(len(yticks))]
            axes.set_yticks(vals)
            axes.set_yticklabels(yticks)
                
        
        
            
        
        

