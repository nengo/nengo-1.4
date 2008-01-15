package ca.shu.ui.lib.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Hashtable;

import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldObject;

/**
 * Layout of nodes which is serializable
 * 
 * @author Shu Wu
 */
public class WorldLayout implements Serializable {

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
	private Hashtable<Integer, PointSerializable> nodePositions;

	/**
	 * Saved view bounds
	 */
	private Rectangle2D savedViewBounds;

	/**
	 * @param layoutName
	 *            Name of the layout
	 * @param world
	 *            Viewer containing nodes
	 */
	public WorldLayout(String layoutName, IWorld world, boolean elasticMode) {
		super();
		this.layoutName = layoutName;
		this.elasticMode = elasticMode;

		nodePositions = new Hashtable<Integer, PointSerializable>();

		for (IWorldObject object : world.getGround().getChildren()) {
			addPosition(object, object.getOffset());
		}

		savedViewBounds = world.getSky().getViewBounds();

	}

	/**
	 * @param nodeName
	 *            Name of node
	 * @param position
	 *            Position of node
	 */
	private void addPosition(IWorldObject wo, Point2D position) {
		nodePositions.put(wo.hashCode(), new PointSerializable(position));
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
	public Point2D getPosition(IWorldObject node) {
		PointSerializable savedPosition = nodePositions.get(node.hashCode());
		if (savedPosition != null) {
			return nodePositions.get(node.hashCode()).toPoint2D();
		} else {
			return null;
		}
	}

	/**
	 * @return Saved view bounds
	 */
	public Rectangle2D getSavedViewBounds() {
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
