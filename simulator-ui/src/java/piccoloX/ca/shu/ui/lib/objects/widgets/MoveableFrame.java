package ca.shu.ui.lib.objects.widgets;

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
 * A Frame instance that can change its object of focus (As long as the object
 * are within the world that the frame is in).
 * 
 * @author Shu Wu
 */
public class MoveableFrame implements PropertyChangeListener {
	private WorldObject currentlySelected;
	private PPath frame;
	Color frameColor = Style.COLOR_BORDER_SELECTED;

	WorldSky frameHolder;

	public MoveableFrame(World world) {
		super();
		init(world);

	}

	public MoveableFrame(World world, WorldObject objSelected) {
		super();
		init(world);
		setSelected(objSelected);
	}

	private void init(World world) {
		this.frameHolder = world.getSky();
		frame = PPath.createRectangle(0f, 0f, 1f, 1f);
		frame.setPickable(false);

		frameHolder.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM,
				this);

		frameHolder.addChild(frame);
	}

	protected void updateBounds() {
		if (currentlySelected != null && !currentlySelected.isDestroyed()) {
			if (currentlySelected.getVisible()) {
				Rectangle2D bounds = currentlySelected
						.objectToSky(currentlySelected.getBounds());

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
		if (newSelected == currentlySelected) {
			return;
		}

		if (currentlySelected != null) {
			currentlySelected.removePropertyChangeListener(
					WorldObject.PROPERTY_GLOBAL_BOUNDS, this);
		}

		currentlySelected = newSelected;
		if (currentlySelected != null) {

			currentlySelected.addPropertyChangeListener(
					WorldObject.PROPERTY_GLOBAL_BOUNDS, this);

			// currentlySelected.addChild(frame);
			// frame.setVisible(true);
			frameHolder.addChild(frame);
			updateBounds();
		} else {

			frame.removeFromParent();
			// frame.setVisible(false);
		}

	}

}
