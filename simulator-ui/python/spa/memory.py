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
    def create(self,dimensions,N_per_D=30,pstc=0.01,output_scale=2,input_scale=0.1,feedback=1,pstc_feedback=0.1):

        conv=nef.convolution.make_array(self.net,'conv',300,dimensions,quick=True)
        deconv=nef.convolution.make_array(self.net,'deconv',300,dimensions,quick=True)

        recall=self.net.make('recall',N_per_D*dimensions,dimensions,quick=True)

        mem=self.net.make('mem',N_per_D*dimensions,dimensions,quick=True)
        if feedback!=0:
            self.net.connect(mem,mem,pstc=pstc_feedback)

        self.net.connect(mem,deconv,transform=nef.convolution.input_transform(dimensions,True),pstc=pstc)

        self.net.connect(conv,mem,func=lambda x: x[0]*x[1],transform=nef.convolution.output_transform(dimensions)*input_scale,pstc=pstc)

        self.net.connect(deconv,recall,func=lambda x: x[0]*x[1],transform=nef.convolution.output_transform(dimensions)*output_scale,pstc=pstc)

        self.add_sink(MemorySink(self,True),'A')
        self.add_sink(MemorySink(self,False),'B')
        self.add_sink(MemorySink(self,False,True),'request')

        self.add_source(mem.getOrigin('X'))
        self.add_source(recall.getOrigin('X'),'recall')
        
        
