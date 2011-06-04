import ca.nengo
import nef

import spa.module
import numeric

class MemorySink:
    def __init__(self,memory,first,invert=False):
        self.memory=memory
        self.first=first
        self.dimension=self.memory.p.dimensions
        self.invert=invert
    def addDecodedTermination(self,name,matrix,pstc,modulatory):
        M=nef.convolution.input_transform(self.dimension,self.first,self.invert)
        if self.invert:
            conv=self.memory.net.network.getNode('deconv')
        else:
            conv=self.memory.net.network.getNode('conv')
        return conv.addDecodedTermination(name,numeric.dot(M,matrix),pstc,modulatory)
        

class Memory(spa.module.Module):
    def create(self,dimensions,N_per_D=30,subdimensions=None,N_conv=300,pstc=0.01,output_scale=2,input_scale=0.1,
                feedback=1,pstc_feedback=0.1,pstc_recall=0.002,cleanup=[],radius=1):

        if N_conv==0:
            conv=nef.convolution.make_array(self.net,'conv',1,dimensions,quick=True)
            deconv=nef.convolution.make_array(self.net,'deconv',1,dimensions,quick=True)
            conv.mode=ca.nengo.model.SimulationMode.DIRECT
            deconv.mode=ca.nengo.model.SimulationMode.DIRECT
        else:
            conv=nef.convolution.make_array(self.net,'conv',N_conv,dimensions,quick=True)
            deconv=nef.convolution.make_array(self.net,'deconv',N_conv,dimensions,quick=True)


        if N_per_D==0:
                mem=self.net.make('mem',1,dimensions,quick=True,mode='direct')
                recall=self.net.make('recall',1,dimensions,quick=True,mode='direct')
        else:
            if subdimensions is None:
                mem=self.net.make('mem',N_per_D*dimensions,dimensions,quick=True)
                recall=self.net.make('recall',N_per_D*dimensions,dimensions,quick=True)
            else:
                assert dimensions%subdimensions==0
                mem=self.net.make_array('mem',N_per_D*subdimensions,dimensions/subdimensions,dimensions=subdimensions,quick=True)
                recall=self.net.make_array('recall',N_per_D*subdimensions,dimensions/subdimensions,dimensions=subdimensions,quick=True)
            
        if feedback!=0:
            if N_per_D==0:
                def limit(x):
                    a=numeric.array(x)
                    norm=numeric.norm(a)
                    if norm>radius: a=a/(norm/radius)
                    return a
                self.net.connect(mem,mem,pstc=pstc_feedback,func=limit)
            else:
                self.net.connect(mem,mem,pstc=pstc_feedback)

        self.net.connect(mem,deconv,transform=nef.convolution.input_transform(dimensions,True),pstc=pstc)

        self.net.connect(conv,mem,func=lambda x: x[0]*x[1],transform=nef.convolution.output_transform(dimensions)*input_scale,pstc=pstc)

        self.net.connect(deconv,recall,func=lambda x: x[0]*x[1],transform=nef.convolution.output_transform(dimensions),pstc=pstc_recall)

        self.add_sink(MemorySink(self,True),'A')
        self.add_sink(MemorySink(self,False),'B')
        self.add_sink(MemorySink(self,False,True),'request')

        self.add_source(mem.getOrigin('X'))
        self.add_source(recall.getOrigin('X'),'recall')

        if len(cleanup)>0:
            clean=self.net.make('clean',1,dimensions,quick=True,mode='direct')
            self.add_source(clean.getOrigin('X'),'clean')
            vocab=self.spa.vocab(self.name)
            for p in cleanup:
                v=(vocab.parse(p)*output_scale).v
                c=self.net.make('c_'+p,50,1,quick=True,mode='rate',intercept=(0.5,1),encoders=[[1]])
                def threshold(x):
                    if x[0]>0.5: return 1
                    else: return 0
                self.net.connect(recall,c,transform=v,pstc=pstc_recall)
                self.net.connect(c,clean,transform=v,func=threshold,pstc=pstc_recall)
                
                
                
            
        
        
