package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface WorldLayer extends WorldObject {
	public World getWorld();
	public Rectangle2D globalToLocal(Rectangle2D globalPoint);
	public Point2D globalToLocal(Point2D globalPoint);

	
	
	
	
}
