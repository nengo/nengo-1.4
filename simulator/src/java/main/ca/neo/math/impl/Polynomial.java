/*
 * Created on 1-Dec-2006
 */
package ca.neo.math.impl;

import ca.neo.config.ConfigUtil;
import ca.neo.math.Function;
import ca.neo.model.Configuration;

/**
 * A one-dimensional polynomial Function. It is defined by a series of coefficients that 
 * must be given in the constructor.    
 * 
 * @author Bryan Tripp
 */
public class Polynomial extends AbstractFunction implements Function {

	private static final long serialVersionUID = 1L;
	
	private float[] myCoefficients;
	private Configuration myConfiguration;
	
	/**
	 * @param coefficients Coefficients [a0 a1 a2 ...] in polynomial y = a0 + a1x + a2x^2 + ...
	 */
	public Polynomial(float[] coefficients) {
		super(1);
		myCoefficients = coefficients;
		myConfiguration = ConfigUtil.defaultConfiguration(this);
	}
	
	public Polynomial() {
		this(new float[]{1});
	}
	
	@Override
	public Configuration getConfiguration() {
		return myConfiguration;
	}

	/**
	 * @return Polynomial order
	 */
	public int getOrder() {
		return myCoefficients.length;
	}

	/**
	 * @param order Polynomial order 
	 */
	public void setOrder(int order) {
		float[] newCoeff = new float[order];
		System.arraycopy(myCoefficients, 0, newCoeff, 0, Math.min(order, myCoefficients.length));
		myCoefficients = newCoeff;
	}

	/**
	 * @return Coefficients [a0 a1 a2 ...] in polynomial y = a0 + a1x + a2x^2 + ...
	 */
	public float[] getCoefficients() {
		return myCoefficients;
	}
	
	/**
	 * @param coefficients Coefficients [a0 a1 a2 ...] in polynomial y = a0 + a1x + a2x^2 + ...
	 */
	public void setCoefficients(float[] coefficients) {
		myCoefficients = new float[coefficients.length];
		System.arraycopy(coefficients, 0, myCoefficients, 0, coefficients.length);
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
