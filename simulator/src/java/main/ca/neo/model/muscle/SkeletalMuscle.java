/*
 * Created on 30-Nov-2006
 */
package ca.neo.model.muscle;

import ca.neo.model.SimulationException;

/**
 * A model of a skeletal muscle or group or equivalent torque muscle. 
 * See also subinterfaces TensionMuscle and TorqueMuscle.
 * 
 * @author Bryan Tripp
 */
public interface SkeletalMuscle {
	
	/**
	 * Runs the muscle model, updating internal state and outputs as needed.  
	 * Runs should be short (eg 1ms), because inputs can not be changed during a 
	 * run, and outputs will only be collected after a run.  
	 * 
	 * @param startTime simulation time at which running starts (s)
	 * @param endTime simulation time at which running ends (s)
	 * @throws SimulationException if a problem is encountered while trying to run 
	 */
	public void run(float startTime, float endTime) throws SimulationException; 

	/**
	 * @param excitation A number between 0 and 1, representing the degree of excitation
	 * 		(1 being maximal)
	 */
	public void setExcitation(float excitation);
	
}
