/*
 * Created on 7-Jun-2006
 */
package ca.neo.model;

import java.io.Serializable;

/**
 * A part of a Network that can be run independently (eg a Neuron). Normally 
 * a source of Origins and/or Terminations.
 * 
 * @author Bryan Tripp
 */
public interface Node extends Serializable, Resettable {

	/**
	 * @return Name of Node (must be unique in a Network) 
	 */
	public String getName();
	
	/**
	 * Sets the Node to run in either the given mode or the closest mode that the Node supports 
	 * (all Nodes must support SimulationMode.DEFAULT, and must default to this mode).
	 * 
	 * @param mode Requested simulation mode 
	 */
	public void setMode(SimulationMode mode);
	
	/**
	 * @return The mode in which the Node is currently running. 
	 */
	public SimulationMode getMode();
	
	/**
	 * Runs the Node (including all its components), updating internal state and outputs as needed.  
	 * Runs should be short (eg 1ms), because inputs can not be changed during a run, and outputs
	 * will only be collected after a run.   
	 * 
	 * @param startTime simulation time at which running starts (s)
	 * @param endTime simulation time at which running ends (s)
	 * @throws SimulationException if a problem is encountered while trying to run 
	 */
	public void run(float startTime, float endTime) throws SimulationException; 
	
}
