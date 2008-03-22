package ca.neo.ui.models;

import java.awt.geom.Point2D;

import ca.neo.model.Node;
import ca.shu.ui.lib.exceptions.UIException;

/**
 * A Container of PNeoNode
 * 
 * @author Shu Wu
 */
public interface NodeContainer {

	/**
	 * Adds a child node to the container
	 * 
	 * @param node
	 *            Node to be added
	 * @return UI Node Wrapper
	 */
	public UINeoNode addNodeModel(Node node) throws ContainerException;

	/**
	 * @param node
	 *            Node to be added
	 * @param posX
	 *            X Position of node
	 * @param posY
	 *            Y Position of node
	 * @return
	 */
	public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException;

	public Point2D localToView(Point2D localPoint);

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
