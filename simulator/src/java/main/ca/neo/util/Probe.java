/*
 * Created on May 19, 2006
 */
package ca.neo.util;

import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;

/**
 * Reads state variables from Probeable objects (eg membrane potential from a Neuron).
 * Collected data can be displayed during a simluation or kept for plotting afterwards.   
 * 
 * @author Bryan Tripp
 */
public interface Probe {

	/**
	 * @param target The object about which state history is to be collected 
	 * @param stateName The name of the state variable to collect 
	 * @param record If true, getData() returns history since last connect() or reset(),
	 * 		otherwise getData() returns most recent sample 
	 * @throws SimulationException if the given target does not have the given state
	 */
	public void connect(Probeable target, String stateName, boolean record) throws SimulationException;
	
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

	/**
	 * @return All collected data since last reset()
	 */
	public TimeSeries getData();
	
}
