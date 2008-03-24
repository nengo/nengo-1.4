/*
 * Created on 7-Jun-2006
 */
package ca.nengo.model;

import java.io.Serializable;

import ca.nengo.util.VisiblyMutable;

/**
 * A part of a Network that can be run independently (eg a Neuron). Normally 
 * a source of Origins and/or Terminations.
 * 
 * @author Bryan Tripp
 */
public interface Node extends Serializable, Resettable, SimulationMode.ModeConfigurable, VisiblyMutable, Cloneable {

	/**
	 * @return Name of Node (must be unique in a Network) 
	 */
	public String getName();
	
	/**
	 * @param name The new name
	 */
	public void setName(String name) throws StructuralException;
	
	/**
	 * Runs the Node (including all its components), updating internal state and outputs as needed.  
	 * Runs should be short (eg 1ms), because inputs can not be changed during a run, and outputs
	 * will only be communicated to other Nodes after a run.   
	 * 
	 * @param startTime simulation time at which running starts (s)
	 * @param endTime simulation time at which running ends (s)
	 * @throws SimulationException if a problem is encountered while trying to run 
	 */
	public void run(float startTime, float endTime) throws SimulationException; 
	
	/**
	 * @return Sets of ouput channels (eg spiking outputs, gap junctional outputs, etc.)
	 */
	public Origin[] getOrigins();
	
	/**
	 * @param name Name of an Origin on this Node
	 * @return The named Origin if it exists
	 * @throws StructuralException if the named Origin does not exist
	 */
	public Origin getOrigin(String name) throws StructuralException;
	
	/**
	 * @return Sets of input channels (these have the same dimension as corresponding Origins 
	 * 		to which they are connected).  
	 */
	public Termination[] getTerminations();
	
	/**
	 * @param name Name of a Termination onto this Node
	 * @return The named Termination if it exists 
	 * @throws StructuralException if the named Termination does not exist
	 */
	public Termination getTermination(String name) throws StructuralException;
	
	/**
	 * @return User-specified documentation for the Node, if any
	 */
	public String getDocumentation();
	
	/**
	 * @param text New user-specified documentation for the Node
	 */
	public void setDocumentation(String text);
	
	/**
	 * @return An independent copy of the Node
	 * @throws CloneNotSupportedException
	 */
	public Node clone() throws CloneNotSupportedException;
}
