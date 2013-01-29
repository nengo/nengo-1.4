import module
import numeric

class Buffer(module.Module):
    def init(self, dimensions=16, N_per_D=30, feedback=1, pstc_feedback=0.01, array_dimensions=None, intercept=None):
        if intercept is None: intercept=(-1,1)
        else: intercept=(intercept,1)
        if array_dimensions is None:
            self.net.make('buffer', N_per_D*dimensions, dimensions, intercept=intercept)
        else:
            self.net.make_array('buffer', N_per_D*array_dimensions, length=dimensions/array_dimensions, dimensions=array_dimensions, intercept=intercept)    

        if feedback!=0:
            self.net.connect('buffer','buffer',weight=feedback, pstc=pstc_feedback)                    

        self.spa.add_source(self, 'buffer')        
        self.spa.add_sink(self, 'buffer')
        
    """        
    def complete(self, recurrent_cleanup=0):
        if feedback==0 and recurrent_cleanup==0: return
    
        vocab=self.spa.sources[self.name]
        t=numeric.zeros((vocab.dimensions,vocab.dimensions),typecode='f')

        if recurrent_cleanup!=0:
            for pre_term in vocab.keys:
                pre=vocab.parse(pre_term).v*recurrent_cleanup
                post=numeric.zeros(vocab.dimensions,typecode='f')
                for post_term in vocab.keys:
                    if pre_term!=post_term:
                        post+=vocab.parse(post_term).v
                t+=numeric.array([pre*bb for bb in post])
                
        if feedback!=0:
            for i in range(vocab.dimensions):
                t[i][i]+=feedback
"""                
        
    def complete(self, recurrent_cleanup=0, pstc_feedback=0.01):
        if recurrent_cleanup!=0:
            vocab=self.spa.sources[self.name]
            
            self.net.make_array('cleanup', 50, len(vocab.keys), intercept=(0,1), encoders=[[1]])
            transform=[vocab.parse(k).v for k in vocab.keys]
            self.net.connect('buffer','cleanup',transform=transform, pstc=pstc_feedback)
            
            t=numeric.zeros((vocab.dimensions,len(vocab.keys)),typecode='f')
            for i in range(len(vocab.keys)):
                for j in range(len(vocab.keys)):
                    if i!=j:
                        t[:,i]+=vocab.parse(vocab.keys[j]).v*recurrent_cleanup
                    else:    
                        t[:,i]-=vocab.parse(vocab.keys[j]).v*recurrent_cleanup
            self.net.connect('cleanup','buffer',transform=t, pstc=pstc_feedback, func=lambda x: 1)            
        
