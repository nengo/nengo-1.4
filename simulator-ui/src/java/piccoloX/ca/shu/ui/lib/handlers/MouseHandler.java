package ca.shu.ui.lib.handlers;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.objects.SelectionBorder;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Handles mouse events. Passes double click and mouse context button events to
 * World Objects. Displays a frame around interactable objects as the mouse
 * moves.
 * 
 * @author Shu Wu
 */
public class MouseHandler extends PBasicInputEventHandler {

	/**
	 * Hand cursor
	 */
	private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * Maximum distance that the mouse is allowed to drag before the handler
	 * gives up on the context menu
	 */
	private static final double MAX_CONTEXT_MENU_DRAG_DISTANCE = 20;

	private SelectionBorder frame;

	private boolean handCursorShown = false;

	private Interactable interactableObj;

	private int mouseButtonPressed = -1;

	private Point2D mouseCanvasPositionPressed;

	private World world;

	public MouseHandler(World world) {
		super();
		frame = new SelectionBorder(world);
		frame.setFrameColor(Style.COLOR_TOOLTIP_BORDER);
		this.world = world;
	}

	/**
	 * @return Interactable object
	 */
	private Interactable getInteractableFromEvent(PInputEvent event) {
		Interactable obj = (Interactable) Util.getNodeFromPickPath(event,
				Interactable.class);

		if (obj == null || !world.isAncestorOf((WorldObject) obj)) {
			return null;
		} else {
			return obj;
		}
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

		Interactable obj = getInteractableFromEvent(event);

		/*
		 * Show cursor and frame around interactable objects NOTE: Do not show
		 * cursor and frame around Windows or Worlds
		 */
		if (obj == null || (obj instanceof Window) || (obj instanceof World)) {
			if (handCursorShown) {
				handCursorShown = false;
				event.getComponent().popCursor();
			}

			frame.setSelected(null);
		} else {
			if (!handCursorShown) {
				handCursorShown = true;
				event.getComponent().pushCursor(HAND_CURSOR);
			}

			frame.setSelected((WorldObject) obj);

		}

	}

	@Override
	public void mousePressed(PInputEvent event) {
		super.mousePressed(event);

		if (event.getButton() == MouseEvent.BUTTON3) {
			mouseCanvasPositionPressed = event.getCanvasPosition();

			mouseButtonPressed = event.getButton();
			interactableObj = getInteractableFromEvent(event);
		} else {
			mouseButtonPressed = -1;
			interactableObj = null;
		}

	}

	@Override
	public void mouseReleased(PInputEvent event) {
		super.mouseReleased(event);

		if (((mouseButtonPressed == event.getButton()) && mouseCanvasPositionPressed
				.distance(event.getCanvasPosition()) < MAX_CONTEXT_MENU_DRAG_DISTANCE)) {

			JPopupMenu menuToShow = world.showSelectionContextMenu();

			if (menuToShow == null
					&& (interactableObj != null)
					&& interactableObj.isContextMenuEnabled()
					&& (interactableObj == (Interactable) Util
							.getNodeFromPickPath(event, Interactable.class))) {
				menuToShow = interactableObj.showContextMenu();

			}

			if (menuToShow != null) {
				menuToShow.setVisible(true);
				MouseEvent e = (MouseEvent) event.getSourceSwingEvent();

				menuToShow.show(e.getComponent(), e.getPoint().x,
						e.getPoint().y);
			}
		}

	}
}
