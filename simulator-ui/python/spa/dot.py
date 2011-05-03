import ca.nengo
import nef
import numeric

class DotProduct(ca.nengo.model.impl.NetworkImpl):
    def __init__(self,name=None,N=30,pstc=0.002,**inputs):
        ca.nengo.model.impl.NetworkImpl.__init__(self)
        self.input1,self.input2=inputs.keys()
        self.weight1=inputs[self.input1]
        self.weight2=inputs[self.input2]
        self.name='dot_%s_%s'%(self.input1,self.input2)
        self.N=N
        self.pstc=pstc
        self.scalar_sources=['value']

    def init_NCA(self,nca):
        d1=nca.get_dimension(self.input1)
        d2=nca.get_dimension(self.input2)
        v1=nca.vocab(self.input1)
        v2=nca.vocab(self.input2)

        dim=max(d1,d2)

        net=nef.Network(self)
        self.inputs=net.make_array('inputs',self.N*2,dim,dimensions=2,encoders=[[1,1],[1,-1],[-1,-1],[-1,1]],quick=True)
        #self.dp=net.make('dp',self.N,1,quick=True)

        

        def product(x):
            return x[0]*x[1]
        self.inputs.addDecodedOrigin('product',[nef.functions.PythonFunction(product,dim)],'AXON')
                            
        self.exposeOrigin(self.inputs.getOrigin('product'),'product')
        nca.add_scalar_source(self.name,self.getOrigin('product'),numeric.ones((1,dim),'f'))
                        

        t1=numeric.zeros((dim*2,dim),typecode='f')
        t2=numeric.zeros((dim*2,dim),typecode='f')
        for i in range(dim):
            t1[i*2,i]=1.0
            t2[i*2+1,i]=1.0

        t1=t1*self.weight1
        t2=t2*self.weight2
            
            
        self.inputs.addDecodedTermination(self.input1,t1,self.pstc,False)
        self.inputs.addDecodedTermination(self.input2,t2,self.pstc,False)
        self.exposeTermination(self.inputs.getTermination(self.input1),self.input1)
        self.exposeTermination(self.inputs.getTermination(self.input2),self.input2)
    def connect_NCA(self,nca):
        o1=nca._sources[self.input1]
        o2=nca._sources[self.input2]
        nca._net.connect(o1,self.getTermination(self.input1))
        nca._net.connect(o2,self.getTermination(self.input2))


        
        
        
        
