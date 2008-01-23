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
	 * @return A rule that defines how the termination's transformation matrix elements change during simulation
	 * @throws StructuralException if the named Termination does not exist
	 */
//	public PlasticityRule getPlasticityRule(String terminationName) throws StructuralException;
	
	/**
	 * @param terminationName Name of termination to which plasticity rule applies.  
	 * @param rule A rule that defines how the termination's transformation matrix elements change during simulation
	 * @throws StructuralException if the named Termination does not exist
	 */
	public void setPlasticityRule(String terminationName, PlasticityRule rule) throws StructuralException;

	/**
	 * @return Period after which plasticity rules are evaluated (defaults to every time step).
	 */
//	public float getPlasticityInterval();
	
	/**
	 * @param time Period after which plasticity rules are evaluated (defaults to every time step).  
	 */
	public void setPlasticityInterval(float time);
	
	/**
	 * @return Names of Terminations for which plasticity rules can be set 
	 */
//	public String[] getPlasticityRuleNames();
	
}
