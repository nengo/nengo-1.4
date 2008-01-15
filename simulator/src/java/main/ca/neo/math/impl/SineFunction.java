/*
 * Created on 8-Jun-2006
 */
package ca.neo.math.impl;

import ca.neo.config.Configuration;
import ca.neo.config.impl.ConfigurationImpl;
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
	
	public static final String DIMENSION_PROPERTY = AbstractFunction.DIMENSION_PROPERTY;
	public static final String OMEGA_PROPERTY = "omega";
	public static final String AMPLITUDE_PROPERTY = "amplitude";
	
	private float myOmega;
	private float myAmplitude;
	private ConfigurationImpl myConfiguration;

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
		myConfiguration = new ConfigurationImpl(this);
		myConfiguration.defineSingleValuedProperty(DIMENSION_PROPERTY, Integer.class, false);
		myConfiguration.defineSingleValuedProperty(OMEGA_PROPERTY, Float.class, true);
		myConfiguration.defineSingleValuedProperty(AMPLITUDE_PROPERTY, Float.class, true);
	}

	/**
	 * @see ca.neo.config.Configurable#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return myConfiguration;
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

}
