/*
 * Created on 1-Dec-2006
 */
package ca.nengo.math;

/**
 * Finds a Function that fits a set of a example points in some sense (e.g. 
 * least-squares). For example, least-squares polynomial approximation and 
 * spline interpolation are possibly implementations. 
 *  
 * @author Bryan Tripp
 */
public interface CurveFitter extends Cloneable {

	/**
	 * @param x Example x points 
	 * @param y Example y points (must be same length as x)
	 * @return A Function that approximates the mapping Y=f(X) exemplified by x and y.   
	 */
	public Function fit(float[] x, float[] y);
	
	public CurveFitter clone() throws CloneNotSupportedException;
	
}
