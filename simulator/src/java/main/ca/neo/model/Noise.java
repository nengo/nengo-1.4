/*
 * Created on 24-May-07
 */
package ca.neo.model;

/**
 * An model of noise that can be explicitly injected into a circuit (e.g. added to 
 * an Origin). 
 *   
 * @author Bryan Tripp
 */
public interface Noise {
	
	/**
	 * @param elapsedTime Elapsed simulation time corresponding to this call
	 * @param input Values which are to be corrupted by noise
	 * @return The noisy values (inputs corrupted by noise) 
	 */
	public float[] getValues(float elapsedTime, float[] input);
	
	/**
	 * @return Dimension of input that can be corrupted by noise
	 */
	public int getDimension();
	
	
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
