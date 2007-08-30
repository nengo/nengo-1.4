package ca.shu.ui.lib.handlers;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.objects.widgets.MoveableFrame;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class MouseHandler extends PBasicInputEventHandler {

	MoveableFrame frame;

	/*
	 * Maximum distance that the mouse is allowed to drag before the handler
	 * gives up on the context menu
	 */
	static final double MAX_CONTEXT_MENU_DRAG_DISTANCE = 20;

	int mouseButtonPressed = -1;

	Point2D mouseCanvasPositionPressed;

	Interactable objPressed;
	World world;

	public MouseHandler(World world) {
		super();
		frame = new MoveableFrame(world);
		frame.setFrameColor(Style.COLOR_TOOLTIP_BORDER);
		this.world = world;
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		if (event.getClickCount() == 2) {
			PNode node = event.getPickedNode();

			while (node != null) {
				if (node instanceof WorldObject) {

					WorldObject wo = (WorldObject) node;

					wo.doubleClicked();

					break;
				}
				node = node.getParent();
			}

		}
		super.mouseClicked(event);

	}

	@Override
	public void mouseMoved(PInputEvent event) {

		Interactable obj = (Interactable) Util.getNodeFromPickPath(event,
				Interactable.class);

		if (obj == null || !world.isAncestorOf((WorldObject) obj)) {
			frame.setSelected(null);
		} else {
			frame.setSelected((WorldObject) obj);

		}

	}

	@Override
	public void mousePressed(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mousePressed(event);

		if (event.getButton() == MouseEvent.BUTTON3) {
			mouseCanvasPositionPressed = event.getCanvasPosition();

			mouseButtonPressed = event.getButton();
			objPressed = (Interactable) Util.getNodeFromPickPath(event,
					Interactable.class);
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
						.getCanvasPosition()) < MAX_CONTEXT_MENU_DRAG_DISTANCE)
				&& (mouseButtonPressed == event.getButton())
				&& (objPressed == (Interactable) Util.getNodeFromPickPath(
						event, Interactable.class))) {

			if (objPressed.isContextMenuEnabled()) {
				JPopupMenu menu = objPressed.showContextMenu(event);

				if (menu != null) {
					menu.setVisible(true);
					MouseEvent e = (MouseEvent) event.getSourceSwingEvent();

					menu.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
				}
			}
		}
	}

}
