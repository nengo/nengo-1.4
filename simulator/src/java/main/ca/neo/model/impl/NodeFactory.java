/*
 * Created on 21-Jun-2006
 */
package ca.neo.model.impl;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;

/**
 * Produces Nodes. This interface does not define rules as to how the Nodes are parameterized, 
 * but a given implementation might use parameters that are constant across nodes, drawn 
 * from a PDF, selected from a database, etc.
 * 
 * TODO: should make many at once? - to allow dependencies between parameters of results in a group 
 * 
 * @author Bryan Tripp
 */
public interface NodeFactory {

	/**
	 * @param name The name of the Node (unique within containing Ensemble or Network)
	 * @return A new Node 
	 * @throws StructuralException for any problem that prevents construction  
	 */
	public Node make(String name) throws StructuralException;
	
}
