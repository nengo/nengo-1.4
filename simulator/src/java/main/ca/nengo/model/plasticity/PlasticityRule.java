/*
 * Created on 29-Jan-2007
 */
package ca.nengo.model.plasticity;

import java.io.Serializable;

import ca.nengo.model.InstantaneousOutput;

/**
 * Specifies how the termination weights of an NEFEnsemble are modified depending 
 * on presynaptic and postsynaptic state.
 * 
 * TODO: the setters introduce state in a way that may not be necessary -- remove them or clone in EnsembleImpl.setPlasticityRule(...)
 * 
 * @author Bryan Tripp
 */
public interface PlasticityRule extends Serializable, Cloneable {
	
	/**
	 * Provides potentially modulatory input to the rule.
	 *   
	 * Note that although modulatory input will most commonly be used here, an NEFEnsemble 
	 * will provide outputs of all Terminations via this method regardless of whether a 
	 * Termination has property Termination.MODULATORY=="true". 
	 * 
	 * @param name The name of a DecodedTermination onto the ensemble
	 * @param state The present value of output from the named Termination (may differ 
	 * 		from its input in terms of dynamics and dimension)
	 * @param time Simulation time at which state arrives at site of plasticity 
	 */
	public void setTerminationState(String name, InstantaneousOutput state, float time);
	
	/**
	 * Provides state or functional output, which may serve as an indication of 
	 * postsynaptic activity (used in Hebbian learning). 
	 *  
	 * @param name The name of a DecodedOrigin from the ensemble 
	 * @param state The present value of output from the named Origin 
	 * @param time Simulation time at which state arrives at site of plasticity 
	 */
	public void setOriginState(String name, InstantaneousOutput state, float time);
	
	/**
	 * @param transform The present transformation matrix of a Termination
	 * @param input The present input to the Termination 
	 * @param time Simulation time at which input arrives at site of plasticity
	 * @return The rate of change of each element in the transform (units per second) if input is RealOutput, 
	 * 		otherwise the increment of each element in the transform
	 */
	public float[][] getDerivative(float[][] transform, InstantaneousOutput input, float time); 
	
	public PlasticityRule clone() throws CloneNotSupportedException;
	
}
