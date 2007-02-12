/*
 * Created on 8-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;

/**
 * Function wrapper for sin(x), where x is in radians.
 * 
 * TODO: could have abstract impl with doMap() but then can't be static/inlined
 * TODO: test
 *  
 * @author Bryan Tripp
 */
public class SineFunction implements Function {

	private static final long serialVersionUID = 1L;	

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
		return doMap(from);
	}

	/**
	 * @see ca.neo.math.Function#multiMap(float[][])
	 */
	public float[] multiMap(float[][] from) {
		float[] result = new float[from.length];
		
		for (int i = 0; i < from.length; i++) {
			result[i] = doMap(from[i]);
		}
		
		return result;
	}
	
	private static float doMap(float[] from) {
		return (float) Math.sin(from[0]);
	}

}
