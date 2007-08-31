package ca.shu.ui.lib.objects;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.world.WorldObject;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Adds a border around an object.
 * 
 * @author Shu Wu
 */
public class Border extends WorldObject implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private Color myColor;
	private PPath myFrame;
	private PNode myTarget;

	/**
	 * Create a new border
	 * 
	 * @param target
	 *            Object to create a border around
	 * @param color
	 *            Color of border
	 */
	public Border(PNode target, Color color) {
		super();
		this.myColor = color;
		this.myTarget = target;

		myFrame = PPath.createRectangle(0f, 0f, 1f, 1f);
		setPickable(false);
		setChildrenPickable(false);
		setSelectable(false);

		addChild(myFrame);

		target.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, this);

		updateBorder();
	}

	@Override
	protected void prepareForDestroy() {
		/*
		 * Remove listener from target
		 */
		myTarget.removePropertyChangeListener(PNode.PROPERTY_BOUNDS, this);

		super.prepareForDestroy();
	}

	/**
	 * Updates the border when the target bounds changes
	 */
	protected void updateBorder() {
		if (myTarget != null) {

			PBounds bounds = myTarget.getBounds();

			myFrame.setBounds((float) bounds.x, (float) bounds.y,
					(float) bounds.width, (float) bounds.height);
			myFrame.setPaint(null);
			myFrame.setStrokePaint(myColor);
		}
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		updateBorder();
	}

}
