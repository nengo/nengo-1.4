package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;


import edu.umd.cs.piccolo.PNode;

public interface WorldLayer extends WorldObject {
	public World getWorld();
	public Rectangle2D globalToLocal(Rectangle2D globalPoint);
	public Point2D globalToLocal(Point2D globalPoint);

	
	
	
	
}
