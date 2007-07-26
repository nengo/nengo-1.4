package ca.shu.ui.lib.world.impl;

import java.awt.geom.Point2D;

import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldLayer;
import ca.shu.ui.lib.world.IWorldObject;
import edu.umd.cs.piccolo.util.PBounds;

public class WorldGround extends WorldObject implements IWorldLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	IWorld world;

	public WorldGround(IWorld world) {
		super();
		this.world = world;

		this.setDraggable(false);
		this.setFrameVisible(false);
		// this.addInputEventListener(new NodeDragHandler());

	}

	public IWorld getWorld() {
		return world;
	}

	public void addWorldObject(IWorldObject node) {

		PBounds bounds = this.getFullBounds();

		node.setOffset(bounds.getWidth() + 50, 0);

		addChildWorldObject(node);

	}

	public void addToWorldLayer(IWorldObject wo) {
		addChildWorldObject(wo);
		
	}

	public double getGroundScale() {
		return world.getSky().getViewScale();
	}
	
	
	/*
	 * Adds a little pizzaz while accepting new objects
	 */
	public void catchObject(IWorldObject wo) {
		addChildWorldObject(wo);

		Point2D finalPosition = world.skyToGround(new Point2D.Double(world
				.getScreenWidth() / 2, world.getScreenHeight() / 2));
		
		wo.setScale(1 / getGroundScale());
		
		wo.setOffset(finalPosition.getX(), finalPosition.getY() - (100 / getGroundScale()));
		
		wo.animateToPositionScaleRotation(finalPosition.getX(), finalPosition.getY(),1,0, 500);
	}
}
