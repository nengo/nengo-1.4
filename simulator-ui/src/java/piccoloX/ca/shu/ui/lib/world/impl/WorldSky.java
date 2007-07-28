package ca.shu.ui.lib.world.impl;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldLayer;
import ca.shu.ui.lib.world.IWorldObject;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;

public class WorldSky extends PCamera implements IWorldLayer {

	@Override
	public void translateView(double arg0, double arg1) {
		// TODO Auto-generated method stub
		super.translateView(arg0, arg1);
		
		
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7467076877836999849L;

	IWorld world;

	public void addWorldObject(IWorldObject node) {
		addChild((PNode) node);
	}

	public IWorld getWorld() {
		return world;
	}

	public WorldSky(IWorld world) {
		super();
		this.world = world;

	}

	public Collection<PNode> getChildrenAtBounds(Rectangle2D bounds) {
		System.out.println("Unimplemented!!");
		// TODO Auto-generated method stub
		return null;
	}

	public void addToWorldLayer(IWorldObject wo) {
		System.out.println("not implemented");
		addChild((PNode) wo);
	}

	public void addChildW(IWorldObject child) {
		System.out.println("not implemented");

	}

	public void endDrag() {
		System.out.println("not implemented");

	}

	public IWorldLayer getWorldLayer() {
		System.out.println("not implemented");
		return null;
	}

	public boolean isDraggable() {
		System.out.println("not implemented");
		return false;
	}

	public void justDropped() {
		System.out.println("not implemented");

	}

	public void justDroppedInWorld() {
		System.out.println("not implemented");

	}

	public void popState(State state) {
		System.out.println("not implemented");

	}

	public void pushState(State state) {
		System.out.println("not implemented");

	}

	public void removedFromWorld() {
		System.out.println("not implemented");

	}

	public void startDrag() {
		System.out.println("not implemented");

	}

	public String getName() {
		System.out.println("not implemented");
		return null;
	}

	public void setDraggable(boolean isDraggable) {
		System.out.println("not implemented");

	}

}
