/*
 * Created on 1-Jun-2006
 */
package ca.neo.dynamics;

import java.io.Serializable;

import ca.neo.model.Units;

/**
 * <p>A state-space model of a continuous-time dynamical system. The system can 
 * be linear or non-linear, and autonomous or time-varying.</p>
 * 
 * <p>While a DynamicalSystem can be time-varying, it must be immutable. That 
 * is, its properties can change over simulation time, but not over run time.</p>
 * 
 * TODO: units here or in subinterface?
 * TODO: reference Chen
 * 
 * @author Bryan Tripp
 */
public interface DynamicalSystem extends Serializable, Cloneable {

	/**
	 * The dynamic equation. 
	 * 
	 * @param t Time
	 * @param u Input vector
	 * @return 1st derivative of state vector
	 */
	public float[] f(float t, float[] u);
	
	/**
	 * The output equation. 
	 * 
	 * @param t Time 
	 * @param u Input vector
	 * @return Output vector
	 */
	public float[] g(float t, float[] u);
	
	/**
	 * @return State vector
	 */
	public float[] getState();
	
	/**
	 * @param state New state vector
	 */
	public void setState(float[] state);
	
	/**
	 * @return Dimension of input vector
	 */
	public int getInputDimension();
	
	/**
	 * @return Dimension of output vector
	 */
	public int getOutputDimension();
	
	/**
	 * @param outputDimension Numbered from 0
	 * @return Units of output in the given dimension
	 */
	public Units getOutputUnits(int outputDimension);
	
	/**
	 * @return An identical copy of this system which references an independent copy of the state variables
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException;
	
}
