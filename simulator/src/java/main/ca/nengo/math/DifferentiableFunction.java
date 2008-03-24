/*
 * Created on 1-Dec-2006
 */
package ca.nengo.math;

/**
 * A Function with a known derivative. 
 *  
 * @author Bryan Tripp
 */
public interface DifferentiableFunction extends Function {

	/**
	 * @return The derivative of this Function
	 */
	public Function getDerivative();
	
}
