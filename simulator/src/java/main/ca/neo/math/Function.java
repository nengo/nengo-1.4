/*
 * Created on May 16, 2006
 */
package ca.neo.math;

import java.io.Serializable;

import ca.neo.config.Configurable;

/**
 * <p>A mathematical function from an n-D space to a 1-D space. For simplicity we always 
 * map to a 1-D space, and model maps to n-D spaces with n Functions.</p>
 * 
 * <p>Instances of Function are immutable once they are created (ie their parameters 
 * do not change over time).</p>
 *  
 * @author Bryan Tripp
 */
public interface Function extends Serializable, Configurable {
	
	/**
	 * @return Dimension of the space that the Function maps from 
	 */
	public int getDimension();

	/**
	 * @param from Must have same length as getDimension() 
	 * @return result of function operation on arg 
	 */
	public float map(float[] from);

	/**
	 * @param from An array of arguments; each element must have length getDimension().   
	 * @return Array of results of function operation on each arg
	 */
	public float[] multiMap(float[][] from);
	
}
