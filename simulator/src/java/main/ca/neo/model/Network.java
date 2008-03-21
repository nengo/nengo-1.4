/*
 * Created on May 16, 2006
 */
package ca.neo.model;

import ca.neo.sim.Simulator;

/**
 * <p>A neural circuit, consisting of Nodes such as Ensembles and ExternalInputs. A Network is the 
 * usual object of a simulation. If you are new to this code, what you probably want to 
 * do is create some Neurons, group them into Ensembles, connect the Ensemles in a Network, 
 * and run the Network in a Simulator.</p>   
 *  
 * <p>Note: Multiple steps are needed to add a Projection between Ensembles. First, an Origin  
 * must be created on the presynaptic Ensemble, and a Termination with the same dimensionality 
 * must be created on the post-synaptic Ensemble. Then the Origin and Termination can be connected 
 * with the method addProjection(Origin, Termination). We don't do this in one step (ie 
 * automatically create the necessary Origin and Termination as needed) because there are various 
 * ways of doing so, and in fact some types of Origins and Terminations can only be created in the 
 * course of constructing the Ensemble. Creation of an Origin or Termination can also be a complex process. 
 * Rather than try to abstract these varied procedures into something that can be driven from the Network 
 * level, we just assume here that the necessary Origins and Terminations exist, and provide a method 
 * for connecting them.</p>  
 * 
 * @author Bryan Tripp
 */
public interface Network extends Node, Probeable {

	/**
	 * @param node Node to add to the Network 
	 * @throws StructuralException if the Network already contains a Node of the same name
	 */
	public void addNode(Node node) throws StructuralException;
	
	/**
	 * @return All the Nodes in the Network
	 */
	public Node[] getNodes();
	
	/**
	 * @param name Name of Node to remove
	 * @return Named node
	 * @throws StructuralException if named Node does not exist in network
	 */
	public Node getNode(String name) throws StructuralException;
	
	/**
	 * @param name Name of Node to remove
	 * @throws StructuralException if named Node does not exist in network
	 */
	public void removeNode(String name) throws StructuralException;
	
	/**
	 * Connects an Origin to a Termination. Origins and Terminations belong to 
	 * Ensembles (or ExternalInputs). Both the Origin and Termination must be set up 
	 * before calling this method. The way to do this will depend on the Ensemble. 
	 * 
	 * @param origin Origin (data source) of Projection.  
	 * @param termination Termination (data destination) of Projection. 
	 * @throws StructuralException if the given Origin and Termination have different dimensions, 
	 * 		or if there is already an Origin connected to the given Termination (note that an 
	 * 		Origin can project to multiple Terminations though). 
	 */
	public Projection addProjection(Origin origin, Termination termination) throws StructuralException;

	/**
	 * @return All Projections in this Network 
	 */
	public Projection[] getProjections();
	
	/**
	 * @param termination Termination of Projection to remove
	 * @throws StructuralException if there exists no Projection between the specified
	 * 		Origin and Termination 
	 */
	public void removeProjection(Termination termination) throws StructuralException;

	/**
	 * Declares the given Origin as available for connection outside the Network
	 * via getOrigins(). This Origin should not be connected within	this Network.
	 * 
	 * @param origin An Origin within this Network that is to connect to something 
	 * 		outside this Network  
	 * @param name Name of the Origin as it will appear outside this Network 
	 */
	public void exposeOrigin(Origin origin, String name);
	
	
	/**
	 * Returns the name of the exposed origin given the inner origin. Returns
	 * null if no such origin is exposed.
	 * 
	 * @param insideOrigin
	 *            Origin inside the network
	 */
	public String getExposedOriginName(Origin insideOrigin);	
	
	/**
	 * Undoes exposeOrigin(x, x, name). 
	 * 
	 * @param name Name of Origin to unexpose. 
	 */
	public void hideOrigin(String name);

	/**
	 * Declares the given Termination as available for connection from outside the Network
	 * via getTerminations(). This Termination should not be connected within this Network.
	 * 
	 * @param termination A Termination within this Network that is to connect to something 
	 * 		outside this Network  
	 * @param name Name of the Termination as it will appear outside this Network 
	 */
	public void exposeTermination(Termination termination, String name);

	/**
	 * Undoes exposeTermination(x, x, name). 
	 * 
	 * @param name Name of Termination to unexpose. 
	 */
	public void hideTermination(String name);

	/**
	 * Returns the name of the exposed termination given the inner termination. Returns
	 * null if no such termination is exposed.
	 * 
	 * @param insideTermination
	 *            Termination inside the network
	 */
	public String getExposedTerminationName(Termination insideTermination);
	
	/**
	 * Declares the given Probeable state as being available for Probing from outside this 
	 * Network. 
	 * 
	 * @param probeable A Probeable within this Network. 
	 * @param stateName A state of the given Probeable
	 * @param name A new name with which to access this state via Network.getHistory
	 * @throws StructuralException 
	 */
	public void exposeState(Probeable probeable, String stateName, String name) throws StructuralException;
	
	/**
	 * Undoes exposeState(x, x, name). 
	 * 
	 * @param name Name of state to unexpose. 
	 */
	public void hideState(String name);
	
	/**
	 * @param simulator The Simulator used to run this Network
	 */
	public void setSimulator(Simulator simulator);

	/**
	 * @return The Simulator used to run this Network
	 */
	public Simulator getSimulator();

	/**
	 * Metadata is non-critical information about the Network (eg UI layout) that the user doesn't  
	 * access directly. 
	 * 
	 * (Note: if there is a need for user-accessible metadata, Network could extend Configurable, but this doesn't 
	 * seem to be necessary.)
	 *  
	 * @param key Name of a metadata item
	 * @return Value of a metadata item 
	 */
	public Object getMetaData(String key);
	
	/**
	 * @param key Name of a metadata item
	 * @param value Value of the named metadata item
	 */
	public void setMetaData(String key, Object value);
	
}
