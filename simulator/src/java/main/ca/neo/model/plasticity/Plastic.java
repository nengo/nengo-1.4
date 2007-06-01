/*
 * Created on 15-Mar-07
 */
package ca.neo.model.plasticity;

import ca.neo.model.StructuralException;

/**
 * Something is <code>Plastic</code> if it contains one or more terminations that can be 
 * modified (over simulation time) on the basis of a PlasticityRule.
 *   
 * @author Bryan Tripp
 */
public interface Plastic {

	/**
	 * @param terminationName Name of termination to which plasticity rule applies.  
	 * @param rule A rule that defines how the termination's transformation matrix elements change during simulation
	 * @throws StructuralException if the named Termination does not exist
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException;
	
	/**
	 * @param time Period after which plasticity rules are evaluated (defaults to every time step).  
	 */
	public void setPlasticityInterval(float time);
	
}
