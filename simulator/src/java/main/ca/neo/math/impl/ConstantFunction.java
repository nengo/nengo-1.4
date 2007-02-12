/*
 * Created on 6-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;

/**
 * A Function that maps everything to the same value. 
 * 
 * @author Bryan Tripp
 */
public class ConstantFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private int myDimension;
	private float myValue;
	
	/**
	 * @param dimension Input dimension of this Function
	 * @param value Constant output value of this Function 
	 */
	public ConstantFunction(int dimension, float value) {
		myDimension = dimension;
		myValue = value;
	}

	/**
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myDimension;
	}

	/**
	 * @return The constant value given in the constructor 
	 * 
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return myValue;
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = myValue;
		}
		
		return result;
	}

}
