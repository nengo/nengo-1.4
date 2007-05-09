/*
 * Created on 8-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;

/**
 * Function wrapper for sin(omega x), where x is in radians and omega is an angular frequency 
 * specified in the constructor.   
 * 
 * TODO: test
 *  
 * @author Bryan Tripp
 */
public class SineFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private final float myOmega;
	
	/**
	 * @param omega Angular frequency
	 */
	public SineFunction(float omega) {
		myOmega = omega;
	}
	
	/**
	 * @return Angular frequency
	 */
	public float getOmega() {
		return myOmega;
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
		return doMap(from, myOmega);
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = doMap(from[i], myOmega);
		}
		
		return result;
	}
	
	private static float doMap(float[] from, float omega) {
		return (float) Math.sin(from[0] * omega);
	}

}
