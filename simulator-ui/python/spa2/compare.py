import module
import numeric

class Compare(module.Module):
    def init(self, dimensions=16, N_per_D=30, array_dimensions=16, N_multiply=200, output_scaling=1.0, radius=1.0):
    
        if array_dimensions is None:
            self.net.make('input1', N_per_D*dimensions, dimensions, radius=radius)
            self.net.make('input2', N_per_D*dimensions, dimensions, radius=radius)
            self.net.make('output', N_per_D*dimensions, dimensions, radius=radius)
        else:
            rad = radius / float(dimensions/array_dimensions)
            self.net.make_array('input1', N_per_D*array_dimensions, length=dimensions/array_dimensions, dimensions=array_dimensions, radius=rad)    
            self.net.make_array('input2', N_per_D*array_dimensions, length=dimensions/array_dimensions, dimensions=array_dimensions, radius=rad)    
            self.net.make_array('output', N_per_D*array_dimensions, length=dimensions/array_dimensions, dimensions=array_dimensions, radius=rad)    

        self.spa.add_sink(self, 'input1', name='1')        
        self.spa.add_sink(self, 'input2', name='2')        
        self.spa.add_source(self, 'output')

        self.net.make_array('compare', N_multiply, length=dimensions, dimensions=2, 
                    encoders=[[1,1], [1,-1], [-1,1], [-1,-1]])
        
        t1=numeric.zeros((dimensions*2,dimensions),typecode='f')
        t2=numeric.zeros((dimensions*2,dimensions),typecode='f')
        for i in range(dimensions):
            t1[i*2,i]=1.0
            t2[i*2+1,i]=1.0
        
        self.net.connect('input1', 'compare', transform=t1)
        self.net.connect('input2', 'compare', transform=t2)
        
        def multiply(x):
            return [x[0]*x[1]]
            
        vocab=self.spa.sources[self.name]
        
        transform = [vocab.parse('YES').v for i in range(dimensions)]
        transform = numeric.array(transform).T
        
        self.net.connect('compare', 'output', func=multiply, 
                transform=transform*output_scaling)
