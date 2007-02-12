/*
 * Created on May 19, 2006
 */
package ca.neo.model;

/**
 * An object that can be reset to some initial state. 
 * 
 * @author Bryan Tripp
 */
public interface Resettable {

	/**
	 * @param randomize True indicates that the object should be reset to a 
	 * 		randomly selected initial state (the object must be aware of the 
	 * 		distribution from which to draw from). False indicates that the 
	 * 		object should be reset to a fixed initial state (which it must 
	 * 		also know). Some objects may not support randomization of the initial 
	 * 		state, in which case a fixed state will be used in either case. 
	 */
	public void reset(boolean randomize);
	
}
