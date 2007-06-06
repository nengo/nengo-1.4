/*
 * Created on May 16, 2006
 */
package ca.neo.model.nef;

import ca.neo.math.Function;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.impl.BiasOrigin;
import ca.neo.model.nef.impl.BiasTermination;
import ca.neo.model.nef.impl.DecodedTermination;
import ca.neo.model.plasticity.Plastic;

/**
 * <p>A group of Nodes that represent a scalar, vector, or function, as 
 * characterized in Eliasmith & Anderson's Neural Engineering Framework.</p>
 * 
 * <p>All Nodes in an NEFEnsemble must be NEFNodes.</p>
 * 
 * @author Bryan Tripp
 */
public interface NEFEnsemble extends DecodableEnsemble, Plastic {
	
	/**
	 * Standard name for the Origin corresponding to the decoded estimate of the state variables 
	 * that Ensemble represents (X is a standard name for state variables in state-space models). 
	 */
	public static final String X = "X"; 
	
	/**
	 * @return Dimension of represented state space (eg 1 for scalar representation)
	 */
	public int getDimension();

	/**
	 * @return List of encoders for each Node (each item is the encoding vector for a Node).
	 */
	public float[][] getEncoders();

	/**
	 * Adds an Origin that corresponds to a decoding of the activities of Nodes in this Ensemble. 
	 *   
	 * @param name Name of decoding 
	 * @param functions Functions that define the decoding (one function for each dimension of output).  
	 * 		All functions must have an input dimension equal to the dimension of this NEFEnsemble.
	 * @param nodeOrigin Name of the Node-level Origins from which this Ensemble-level Origin is derived
	 * 		(often Neuron.AXON)
	 * @throws StructuralException if functions do not all have the same input dimension as the 
	 * 		dimension of this ensemble   
	 */
	public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin) throws StructuralException;

	/**
	 * Adds a BiasOrigin, which operates in parallel with an existing Origin, such that the effective weights of the 
	 * two origins together are all of the same sign (as is normally the case with synaptic weights in the brain). 
	 *   
	 * TODO: with a little work this could be pushed up to DecodableEnsemble (have to generalize constant-rate responses)
	 * 
	 * @param existing An existing Origin on this NEFEnsemble
	 * @param numInterneurons Number of neurons 
	 * @param excitatory If true, effective weights will be positive; if false they will be negative (inhibitory)
	 * @return Resulting BiasOrigin
	 * @throws StructuralException if given Origin is not a DecodedOrigin or if there is a construction problem 
	 */
	public BiasOrigin addBiasOrigin(Origin existing, int numInterneurons, boolean excitatory) throws StructuralException;
		
	/**
	 * Adds a new Termination into this Ensemble, at which information is to be received 
	 * in the form of decoded state variables rather than spikes, etc.  
	 *  
	 * @param name Unique name for this Termination (in the scope of this Ensemble)
	 * @param matrix Transformation matrix which defines a linear map on incoming information, 
	 * 		onto the space of vectors that can be represented by this NEFEnsemble. The first dimension 
	 * 		is taken as matrix rows, and must have the same length as the Origin that will be connected 
	 * 		to this Termination. The second dimension is taken as matrix columns, and must have the same 
	 * 		length as the encoders of this NEFEnsemble. TODO: this is transposed?  
	 * @param tauPSC Time constant of post-synaptic current decay (all Terminations have    
	 * 		this property but it may have slightly different interpretations depending other properties
	 * 		of the Termination). 
	 * @param isModulatory If true, inputs to this Termination do not drive Nodes in the Ensemble directly 
	 * 		but may have modulatory influences (eg related to plasticity). If false, the transformation matrix
	 * 		output dimension must match the dimension of this Ensemble.   
	 * @return The resulting Termination
	 * @throws StructuralException if given transformation matrix is not a matrix
	 */
	public Termination addDecodedTermination(String name, float[][] matrix, float tauPSC, boolean isModulatory) 
		throws StructuralException;
	
	/**
	 * As above but with arbitrary single-input-single-output PSC dynamics. 
	 *  
	 * @param name Unique name for this Termination (in the scope of this Ensemble)
	 * @param matrix Transformation matrix which defines a linear map on incoming information 
	 * @param tfNumerator Coefficients of transfer function numerator (see CanonicalModel.getRealization(...) 
	 * 		for details) 
	 * @param tfDenominator Coefficients of transfer function denominator
	 * @param isModulatory If true, inputs to this Termination do not drive Nodes in the Ensemble directly 
	 * 		but may have modulatory influences (eg related to plasticity). If false, the transformation matrix
	 * 		output dimension must match the dimension of this Ensemble.   
	 * @return The resulting Termination
	 * @throws StructuralException if given transformation matrix is not a matrix or there is a problem 
	 * 		with the transfer function
	 */
	public Termination addDecodedTermination(String name, float[][] matrix, float[] tfNumerator, float[] tfDenominator, 
			float passthrough, boolean isModulatory) throws StructuralException;
	
	/**
	 * Adds BiasTerminations, which are meant to receive projections from BiasOrigins. A pair of BiasTerminations is returned, 
	 * one to receive a projection directly from a BiasOrigin, and the other to receive a projection indirectly through an 
	 * ensemble of interneurons that is associated with the BiasOrigin. 
	 *   
	 * @param baseTermination The Termination that is to be biased (so that projections to it consist of weights of a single sign) 
	 * @param interneuronTauPSC Time constant of post-synaptic current decay  of inhibitory termination from interneurons onto this ensemble 
	 * @param biasDecoder The decoding weight of the associated BiasOrigin (decoders are uniform in BiasOrigin, so this can be obtained from 
	 * 		BiasOrigin.getDecoders()[0][0]). 
	 * @param functionDecoders The decoding vectors of the BiasOrigin's associated base origin
	 * @return A pair of BiasTerminations: the first is to receive direct input from a BiasOrigin and the second is to receive input from 
	 * 		the interneuron ensemble associated with the BiasOrigin (see BiasOrigin.getInterneurons()) 
	 * @throws StructuralException
	 */
	public BiasTermination[] addBiasTerminations(DecodedTermination baseTermination, 
			float interneuronTauPSC, float biasDecoder, float[][] functionDecoders) throws StructuralException;
	
	/**
	 * @param name Name of Termination to remove. 
	 */
	public void removeDecodedTermination(String name);
	
}
