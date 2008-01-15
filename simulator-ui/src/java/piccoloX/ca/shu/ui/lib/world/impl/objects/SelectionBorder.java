package ca.shu.ui.lib.world.piccolo.objects;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.EventListener;
import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.IWorldSky;
import ca.shu.ui.lib.world.IWorldObject.EventType;
import ca.shu.ui.lib.world.piccolo.primitives.Path;

/**
 * A Border instance that can change its object of focus (As long as the object
 * are within the world that the frame is in). Border is attached to the sky
 * layer so there is no attenuation of the edge width when the ground is viewed
 * at a low scale.
 * 
 * @author Shu Wu
 */
public class SelectionBorder implements EventListener {
	private Path frame;

	private Color frameColor = Style.COLOR_BORDER_SELECTED;

	private IWorldSky frameHolder;

	private IWorldObject selectedObj;

	/**
	 * @param world
	 *            World, whose sky, this border shall be added to.
	 */
	public SelectionBorder(IWorld world) {
		super();
		init(world);

	}

	/**
	 * @param world
	 *            World, whose sky, this border shall be added to.
	 * @param objSelected
	 *            Object to select initially
	 */
	public SelectionBorder(IWorld world, IWorldObject objSelected) {
		super();
		init(world);
		setSelected(objSelected);
	}

	/**
	 * Initializes this instance
	 * 
	 * @param world
	 *            World, whose sky, this border shall be added to.
	 */
	private void init(IWorld world) {
		this.frameHolder = world.getSky();
		frame = Path.createRectangle(0f, 0f, 1f, 1f);
		frame.setPickable(false);

		frameHolder.addPropertyChangeListener(EventType.VIEW_TRANSFORM, this);

		frameHolder.addChild(frame);
	}

	/**
	 * Updates the bounds of the border to match those of the selected object
	 */
	protected void updateBounds() {
		if (selectedObj != null && !selectedObj.isDestroyed()) {
			if (selectedObj.getVisible()) {
				Rectangle2D bounds = selectedObj.objectToSky(selectedObj
						.getBounds());

				frame.setBounds((float) bounds.getX(), (float) bounds.getY(),
						(float) bounds.getWidth(), (float) bounds.getHeight());
				frame.setPaint(null);
				frame.setStrokePaint(frameColor);
				frame.setVisible(true);
			} else {
				frame.setVisible(false);
			}
		} else {
			setSelected(null);
		}
	}

	public void destroy() {
		setSelected(null);

		frameHolder
				.removePropertyChangeListener(EventType.VIEW_TRANSFORM, this);
	}

	public Color getFrameColor() {
		return frameColor;
	}

	public void setFrameColor(Color frameColor) {
		this.frameColor = frameColor;
		updateBounds();
	}

	public void setSelected(IWorldObject newSelected) {
		if (newSelected == selectedObj) {
			return;
		}

		if (selectedObj != null) {
			selectedObj.removePropertyChangeListener(EventType.GLOBAL_BOUNDS,
					this);
			selectedObj = null;
		}

		selectedObj = newSelected;
		if (selectedObj != null) {

			selectedObj
					.addPropertyChangeListener(EventType.GLOBAL_BOUNDS, this);

			frameHolder.addChild(frame);
			updateBounds();
		} else {

			frame.removeFromParent();
		}

	}

	public void propertyChanged(EventType event) {
		updateBounds();

	}

}
