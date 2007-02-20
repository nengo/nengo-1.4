/*
 * Created on May 18, 2006
 */
package ca.neo.model;


/**
 * InstantaneousOutput consisting of spikes. 
 * 
 * @author Bryan Tripp
 */
public interface SpikeOutput extends InstantaneousOutput {

	/**
	 * @return Instantaneous output in spiking channels (true means spike; false means 
	 * 		no spike). 
	 */
	public boolean[] getValues();
	
}
