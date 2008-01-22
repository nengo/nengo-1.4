package ca.shu.ui.lib.world.piccolo.objects;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import ca.shu.ui.lib.world.EventListener;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.primitives.Path;

/**
 * Adds a border around an object.
 * 
 * @author Shu Wu
 */
public class Border extends WorldObjectImpl implements EventListener {

	private static final long serialVersionUID = 1L;

	private Color myColor;
	private Path myFrame;
	private WorldObjectImpl myTarget;

	/**
	 * Create a new border
	 * 
	 * @param target
	 *            Object to create a border around
	 * @param color
	 *            Color of border
	 */
	public Border(WorldObjectImpl target, Color color) {
		super();
		this.myColor = color;
		this.myTarget = target;

		myFrame = Path.createRectangle(0f, 0f, 1f, 1f);
		setPickable(false);
		setChildrenPickable(false);

		addChild(myFrame);

		myTarget.addPropertyChangeListener(EventType.BOUNDS_CHANGED, this);

		updateBorder();
	}

	@Override
	protected void prepareForDestroy() {
		/*
		 * Remove listener from target
		 */
		myTarget.removePropertyChangeListener(EventType.BOUNDS_CHANGED, this);

		super.prepareForDestroy();
	}

	/**
	 * Updates the border when the target bounds changes
	 */
	protected void updateBorder() {
		if (myTarget != null) {

			Rectangle2D bounds = myTarget.getBounds();

			myFrame.setBounds((float) bounds.getX(), (float) bounds.getY(), (float) bounds
					.getWidth(), (float) bounds.getHeight());
			myFrame.setPaint(null);
			myFrame.setStrokePaint(myColor);
		}
	}

	public void propertyChanged(EventType event) {
		updateBorder();
	}

}
