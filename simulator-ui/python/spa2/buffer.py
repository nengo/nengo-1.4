import module

class Buffer(module.Module):
    def init(self, dimensions=16, feedback=1, pstc_feedback=0.01, N_per_D=30):
        self.net.make('buffer', N_per_D*dimensions, dimensions)
        if feedback>0:
            self.net.connect('buffer','buffer',weight=feedback,pstc=pstc_feedback)
        
        self.spa.add_source(self, 'buffer')        
        self.spa.add_sink(self, 'buffer')
