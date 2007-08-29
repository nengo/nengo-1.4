package ca.neo.ui.models.viewers;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import ca.neo.ui.models.UINeoNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Layout of nodes which is serializable
 * 
 * @author Shu Wu
 */
public class NodeLayout implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Name of the layout
	 */
	private String layoutName;

	/**
	 * Node positions referenced by name
	 */
	private Hashtable<String, Point2D> nodePositions;

	/**
	 * Saved view bounds
	 */
	private PBounds savedViewBounds;

	/**
	 * @param layoutName
	 *            Name of the layout
	 * @param nodeViewer
	 *            Viewer containing nodes
	 */
	public NodeLayout(String layoutName, NodeViewer nodeViewer) {
		super();
		this.layoutName = layoutName;
		nodePositions = new Hashtable<String, Point2D>();

		Enumeration<UINeoNode> en = nodeViewer.getNeoNodes().elements();

		while (en.hasMoreElements()) {
			UINeoNode node = en.nextElement();
			addPosition(node.getName(), node.getOffset());
		}
		savedViewBounds = nodeViewer.getSky().getViewBounds();

	}

	/**
	 * @param nodeName
	 *            Name of node
	 * @param position
	 *            Position of node
	 * @return Position of node
	 */
	public Point2D addPosition(String nodeName, Point2D position) {
		return nodePositions.put(nodeName, position);
	}

	/**
	 * @return Layout name
	 */
	public String getName() {
		return layoutName;
	}

	/**
	 * @param nodeName
	 *            Name of node
	 * @return Position of node
	 */
	public Point2D getPosition(String nodeName) {
		return nodePositions.get(nodeName);
	}

	/**
	 * @return Saved view bounds
	 */
	public PBounds getSavedViewBounds() {
		return savedViewBounds;
	}

}
