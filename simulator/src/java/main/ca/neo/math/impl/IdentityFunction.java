/*
 * Created on 14-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;

/**
 * Identity function on a particular dimension of input, ie f(x) = x_i, 
 * where i is a constant.
 * 
 * @author Bryan Tripp
 */
public class IdentityFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private int myDimension;
	private int myIdentityDimension;
	
	/**
	 * @param dimension Dimension of input vector  
	 * @param i Index (from 0) of input vector of which this function is 
	 * 		an identity 
	 */
	public IdentityFunction(int dimension, int i) {
		if (i >= dimension || i < 0) {
			throw new IllegalArgumentException("Index " + i + " is out of range");
		}
		
		if (dimension <= 0) {
			throw new IllegalArgumentException("Dimension must be a +ve integer");
		}
		
		myDimension = dimension;
		myIdentityDimension = i;
	}

	/**
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myDimension;
	}
	
	/**
	 * @return Index on input vector of which this funciton is an identity
	 */
	public int getIdentityDimension() {
		return myIdentityDimension;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return from[myIdentityDimension];
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = from[i][myIdentityDimension];
		}
		
		return result;
	}
	
}
