/*
 * Created on 21-Jun-2006
 */
package ca.nengo.model.impl;

import java.io.Serializable;

import ca.nengo.model.Node;
import ca.nengo.model.StructuralException;

/**
 * Produces Nodes. This interface does not define rules as to how the Nodes are parameterized, 
 * but a given implementation might use parameters that are constant across nodes, drawn 
 * from a PDF, selected from a database, etc.
 * 
 * @author Bryan Tripp
 */
public interface NodeFactory extends Serializable {

	/**
	 * @param name The name of the Node (unique within containing Ensemble or Network)
	 * @return A new Node 
	 * @throws StructuralException for any problem that prevents construction  
	 */
	public Node make(String name) throws StructuralException;

	/**
	 * @return A short description of the type of Node created by this factory
	 */
	public String getTypeDescription();
}
