/*
 * Created on May 19, 2006
 */
package ca.neo.util;

import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;

/**
 * Reads state variables from Probeable objects (eg membrane potential from a Neuron).
 * Different types of Probe will do different things with collected data. For example 
 * a Recorder will collect data until the end of a simulation, and a Scope will display
 * data during a simulation. 
 * 
 * @author Bryan Tripp
 */
public interface Probe {

	/**
	 * @param target The object about which state history is to be collected 
	 * @param stateName The name of the state variable to collect 
	 * @throws SimulationException if the given target does not have the given state
	 */
	public void connect(Probeable target, String stateName) throws SimulationException;
	
	/**
	 * Clears collected data. 
	 */
	public void reset();
	
	/**
	 * Processes new data. To be called after every Network time step. 
	 */
	public void collect(float time);	
	
	/**
	 * @param rate Rate in samples per second. The default is one sample per network time step, and it is 
	 * 		not possible to sample faster than this (specifying a higher sampling rate has no effect).   
	 */
	public void setSamplingRate(float rate);

}
