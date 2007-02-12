/*
 * Created on May 19, 2006
 */
package ca.neo.util;

/**
 * A Probe that records the history of a state variable during a simulation. 
 *  
 * @author Bryan Tripp
 */
public interface Recorder extends Probe {

	/**
	 * @return All collected data since last reset()
	 */
	public TimeSeries getData();
	
}
