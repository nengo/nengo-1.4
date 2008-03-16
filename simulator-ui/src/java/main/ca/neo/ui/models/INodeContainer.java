package ca.neo.ui.models;

import ca.neo.model.Node;
import ca.shu.ui.lib.exceptions.UIException;

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
	public void addNodeModel(Node node) throws ContainerException;

	public Node getNodeModel(String name);

	public static class ContainerException extends UIException {
		private static final long serialVersionUID = 1L;

		public ContainerException() {
			super();
		}

		public ContainerException(String arg0) {
			super(arg0);
		}

	}

}
