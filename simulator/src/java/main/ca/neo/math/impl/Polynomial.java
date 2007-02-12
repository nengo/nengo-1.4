/*
 * Created on 1-Dec-2006
 */
package ca.neo.math.impl;

import ca.neo.math.Function;

/**
 * A one-dimensional polynomial Function. It is defined by a series of coefficients that 
 * must be given in the constructor.    
 * 
 * @author Bryan Tripp
 */
public class Polynomial extends AbstractFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private float[] myCoefficients;
	
	/**
	 * @param coefficients Coefficients [a0 a1 a2 ...] in polynomial y = a0 + a1x + a2x^2 + ...
	 */
	public Polynomial(float[] coefficients) {
		super(1);
		myCoefficients = coefficients;
	}

	/**
	 * @see ca.neo.math.Function#map(float[])
	 */
	public float map(float[] from) {
		float result = myCoefficients[0];
		
		float xpowi = from[0];
		for (int i = 1; i < myCoefficients.length; i++) {
			result += myCoefficients[i] * xpowi; 
			xpowi = xpowi*from[0];
		}
		
		return result;
	}

}
