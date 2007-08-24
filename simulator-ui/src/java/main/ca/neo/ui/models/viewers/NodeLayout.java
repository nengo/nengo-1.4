package ca.neo.ui.models.viewers;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import ca.neo.ui.models.PNeoNode;
import edu.umd.cs.piccolo.util.PBounds;

public class NodeLayout implements Serializable {

	private static final long serialVersionUID = 1L;

	String layoutName;
	Hashtable<String, Point2D> nodePositions;

	// /**
	// * @param layoutName
	// * Name of the layout
	// */
	// public NodeLayout(String layoutName) {
	// super();
	//
	// }
	PBounds savedViewBounds;

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

		Enumeration<PNeoNode> en = nodeViewer.getNeoNodes().elements();

		while (en.hasMoreElements()) {
			PNeoNode node = en.nextElement();
			addPosition(node.getName(), node.getOffset());
		}
		savedViewBounds = nodeViewer.getSky().getViewBounds();

	}

	public Point2D addPosition(String nodeName, Point2D position) {
		return nodePositions.put(nodeName, position);
	}

	public String getName() {
		return layoutName;
	}

	public Point2D getPosition(String nodeName) {
		return nodePositions.get(nodeName);
	}

	public PBounds getSavedViewBounds() {
		return savedViewBounds;
	}

}
