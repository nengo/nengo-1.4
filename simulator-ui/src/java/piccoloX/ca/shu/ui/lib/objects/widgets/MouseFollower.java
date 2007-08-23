package ca.shu.ui.lib.objects.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class MouseFollower implements PropertyChangeListener {
	WorldObject currentlySelected;
	PPath frame = PPath.createRectangle(0f, 0f, 1f, 1f);

	// Interactable prevObj = null;

	public WorldObject getObjToFollow() {
		return currentlySelected;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		updateBounds();
	}

	public void setObjToFollow(WorldObject obj) {
		if (obj == currentlySelected) {
			return;
		}

		if (currentlySelected != null) {
			currentlySelected.removePropertyChangeListener(this);
		}

		currentlySelected = obj;
		if (currentlySelected != null) {

			currentlySelected.addPropertyChangeListener(PNode.PROPERTY_BOUNDS,
					this);
			currentlySelected.addChild(frame);
			// frame.setVisible(true);

			updateBounds();
		} else {

			frame.removeFromParent();
			// frame.setVisible(false);
		}

	}

	public void updateBounds() {
		if (currentlySelected != null) {

			PBounds bounds = currentlySelected.getBounds();

			frame.setBounds((float) bounds.x, (float) bounds.y,
					(float) bounds.width, (float) bounds.height);
			frame.setPaint(null);
			frame.setStrokePaint(Style.COLOR_BORDER_CONTEXT);
		}
	}

}
