package ca.shu.ui.lib.handlers;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPopupMenu;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.impl.World;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

public class ContextMenuHandler extends PBasicInputEventHandler {

	/*
	 * Maximum distance that the mouse is allowed to drag before the handler
	 * gives up on the context menu
	 */
	static double maxDragDistance = 20;

	AutomaticFrame frame = new AutomaticFrame();

	int mouseButtonPressed = -1;

	Point2D mouseCanvasPositionPressed;

	IContextMenu objPressed;
	IWorld world;

	public ContextMenuHandler(IWorld world) {
		super();
		this.world = world;
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseMoved(event);

		IContextMenu obj = (IContextMenu) Util.getNodeFromPickPath(event,
				IContextMenu.class);

		if ((obj instanceof World)) {
			frame.setObjToFollow(null);
		} else {
			frame.setObjToFollow(obj);
		}

	}

	@Override
	public void mousePressed(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mousePressed(event);

		if (event.getButton() == MouseEvent.BUTTON3) {
			mouseCanvasPositionPressed = event.getCanvasPosition();

			mouseButtonPressed = event.getButton();
			objPressed = (IContextMenu) Util.getNodeFromPickPath(event,
					IContextMenu.class);
		} else {
			mouseButtonPressed = -1;
			objPressed = null;
		}

	}

	@Override
	public void mouseReleased(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseReleased(event);

		if ((objPressed != null)
				&& (mouseCanvasPositionPressed.distance(event
						.getCanvasPosition()) < maxDragDistance)
				&& (mouseButtonPressed == event.getButton())
				&& (objPressed == (IContextMenu) Util.getNodeFromPickPath(
						event, IContextMenu.class))) {

			JPopupMenu menu = objPressed.showPopupMenu(event);

			if (menu != null) {
				menu.setVisible(true);
				MouseEvent e = (MouseEvent) event.getSourceSwingEvent();

				menu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
			}
			// event.setHandled(true);
		}
	}
}

class AutomaticFrame implements PropertyChangeListener {
	PPath frame = PPath.createRectangle(0f, 0f, 1f, 1f);
	IWorldObject objToFollow;
	IWorldObject prevObj = null;

	public IWorldObject getObjToFollow() {
		return objToFollow;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		updateBounds();
	}

	public void setObjToFollow(IWorldObject obj) {
		if (obj == prevObj) {
			return;
		}

		if (objToFollow != null) {
			objToFollow.removePropertyChangeListener(this);
		}

		objToFollow = obj;
		if (objToFollow != null) {

			objToFollow.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, this);
			objToFollow.addChild(frame);
			frame.setVisible(true);
			updateBounds();
		} else {
			frame.setVisible(false);
		}
		prevObj = objToFollow;
	}

	public void updateBounds() {
		if (objToFollow != null) {

			PBounds bounds = objToFollow.getBounds();

			frame.setBounds((float) bounds.x, (float) bounds.y,
					(float) bounds.width, (float) bounds.height);
			frame.setPaint(null);
			frame.setStrokePaint(Style.COLOR_BORDER_CONTEXT);
		}
	}

}
