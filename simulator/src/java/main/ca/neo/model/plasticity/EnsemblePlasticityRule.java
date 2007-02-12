/*
 * Created on 29-Jan-2007
 */
package ca.neo.model.plasticity;

/**
 * Specifies how the termination weights of an NEFEnsemble are modified depending 
 * on presynaptic and postsynaptic state.
 * 
 * @author Bryan Tripp
 */
public interface EnsemblePlasticityRule {
	
	/**
	 * Provides potentially modulatory input to the rule.
	 *   
	 * Note that an NEFEnsemble will provide outputs of all Terminations via this method 
	 * regardless of whether a Termination has property Termination.MODULATORY=="true". 
	 * 
	 * @param name The name of a DecodedTermination onto the ensemble
	 * @param state The present value of output from the named Termination (may differ 
	 * 		from its input in terms of dynamics and dimension)
	 */
	public void setTerminationState(String name, float[] state);
	
	/**
	 * Provides state or functional output, which may serve as an indication of 
	 * postsynaptic activity (used in Hebbian learning). 
	 *  
	 * @param name The name of a DecodedOrigin from the ensemble 
	 * @param state The present value of output from the named Origin 
	 */
	public void setOriginState(String name, float[] state);
	
	/**
	 * @param transform The present transformation matrix of a Termination
	 * @param input The present input to the Termination 
	 * @return The rate of change of each element in the transform (units per second)
	 */
	public float[][] getDerivative(float[][] transform, float[] input); 
	
}
