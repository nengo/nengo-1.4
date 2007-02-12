/*
 * Created on 23-May-2006
 */
package ca.neo.model.impl;

import java.util.HashMap;
import java.util.Map;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.Projection;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;

/**
 * Default implementation of Network. 
 *  
 * @author Bryan Tripp
 */
public class NetworkImpl implements Network {

	private static final long serialVersionUID = 1L;
	
	private Map myNodeMap; //keyed on name
	private Map myProjectionMap; //keyed on Termination

	public NetworkImpl() {
		myNodeMap = new HashMap(20);
		myProjectionMap	= new HashMap(50);	 
	}	

	/**
	 * @see ca.neo.model.Network#addNode(ca.neo.model.Node)
	 */
	public void addNode(Node node) throws StructuralException {
		if (myNodeMap.containsKey(node.getName())) {
			throw new StructuralException("This Network already contains a Node named " + node.getName());
		}
		
		myNodeMap.put(node.getName(), node);
	}

	/**
	 * @see ca.neo.model.Network#getNodes()
	 */
	public Node[] getNodes() {
		return (Node[]) myNodeMap.values().toArray(new Node[0]);
	}

	/**
	 * @see ca.neo.model.Network#getNode(java.lang.String)
	 */
	public Node getNode(String name) throws StructuralException {
		if (!myNodeMap.containsKey(name)) {
			throw new StructuralException("No Node named " + name + " in this Network");			
		}
		return (Node) myNodeMap.get(name);
	}

	/**
	 * @see ca.neo.model.Network#removeNode(java.lang.String)
	 */
	public void removeNode(String name) throws StructuralException {
		if (myNodeMap.containsKey(name)) {
			myNodeMap.remove(name);
		} else {
			throw new StructuralException("No Node named " + name + " in this Network");
		}
	}

	/**
	 * @see ca.neo.model.Network#addProjection(ca.neo.model.Origin, ca.neo.model.Termination)
	 */
	public void addProjection(Origin origin, Termination termination) throws StructuralException {
		if (myProjectionMap.containsKey(termination)) {
			throw new StructuralException("There is already an Origin connected to the specified Termination");
		}
		
		if (origin.getDimensions() != termination.getDimensions()) {
			throw new StructuralException("Can't connect Origin of dimension " + origin.getDimensions() 
					+ " to Termination of dimension " + termination.getDimensions());
		}
		
		myProjectionMap.put(termination, new ProjectionImpl(origin, termination));
	}

	/**
	 * @see ca.neo.model.Network#getProjections()
	 */
	public Projection[] getProjections() {
		return (Projection[]) myProjectionMap.values().toArray(new Projection[0]);
	}

	/**
	 * @see ca.neo.model.Network#removeProjection(ca.neo.model.Termination)
	 */
	public void removeProjection(Termination termination) throws StructuralException {
		if (myProjectionMap.containsKey(termination)) {
			myProjectionMap.remove(termination);
		} else {
			throw new StructuralException("The Network contains no Projection ending on the specified Termination");
		}
	}

}
