package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;

public class WorldGround extends WorldObject implements IWorldLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	World world;

	public WorldGround(World world) {
		super();
		this.world = world;

		this.setDraggable(false);
		this.setFrameVisible(false);
		// this.addInputEventListener(new NodeDragHandler());

	}

	public void addWorldObject(WorldObject node) {

		addChild(node);

	}

	public void catchObject(WorldObject wo) {
		catchObject(wo, true);

	}

	/**
	 * Adds a little pizzaz while accepting new objects
	 * 
	 * @param wo
	 *            Object to be added
	 * @param centerCameraPosition
	 *            whether the object's position should be changed to appear at
	 *            the center of the camera
	 */
	public void catchObject(WorldObject wo, boolean centerCameraPosition) {
		addChild(wo);

		Point2D finalPosition;
		if (centerCameraPosition) {
			finalPosition = world.skyToGround(new Point2D.Double(world
					.getScreenWidth() / 2, world.getScreenHeight() / 2));
		} else {
			finalPosition = wo.getOffset();
		}
		wo.setScale(1 / getGroundScale());

		wo.setOffset(finalPosition.getX(), finalPosition.getY()
				- (100 / getGroundScale()));

		wo.animateToPositionScaleRotation(finalPosition.getX(), finalPosition
				.getY(), 1, 0, 500);
	}

	public double getGroundScale() {
		return world.getSky().getViewScale();
	}

	@Override
	public World getWorld() {
		return world;
	}
}
