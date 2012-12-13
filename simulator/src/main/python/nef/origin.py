from theano import tensor as TT
from theano.tensor.shared_randomstreams import RandomStreams
import theano
import numpy
import neuron

# generate sample points uniformly distributed within the sphere
def make_samples(neurons,dimensions,srng):
    samples=srng.normal((neurons,dimensions))
    norm=TT.sum(samples*samples,axis=[1],keepdims=True)
    samples=samples/TT.sqrt(norm)        
    
    scale=srng.uniform([neurons])**(1.0/dimensions)
    samples=samples.T*scale
    
    return theano.function([],samples)()



class Origin:
    def __init__(self,ensemble,func=None):
        self.ensemble=ensemble
        self.func=func
        self.decoder=self.compute_decoder()
        self.dimensions=self.decoder.shape[1]*self.ensemble.count
        self.value=theano.shared(numpy.zeros(self.dimensions).astype('float32'))
    
    # the theano computation for converting neuron output into a decoded value
    def update(self,spikes):
        return {self.value:TT.unbroadcast(TT.dot(spikes,self.decoder).reshape([self.dimensions]),0)} 
        
    def compute_decoder(self):    
        srng = RandomStreams(seed=self.ensemble.seed)
        
        #TODO: have this be more for higher dimensions?  5000 maximum (like Nengo)?
        S=500
                
        samples=make_samples(S,self.ensemble.dimensions,srng)

        # compute the target values (which are the same as the sample points for the 'X' origin)        
        if self.func is None:
            values=samples
        else:
            values=numpy.array([self.func(s) for s in samples.T])
            if len(values.shape)<2: values.shape=values.shape[0],1
            values=values.T
        
        # compute the input current for every neuron and every sample point
        J=numpy.dot(self.ensemble.encoders,samples)
        J+=numpy.array([self.ensemble.bias]).T
        
        # generate an array of ensembles, one ensemble per sample point
        neurons=self.ensemble.neuron.__class__((self.ensemble.neurons,S),t_rc=self.ensemble.neuron.t_rc,t_ref=self.ensemble.neuron.t_ref)
        
        # run the neuron model for 1 second, accumulating spikes to get a spike rate
        #  TODO: is this enough?  Should it be less?  If we do less, we may get a good noise approximation!
        A=neuron.accumulate(J,neurons)
        
        # compute Gamma and Upsilon
        G=numpy.dot(A,A.T)
        U=numpy.dot(A,values.T)
        
        #TODO: optimize this so we're not doing the full eigenvalue decomposition
        #TODO: add NxS method for large N?
        
        w,v=numpy.linalg.eigh(G)
        limit=0.1*0.1*max(w)        
        for i in range(len(w)):
            if w[i]<limit: w[i]=0
            else: w[i]=1.0/w[i]
        Ginv=numpy.dot(v,numpy.multiply(w[:,numpy.core.newaxis],v.T))
        
        #Ginv=numpy.linalg.pinv(G)  
        
        # compute decoder
        decoder=numpy.dot(Ginv,U)/(self.ensemble.neuron.dt)
        return decoder.astype('float32')

