package ca.neo.ui.models.viewers;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import ca.neo.ui.models.PNeoNode;

public class NodeLayout implements Serializable {

	private static final long serialVersionUID = 1L;

	Hashtable<String, Point2D> nodePositions;
	String layoutName;

	/**
	 * @param layoutName
	 *            Name of the layout
	 */
	public NodeLayout(String layoutName) {
		super();
		this.layoutName = layoutName;
		nodePositions = new Hashtable<String, Point2D>();
	}

	/**
	 * @param layoutName
	 *            Name of the layout
	 * @param nodes
	 *            Hastable of nodes
	 */
	public NodeLayout(String layoutName, Hashtable<String, PNeoNode> nodes) {
		this(layoutName);

		Enumeration<PNeoNode> en = nodes.elements();

		while (en.hasMoreElements()) {
			PNeoNode node = en.nextElement();
			addPosition(node.getName(), node.getOffset());
		}

	}

	public Point2D getPosition(String nodeName) {
		return nodePositions.get(nodeName);
	}

	public Point2D addPosition(String nodeName, Point2D position) {
		return nodePositions.put(nodeName, position);
	}

	public String getName() {
		return layoutName;
	}

}
