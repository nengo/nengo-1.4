/*
 * Created on May 16, 2006
 */
package ca.neo.model;

import java.io.Serializable;

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
public interface Network extends Serializable {

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
	public void addProjection(Origin origin, Termination termination) throws StructuralException;

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
	
}
