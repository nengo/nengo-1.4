import pylab
import numpy
import theme

def flatten2(data):
    if data is None: return
    try:
        len(data[0])
        for row in data:
            for x in flatten2(row):
                yield x
    except:
        yield data            
def flatten1(data):
    if isinstance(data,(bool,int,float)) or data is None:
        yield data
    else:
        for row in data:
            for x in flatten1(row):
                yield x



class Plot:
    def __init__(self,dpi=100,width=6,height=4,
                      border_left=0.7,border_right=0.2,border_top=0.2,border_bottom=0.5,
                      color=True):
        self.fig=pylab.figure(figsize=(width,height),dpi=dpi)
        self.theme=theme.ThemePhi(color=color)
        self.fig.subplots_adjust(hspace=0,left=float(border_left)/width,right=1.0-float(border_right)/width,
                                            bottom=float(border_bottom)/height,top=1.0-float(border_top)/height)        
        self.axes=self.fig.add_subplot(1,1,1)
        self.legend_item={}
    def flatten(self,value,depth):
        if depth==1:
            return list(flatten1(value))    
        elif depth==2:    
            return list(flatten2(value))    
        else:
            if not hasattr(value,'shape'):
                value=numpy.array(value)
            shape=list(value.shape)
            while len(shape)<depth:
                shape=[1]+shape
            while len(shape)>depth:
                shape=[shape[0]*shape[1]]+shape[2:]
            value.shape=shape
            return value                    
    def legend(self,labels):
        items=[self.legend_item.get(i,None) for i in range(len(labels))]
        self.axes.legend(items,labels,loc='best')    
    def save(self,filename,dpi=300):
        self.fig.savefig(filename,dpi=dpi)    

    def show(self):
        pylab.show()

