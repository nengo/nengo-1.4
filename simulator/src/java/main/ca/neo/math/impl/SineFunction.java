/*
 * Created on 8-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;

/**
 * Function wrapper for sin(omega x), where x is in radians and omega is the angular frequency.  
 * 
 * TODO: test
 *  
 * @author Bryan Tripp
 */
public class SineFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private float myOmega;
	private float myAmplitude;

	/**
	 * Uses default angular frequency of 2pi and amplitude of 1
	 */
	public SineFunction() {
		this(2 * (float) Math.PI);
	}
	
	/**
	 * Uses default amplitude of 1. 
	 * 
	 * @param omega Angular frequency
	 */
	public SineFunction(float omega) {
		this(omega, 1);
	}
	
	/**
	 * @param omega Angular frequency
	 * @param amplitude Amplitude (peak value)
	 */
	public SineFunction(float omega, float amplitude) {
		myOmega = omega;
		myAmplitude = amplitude;
	}

	/**
	 * @return Angular frequency
	 */
	public float getOmega() {
		return myOmega;
	}
	
	/**
	 * @param omega Angular frequency
	 */
	public void setOmega(float omega) {
		myOmega = omega;
	}
	
	/**
	 * @return Amplitude (peak value)
	 */
	public float getAmplitude() {
		return myAmplitude;
	}
	
	/**
	 * @param amplitude Amplitude (peak value)
	 */
	public void setAmplitude(float amplitude) {
		myAmplitude = amplitude;
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
		return doMap(from, myOmega, myAmplitude);
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = doMap(from[i], myOmega, myAmplitude);
		}
		
		return result;
	}
	
	private static float doMap(float[] from, float omega, float amplitude) {
		return amplitude * (float) Math.sin(from[0] * omega);
	}

	@Override
	public Function clone() throws CloneNotSupportedException {
		return (Function) super.clone();
	}
}
