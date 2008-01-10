package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.shu.ui.lib.objects.PEdge;

import edu.umd.cs.piccolo.PNode;

/**
 * A Layer of the world
 * 
 * @author Shu Wu
 */
public interface IWorldLayer {
	/**
	 * @param child
	 *            Child node to add
	 */
	public void addChild(PNode child);

	/**
	 * @return World which this layer belongs to
	 */
	public World getWorld();

	/**
	 * @param world
	 *            The world
	 */
	public void setWorld(World world);

	/**
	 * Converts a global coordinate to a local coordinate. This method modifies
	 * the parameter.
	 * 
	 * @param globalPoint
	 *            Global coordinate
	 * @return Local coordinate
	 */
	public Point2D globalToLocal(Point2D globalPoint);

	public void addEdge(PEdge edge);

	/**
	 * Converts a local bound to a global bound. This method modifies the
	 * parameter.
	 * 
	 * @param globalPoint
	 *            Global bound
	 * @return local bound
	 */
	public Rectangle2D globalToLocal(Rectangle2D globalPoint);
}
