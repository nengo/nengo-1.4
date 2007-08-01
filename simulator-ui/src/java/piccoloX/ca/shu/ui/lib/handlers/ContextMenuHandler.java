package ca.shu.ui.lib.handlers;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.objects.widgets.AutomaticFrame;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.impl.WorldImpl;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class ContextMenuHandler extends PBasicInputEventHandler {

	/*
	 * Maximum distance that the mouse is allowed to drag before the handler
	 * gives up on the context menu
	 */
	static double maxDragDistance = 20;

	static AutomaticFrame frame = new AutomaticFrame();

	int mouseButtonPressed = -1;

	Point2D mouseCanvasPositionPressed;

	IContextMenu objPressed;
	World world;

	public ContextMenuHandler(World world) {
		super();
		this.world = world;
	}

	@Override
	public void mouseMoved(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseMoved(event);

		IContextMenu obj = (IContextMenu) Util.getNodeFromPickPath(event,
				IContextMenu.class);

		if ((obj instanceof WorldImpl)) {
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

