/*
 * Created on May 18, 2006
 */
package ca.neo.model;

/**
 * An Origin (source of information) outside the scope of the Network. An ExternalInput 
 * could model for example sensory input or activity from neurons in areas that are 
 * not modelled explicitly. 
 *   
 * @author Bryan Tripp
 */
public interface ExternalInput extends Node, Origin {

	/**
	 * Performs any processing necessary to produce new values for getValues(), 
	 * which correspond to the output of this Origin at endTime.   
	 * 
	 * @param startTime Simulation time at which running starts. 
	 * @param endTime Simulation time at which running ends. 
	 */
	public void run(float startTime, float endTime);
	
}
