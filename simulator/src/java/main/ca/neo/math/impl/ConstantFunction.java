/*
 * Created on 6-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;
import ca.neo.model.Configuration;
import ca.neo.model.impl.ConfigurationImpl;

/**
 * A Function that maps everything to the same value. 
 * 
 * @author Bryan Tripp
 */
public class ConstantFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	public static final String DIMENSION_PROPERTY = AbstractFunction.DIMENSION_PROPERTY;
	public static final String VALUE_PROPERTY = "value";
	
	private int myDimension;
	private float myValue;
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param dimension Input dimension of this Function
	 * @param value Constant output value of this Function 
	 */
	public ConstantFunction(int dimension, float value) {
		myDimension = dimension;
		myValue = value;
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, true);
		myConfiguration.defineSingleValuedProperty(VALUE_PROPERTY, Float.class, true);
	}

	/**
	 * Instantiates with defaults one dimension and value 1. 
	 */
	public ConstantFunction() {
		this(1, 1);
	}

	/**
	 * @see ca.neo.model.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @param value The new constant result of the function
	 */
	public void setValue(float value) {
		myValue = value;
	}
	
	/**
	 * @param dimension New dimension
	 */
	public void setDimension(int dimension) {
		myDimension = dimension;
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

	/**
	 * @return Value of function
	 */
	public float getValue() {
		return myValue;
	}

}
