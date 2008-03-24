/*
 * Created on May 5, 2006
 */
package ca.nengo.model;

import java.io.Serializable;

/**
 * A connection between an Origin and a Termination. 
 *    
 * @author Bryan Tripp
 */
public interface Projection extends Serializable {

	/**
	 * @return Origin of this Projection (where information comes from)
	 */
	public Origin getOrigin();
	
	/**
	 * @return Termination of this Projection (where information goes)
	 */
	public Termination getTermination();
	
	/**
	 * @return The Network to which this Projection belongs
	 */
	public Network getNetwork();

	/**
	 * Makes all the synaptic weights in the projection either positive or negative, so that the projection 
	 * accords with Dale's principle. This introduces a bias current postsynaptically, which is a function 
	 * of presynaptic activity. This bias is removed by projecting the same function through an ensemble 
	 * of interneurons. See Parisien, Anderson & Eliasmith, 2007, Neural Computation for more detail.  
	 *   
	 * @param numInterneurons Number of interneurons through which bias function is projected
	 * @param tauInterneurons Time constant of post-synaptic current in projection from presynaptic ensemble to interneurons
	 * @param tauBias Time constant of post-synaptic current in projection from interneurons to postsynaptic ensemble
	 * @param excitatory If true, synapses in main projection are made excitatory; if false, inhibitory 
	 * @param optimize If true, performs optimizations to minimize distortion in the parallel projection through interneurons
	 * @throws StructuralException
	 */
	public void addBias(int numInterneurons, float tauInterneurons, float tauBias, boolean excitatory, boolean optimize) throws StructuralException;
	
	/**
	 * Deletes bias-related interneurons, projections, origins, and terminations.  
	 */
	public void removeBias();

	/**
	 * @param enable If true, and initializeBias(...) has been called, then bias is enabled; if false it is disabled (default true) 
	 */
	public void enableBias(boolean enable);
	
	/**
	 * @return true if bias is enabled
	 */
	public boolean biasIsEnabled();
	
	/**
	 * @return Matrix of weights in this Projection (if there are neurons on each end, then these are synaptic weights) 
	 */
	public float[][] getWeights();
	
}
