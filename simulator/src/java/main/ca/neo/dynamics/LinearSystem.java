/*
 * Created on 7-Jun-2006
 */
package ca.neo.dynamics;

/**
 * <p>A linear dynamical system, which may or may not be time-varying. We use 
 * the state-space model of linear systems, which consist of four (possibly 
 * time-varying) matrices.</p>
 * 
 * TODO: ref chen
 * 
 * <p>The distinction between linear and non-linear dynamical systems is 
 * important, because many assumptions that hold for linear systems do not hold 
 * in general. For this reason, only linear systems can be used in some situations, 
 * and we need this interface to enforce their use.</p>
 *  
 * @author Bryan Tripp
 */
public interface LinearSystem extends DynamicalSystem {

	/**
	 * @param t Simulation time
	 * @return The dynamics matrix at the given time 
	 */
	public float[][] getA(float t);
	
	/**
	 * @param t Simulation time
	 * @return The input matrix at the given time 
	 */
	public float[][] getB(float t);
	
	/**
	 * @param t Simulation time
	 * @return The output matrix at the given time 
	 */
	public float[][] getC(float t);

	/**
	 * @param t Simulation time
	 * @return The passthrough matrix at the given time 
	 */
	public float[][] getD(float t);
	
}
