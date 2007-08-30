package ca.shu.ui.lib.objects.widgets;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.world.WorldObject;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class Border extends WorldObject implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private PNode borderParent;
	PPath frame;
	Color frameColor;

	public Border(PNode objSelected, Color color) {
		super();
		this.frameColor = color;
		this.borderParent = objSelected;

		frame = PPath.createRectangle(0f, 0f, 1f, 1f);
		setPickable(false);
		setChildrenPickable(false);
		setSelectable(false);

		addChild(frame);

		borderParent.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, this);

		updateBounds();
	}

	protected void updateBounds() {
		if (borderParent != null) {

			PBounds bounds = borderParent.getBounds();

			frame.setBounds((float) bounds.x, (float) bounds.y,
					(float) bounds.width, (float) bounds.height);
			frame.setPaint(null);
			frame.setStrokePaint(frameColor);
		}
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		updateBounds();
	}

	@Override
	protected void prepareForDestroy() {

		borderParent.removePropertyChangeListener(PNode.PROPERTY_BOUNDS, this);

		super.prepareForDestroy();
	}

}
