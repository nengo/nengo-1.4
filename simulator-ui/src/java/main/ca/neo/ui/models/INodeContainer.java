package ca.neo.ui.models;

import ca.neo.model.Node;

/**
 * A Container of PNeoNode
 * 
 * @author Shu Wu
 */
public interface INodeContainer {

	/**
	 * Adds a child node to the container
	 * 
	 * @param node
	 *            Node to be added
	 */
	public void addNodeModel(Node node);
	
	public Node getNodeModel(String name);

}
