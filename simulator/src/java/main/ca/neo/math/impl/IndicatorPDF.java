/*
 * Created on 13-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.PDF;

/**
 * Uniform probability between upper and lower limits, zero elsewhere.
 * 
 * @author Bryan Tripp
 */
public class IndicatorPDF implements PDF {

	private static final long serialVersionUID = 1L;
	
	private float myLow;
	private float myHigh;
	private float myDifference;
	private float myVal;

	/**
	 * @param low Lower limit of range of possible values
	 * @param high Upper limit of range of possible values
	 */
	public IndicatorPDF(float low, float high) {
		set(low, high);
	}
	
	/**
	 * Instantiates with default range [0,1]. 
	 */
	public IndicatorPDF() {
		this(0, 1);
	}
	
	private void set(float low, float high) {
		if (high <= low) {
			throw new IllegalArgumentException("High value must be greater than low value");
		}
		
		myLow = low;
		myHigh = high;
		myDifference = high - low;
		myVal = 1f / myDifference;   		
	}

	/**
	 * @param low Lower limit of range of possible values
	 */
	public void setLow(float low) {
		set(low, myHigh);
	}

	/**
	 * @param high Upper limit of range of possible values
	 */
	public void setHigh(float high) {
		set(myLow, high);
	}
	
	/**
	 * @return Lower limit of range of possible values
	 */
	public float getLow() {
		return myLow;
	}

	/**
	 * @return Upper limit of range of possible values
	 */
	public float getHigh() {
		return myHigh;
	}
	
	/**
	 * @return Probability density between low and high limits
	 */
	public float getDensity() {
		return myVal;
	}

	/**
	 * @see ca.neo.math.PDF#sample()
	 */
	public float[] sample() {
		return new float[] {myLow + myDifference * (float) Math.random()};
	}

	/**
	 * @return 1 
	 * @see ca.neo.math.Function#getDimension()
	 */
	public int getDimension() {
		return 1;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		return doMap(myLow, myHigh, myVal, from[0]);
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = doMap(myLow, myHigh, myVal, from[i][0]);
		}
		
		return result;
	}
	
	private static float doMap(float low, float high, float val, float from) {
		return (from >= low && from <= high) ? val : 0f;
	}

}
