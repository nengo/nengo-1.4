/*
 * Created on 14-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.config.Configuration;
import ca.neo.config.impl.ConfigurationImpl;
import ca.neo.math.Function;

/**
 * Identity function on a particular dimension of input, ie f(x) = x_i, 
 * where i is a constant.
 * 
 * @author Bryan Tripp
 */
public class IdentityFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	public static final String DIMENSION_PROPERTY = AbstractFunction.DIMENSION_PROPERTY;
	public static final String INDEX_PROPERTY = "identityDimension";
	
	private int myDimension;
	private int myIdentityDimension;
	private ConfigurationImpl myConfiguration;
	
	/**
	 * @param dimension Dimension of input vector  
	 * @param i Index (from 0) of input vector of which this function is 
	 * 		an identity 
	 */
	public IdentityFunction(int dimension, int i) {
		setDimension(dimension);
		setIdentityDimension(i);
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, true);
		myConfiguration.defineSingleValuedProperty(INDEX_PROPERTY, Integer.class, true);
	}
	
	/**
	 * Instantiates with default of one dimension. 
	 */
	public IdentityFunction() {
		this(1, 0);
	}

	/**
	 * @see ca.neo.config.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return myDimension;
	}
	
	/**
	 * @param dimension New dimension of expected input vectors
	 */
	public void setDimension(int dimension) {
		if (dimension <= 0) {
			throw new IllegalArgumentException("Dimension must be a +ve integer");
		}
		myDimension = dimension;
	}
	
	/**
	 * @return Index on input vector of which this funciton is an identity
	 */
	public int getIdentityDimension() {
		return myIdentityDimension;
	}
	
	/**
	 * @param i Index (from 0) of input vector of which this function is 
	 * 		an identity 
	 */
	public void setIdentityDimension(int i) {
		if (i >= myDimension || i < 0) {
			throw new IllegalArgumentException("Index " + i + " is out of range");
		}
		myIdentityDimension = i;
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
