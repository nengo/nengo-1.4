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
	 * Whether elastic layout is enabled
	 */
	private boolean elasticMode;

	/**
	 * Node positions referenced by name
	 */
	private Hashtable<String, PointSerializable> nodePositions;

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
	public NodeLayout(String layoutName, NodeViewer nodeViewer,
			boolean elasticMode) {
		super();
		this.layoutName = layoutName;
		this.elasticMode = elasticMode;

		nodePositions = new Hashtable<String, PointSerializable>();

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
	 */
	public void addPosition(String nodeName, Point2D position) {
		nodePositions.put(nodeName, new PointSerializable(position));
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
		return nodePositions.get(nodeName).toPoint2D();
	}

	/**
	 * @return Saved view bounds
	 */
	public PBounds getSavedViewBounds() {
		return savedViewBounds;
	}

	public boolean elasticModeEnabled() {
		return elasticMode;
	}

}

/**
 * Wraps point2D in a serializable wrapper
 * 
 * @author Shu Wu
 */
class PointSerializable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	double x, y;

	public PointSerializable(Point2D point) {
		x = point.getX();
		y = point.getY();
	}

	public Point2D toPoint2D() {
		return new Point2D.Double(x, y);
	}

}
