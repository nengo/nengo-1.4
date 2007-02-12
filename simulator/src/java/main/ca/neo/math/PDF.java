/*
 * Created on May 16, 2006
 */
package ca.neo.math;

/**
 * Probability density function. 
 * 
 * @author Bryan Tripp
 */
public interface PDF extends Function {

	/**
	 * @return A random sample from this density
	 */
	public float[] sample();
	
}
