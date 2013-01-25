import module
import numeric

class Cleanup(module.Module):
    def init(self, dimensions=16):
        self.net.make('input', 1, dimensions, mode='direct')
        self.net.make('output', 1, dimensions, mode='direct')


        self.spa.add_source(self, 'output')        
        self.spa.add_sink(self, 'input')
        
    def complete(self, N_per_D=30, scaling=1, min_intercept=0.1, mutual_inhibit=0, feedback=0, pstc_feedback=0.01):
        vocab=self.spa.sources[self.name]
            
        self.net.make_array('cleanup', 50, len(vocab.keys), intercept=(min_intercept,1), encoders=[[1]])
        transform=[vocab.parse(k).v for k in vocab.keys]
        self.net.connect('input','cleanup',transform=transform, pstc=0.001)
            
        t=numeric.zeros((vocab.dimensions,len(vocab.keys)),typecode='f')
        for i in range(len(vocab.keys)):
            t[:,i]+=vocab.parse(vocab.keys[i]).v*scaling
        self.net.connect('cleanup','output',transform=t, pstc=0.001)#, func=lambda x: 1)            
        
        if mutual_inhibit!=0 or feedback!=0:
            t=(numeric.eye(len(vocab.keys))-1)*mutual_inhibit
            t+=numeric.eye(len(vocab.keys))*feedback
            self.net.connect('cleanup','cleanup',transform=t, pstc=pstc_feedback)
        

