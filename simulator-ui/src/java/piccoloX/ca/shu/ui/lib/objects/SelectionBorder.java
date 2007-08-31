package ca.shu.ui.lib.objects;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldSky;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A Border instance that can change its object of focus (As long as the object
 * are within the world that the frame is in). Border is attached to the sky
 * layer so there is no attenuation of the edge width when the ground is viewed
 * at a low scale.
 * 
 * @author Shu Wu
 */
public class SelectionBorder implements PropertyChangeListener {
	private PPath frame;

	private Color frameColor = Style.COLOR_BORDER_SELECTED;

	private WorldSky frameHolder;

	private WorldObject selectedObj;

	/**
	 * @param world
	 *            World, whose sky, this border shall be added to.
	 */
	public SelectionBorder(World world) {
		super();
		init(world);

	}

	/**
	 * @param world
	 *            World, whose sky, this border shall be added to.
	 * @param objSelected
	 *            Object to select initially
	 */
	public SelectionBorder(World world, WorldObject objSelected) {
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
	private void init(World world) {
		this.frameHolder = world.getSky();
		frame = PPath.createRectangle(0f, 0f, 1f, 1f);
		frame.setPickable(false);

		frameHolder.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM,
				this);

		frameHolder.addChild(frame);
	}

	/**
	 * Updates the bounds of the border to match those of the selected object
	 */
	protected void updateBounds() {
		if (selectedObj != null && !selectedObj.isDestroyed()) {
			if (selectedObj.getVisible()) {
				Rectangle2D bounds = selectedObj
						.objectToSky(selectedObj.getBounds());

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

		frameHolder.removePropertyChangeListener(
				PCamera.PROPERTY_VIEW_TRANSFORM, this);
	}

	public Color getFrameColor() {
		return frameColor;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		updateBounds();
	}

	public void setFrameColor(Color frameColor) {
		this.frameColor = frameColor;
		updateBounds();
	}

	public void setSelected(WorldObject newSelected) {
		if (newSelected == selectedObj) {
			return;
		}

		if (selectedObj != null) {
			selectedObj.removePropertyChangeListener(
					WorldObject.PROPERTY_GLOBAL_BOUNDS, this);
		}

		selectedObj = newSelected;
		if (selectedObj != null) {

			selectedObj.addPropertyChangeListener(
					WorldObject.PROPERTY_GLOBAL_BOUNDS, this);

			frameHolder.addChild(frame);
			updateBounds();
		} else {

			frame.removeFromParent();
		}

	}

}
