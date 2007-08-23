package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;

public interface IWorldLayer {
	public void addChild(PNode child);

	public World getWorld();

	public Point2D globalToLocal(Point2D globalPoint);

	public Rectangle2D globalToLocal(Rectangle2D globalPoint);
}
