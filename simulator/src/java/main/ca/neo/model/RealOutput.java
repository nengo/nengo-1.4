/*
 * Created on May 18, 2006
 */
package ca.neo.model;

/**
 * Instantaneous output consisting of continuous quantities (e.g. firing rates or 
 * represented variables).  
 * 
 * @author Bryan Tripp
 */
public interface RealOutput extends InstantaneousOutput {

	/**
	 * @return Instantaneous activity within real-valued channels. A real-valued channel could 
	 * 		correspond to an axon, a lumped group of axons, a gap-junctional connection, the 
	 * 		concentration of some substance in inter-cellular space, etc. Alternatively, each  
	 * 		value can correspond to the decoded estimate of one dimension in a vector that 
	 * 		is represented by activity in an NEFEnsemble, or to some function thereof. 
	 */
	public float[] getValues();
}
