package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;


import edu.umd.cs.piccolo.PNode;

public interface IWorldLayer extends IWorldObject {
	public IWorld getWorld();
	public Rectangle2D globalToLocal(Rectangle2D globalPoint);
	public Point2D globalToLocal(Point2D globalPoint);
	public Collection<PNode> getChildrenAtBounds(Rectangle2D bounds);
	
	public void addChild(PNode node);
	public void addWorldObject(IWorldObject node);
	public void addToWorldLayer(IWorldObject wo);
	
	
}
