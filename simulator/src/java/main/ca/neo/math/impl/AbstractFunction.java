package ca.neo.math.impl;

import ca.neo.math.Function;

/**
 * Base class for Function implementations. The default implementation of 
 * multiMap() calls map(). This will be a little slower than if both methods 
 * were to call a static function, so if multiMap speed is an issue this 
 * method could be overridden, or it might be better not to use this abstract class.  
 * 
 * @author Bryan Tripp
 */
public abstract class AbstractFunction implements Function {

	private int myDim;
	
	/**
	 * @param dim Input dimension of the function
	 */
	public AbstractFunction(int dim) {
		myDim = dim;
	}
	
	/**
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myDim;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public abstract float map(float[] from);

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = map(from[i]);
		}
		
		return result;
	}
	
}
