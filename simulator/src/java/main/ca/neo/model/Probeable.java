/*
 * Created on May 3, 2006
 */
package ca.neo.model;

import java.util.Properties;

import ca.neo.util.TimeSeries;

/**
 * <p>An object that can be probed for a history of its state OVER THE MOST RECENT 
 * NETWORK TIME STEP. A Probeable must declare a list of state variables via 
 * the method listStates(), and is responsible for storing store a history of these 
 * state variables covering the most recent network time step (data from past 
 * time steps can be discarded).   
 * 
 * @author Bryan Tripp
 */
public interface Probeable {
	
	/**
	 * Note that the units of TimeSeries' for a given state do not change over time (ie at different 
	 * time steps). 
	 * 
	 * CAUTION: The TimeSeries should not contain a reference to any arrays that you are going to change 
	 * later. The caller owns what you return.  
	 *  
	 * @param stateName A state variable name
	 * @return History of values for the named state variable. The history must cover the most recent
	 * 		network time step, and no more. There should be no overlap in the time points returned 
	 * 		for different steps. 
	 * @throws SimulationException if the Probeable does not have the requested state 
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException;
	
	/**
	 * @return List of state variable names, eg "V", and associated descriptions
	 * 		eg "membrane potential (mV)" 
	 */
	public Properties listStates();

}
