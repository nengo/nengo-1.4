/*
 * Created on 24-May-07
 */
package ca.nengo.model;

import java.io.Serializable;

/**
 * <p>An model of noise that can be explicitly injected into a circuit (e.g. added to 
 * an Origin). </p>
 * 
 * <p>Noise may be cloned across independent dimensions of a Noisy. This means that 
 * either 1) noise parameters can't be changed after construction, or 2) parameters
 * must be shared or propagated across clones. </p>
 *   
 * @author Bryan Tripp
 */
public interface Noise extends Cloneable, Resettable, Serializable {
	
	public static final String DIMENSION_PROPERTY = "dimension";
	
	/**
	 * @param startTime Simulation time at which step starts 
	 * @param endTime Simulation time at which step ends
	 * @param input Value which is to be corrupted by noise
	 * @return The noisy values (inputs corrupted by noise) 
	 */
	public float getValue(float startTime, float endTime, float input);
	
	public Noise clone();
	
	/**
	 * An object that implements this interface is subject to Noise.  
	 * 
	 * @author Bryan Tripp
	 */
	public interface Noisy {

		/**
		 * @param noise New noise model
		 */
		public void setNoise(Noise noise);
		
		/**
		 * @return Noise with which the object is to be corrupted
		 */
		public Noise getNoise();
		
	}

}
