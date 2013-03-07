from theano import tensor as TT
from theano.tensor.shared_randomstreams import RandomStreams
import theano
import numpy
import neuron

def make_samples(neurons, dimensions, srng):
    """Generate sample points uniformly distributed within the sphere
    Returns float array of sample points
    
    :param int neurons: number of neurons to generate samples for
    :param int dimensions: dimensionality of sphere to generate points in
    :param theano.tensor.shared_randomstreams srng: theano random number generator
    """
    samples = srng.normal((neurons, dimensions)) # get samples from normal distribution
    # normalize magnitude of sampled points to be of unit length
    norm = TT.sum(samples * samples, axis=[1], keepdims=True) 
    samples = samples / TT.sqrt(norm)

    # generate magnitudes for vectors from uniform distribution
    scale = srng.uniform([neurons])**(1.0 / dimensions) 
    samples = samples.T * scale # scale sample points
    
    return theano.function([],samples)()

class Origin:
    def __init__(self, ensemble, func=None):
        """The output to a population of neurons (ensemble), performing a transformation (func) on the represented value

        :param Ensemble ensemble: the Ensemble to which this origin is attached
        :param function func: the transformation to perform to the ensemble's represented values to get the output value
        """
        self.ensemble = ensemble
        self.func = func
        self.decoder = self.compute_decoder()
        self.dimensions = self.decoder.shape[1]*self.ensemble.array_size
        self.projected_value = theano.shared(numpy.zeros(self.dimensions).astype('float32'))
    
    def compute_decoder(self):     
        """Calculate the scaling values to apply to the output to each of the neurons in the attached 
        population such that the weighted summation of their output generates the desired decoded output.
        Decoder values computed as D = (A'A)^-1 A'X_f where A is the matrix of activity values of each 
        neuron over sampled X values, and X_f is the vector of desired f(x) values across sampled points
        """
        srng = RandomStreams(seed=self.ensemble.seed) # theano random number generator
        
        #TODO: have this be more for higher dimensions?  5000 maximum (like Nengo)?
        S=500
               
        # generate sample points from state space randomly to minimize error over in decoder calculation
        samples = make_samples(S, self.ensemble.dimensions, srng) 

        # compute the target projected_values at the sampled points (which are the same as the sample points for the 'X' origin)      ?????????? what does this ( ) part mean?
        if self.func is None: # if no function provided, use identity function as default
            projected_values = samples 
        else: # otherwise calculate target projected_values using provided function
            projected_values=numpy.array([self.func(s) for s in samples.T]) 
            if len(projected_values.shape)<2: projected_values.shape=projected_values.shape[0],1
            projected_values=projected_values.T
        
        # compute the input current for every neuron and every sample point
        J = numpy.dot(self.ensemble.encoders, samples)
        J += numpy.array([self.ensemble.bias]).T
        
        # duplicate attached population of neurons into array of ensembles, one ensemble per sample point
        # so in parallel we can calculate the activity of all of the neurons at each sample point 
        neurons = self.ensemble.neuron.__class__((self.ensemble.neurons, S), tau_rc=self.ensemble.neuron.tau_rc, tau_ref=self.ensemble.neuron.tau_ref)
        
        # run the neuron model for 1 second, accumulating spikes to get a spike rate
        #  TODO: is this enough?  Should it be less?  If we do less, we may get a good noise approximation!
        A = neuron.accumulate(J, neurons)
        
        # compute Gamma and Upsilon
        G = numpy.dot(A, A.T)
        U = numpy.dot(A, projected_values.T)
        
        #TODO: optimize this so we're not doing the full eigenvalue decomposition
        #TODO: add NxS method for large N?
        
        #TODO: compare below with pinv rcond
        w, v = numpy.linalg.eigh(G) # eigh for symmetric matrices, returns evalues w, and normalized evectors v
        limit = .01 * max(w) # formerly 0.1 * 0.1 * max(w), set threshold 
        for i in range(len(w)):
            if w[i] < limit: w[i] = 0 # if < limit set eval = 0
            else: w[i] = 1.0 / w[i] # prep for upcoming Ginv calculation                                                       ?????????????????????????????????????????????? 
        # w[:, np.core.newaxis] gives transpose of vector, np.multiply is very fast element-wise multiplication
        Ginv = numpy.dot(v, numpy.multiply(w[:, numpy.core.newaxis], v.T)) 
        
        #Ginv=numpy.linalg.pinv(G, rcond=.01)  
        
        # compute decoder - least squares method 
        decoder = numpy.dot(Ginv, U) / (self.ensemble.neuron.dt)
        return decoder.astype('float32')

    def update(self, spikes):
        """The theano computation for converting neuron output into a decoded value
        Returns a dictionary with the decoded output value

        :param array spikes: theano object representing the instantaneous spike raster from the attached population
        """
        return {self.projected_value:TT.unbroadcast(TT.dot(spikes,self.decoder).reshape([self.dimensions]), 0)} 
        
