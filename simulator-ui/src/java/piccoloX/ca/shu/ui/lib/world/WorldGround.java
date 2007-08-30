package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;

/**
 * Layer within a world which is zoomable and pannable. It contains world
 * objects.
 * 
 * @author Shu Wu
 */
public class WorldGround extends WorldObject implements IWorldLayer {

	private static final long serialVersionUID = 1L;

	/**
	 * World this layer belongs to
	 */
	private World world;

	/**
	 * Create a new ground layer
	 * 
	 * @param world
	 *            World this layer belongs to
	 */
	public WorldGround(World world) {
		super();
		this.world = world;

		this.setSelectable(false);

	}

	/**
	 * Adds a child object. Like addChild, but with more pizzaz.
	 * 
	 * @param wo
	 *            Object to add to the layer
	 */
	public void addObject(WorldObject wo) {
		addObject(wo, true);

	}

	/**
	 * Adds a little pizzaz when adding new objects
	 * 
	 * @param wo
	 *            Object to be added
	 * @param centerCameraPosition
	 *            whether the object's position should be changed to appear at
	 *            the center of the camera
	 */
	public void addObject(WorldObject wo, boolean centerCameraPosition) {
		addChild(wo);

		Point2D finalPosition;
		if (centerCameraPosition) {
			finalPosition = world.skyToGround(new Point2D.Double(world
					.getWidth() / 2, world.getHeight() / 2));
		} else {
			finalPosition = wo.getOffset();
		}
		wo.setScale(1 / getGroundScale());

		wo.setOffset(finalPosition.getX(), finalPosition.getY()
				- (100 / getGroundScale()));

		wo.animateToPositionScaleRotation(finalPosition.getX(), finalPosition
				.getY(), 1, 0, 500);
	}

	/**
	 * @return The scale of the ground in relation to the sky
	 */
	public double getGroundScale() {
		return world.getSky().getViewScale();
	}

	@Override
	public World getWorld() {
		return world;
	}
}
