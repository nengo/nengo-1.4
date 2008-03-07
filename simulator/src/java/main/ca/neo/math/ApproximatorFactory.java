/*
 * Created on 20-Feb-07
 */
package ca.neo.math;

import java.io.Serializable;

/**
 * Produces LinearApproximators, which approximate Functions through a weighted sum of component
 * functions. The component functions are given as lists of evaluation points and corresponding 
 * values.  
 * 
 * @author Bryan Tripp
 */
public interface ApproximatorFactory extends Serializable, Cloneable {
	
	/**
	 * @param evalPoints Points at which component functions are evaluated. These should 
	 * 		usually be uniformly distributed, because the sum of error at these points is 
	 * 		treated as an integral over the domain of interest. 
	 * @param values The values of component funcitons at the evalPoints. The first dimension 
	 * 		makes up the list of functions, and the second the values of these functions at each 
	 * 		evaluation point.
	 * @return A LinearApproximator that can be used to approximate new Functions as a wieghted 
	 * 		sum of the given components.  
	 */
	public LinearApproximator getApproximator(float[][] evalPoints, float[][] values);
	
	public ApproximatorFactory clone() throws CloneNotSupportedException;
	
}
