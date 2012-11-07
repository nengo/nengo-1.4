package ca.nengo.ui.lib.world.handlers;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Stack;

import javax.swing.JPopupMenu;

import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.lib.Style.NengoStyle;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.lib.world.Interactable;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.lib.world.piccolo.WorldImpl;
import ca.nengo.ui.lib.world.piccolo.objects.SelectionBorder;
import ca.nengo.ui.lib.world.piccolo.objects.Window;
import ca.nengo.ui.lib.world.piccolo.primitives.PiccoloNodeInWorld;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.nodes.UINetwork;
import ca.nengo.ui.models.viewers.NetworkViewer;
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

	// private int mouseButtonPressed = -1;

	private Point2D mouseCanvasPositionPressed;

	private WorldImpl world;

	public MouseHandler(WorldImpl world) {
		super();
		frame = new SelectionBorder(world);
		frame.setFrameColor(NengoStyle.COLOR_TOOLTIP_BORDER);
		this.world = world;
	}

	/**
	 * @return Interactable object
	 */
	private Interactable getInteractableFromEvent(PInputEvent event) {
		Interactable obj = (Interactable) Util.getNodeFromPickPath(event, Interactable.class);

		if (obj == null || !world.isAncestorOf(obj)) {
			return null;
		} else {
			return obj;
		}
	}

	/**
	 * @param event
	 * @return Was Popup Triggered?
	 */
	private boolean maybeTriggerPopup(PInputEvent event) {
		if (event.isPopupTrigger()) {
			JPopupMenu menuToShow = null;
			MouseEvent e = (MouseEvent) event.getSourceSwingEvent();
			
			if (world.getSelection().size() > 1) {
				menuToShow = world.getSelectionMenu(world.getSelection());
			}

			if (menuToShow == null && (interactableObj != null)
					&& (interactableObj == getInteractableFromEvent(event))) {
				if (interactableObj instanceof WorldImpl) {
					// determine the position to add the pasted object
					WorldObject obj = interactableObj;
					
					// We need to loop through each sub-network to transform
					// the point to each sub-network's coordinate system
					
					// First determine the path from the root node to the sub-network
					// into which we are pasting
					Stack<WorldObject> objStack = new Stack<WorldObject>();
					while (obj != null) {
						objStack.push(obj);
						if (obj instanceof NetworkViewer) {
							UINetwork nViewer = ((NetworkViewer) obj).getViewerParent();
							UINetwork v = nViewer.getNetworkParent();
							if (v != null) obj = v.getViewer();
							else obj = NengoGraphics.getInstance().getWorld();
						} else {
							obj = null;
						}
					}
					
					// Now loop through the stack of nodes and transform the point
					// into the proper network's coordinate system
					Point2D newPosition = mouseCanvasPositionPressed;
					while (!objStack.empty()) {
						obj = objStack.pop();
						newPosition = obj.globalToLocal(newPosition);
						newPosition = ((NodeContainer)obj).localToView(newPosition);
					}
					
					menuToShow = ((WorldImpl)interactableObj).getContextMenu(newPosition.getX(), newPosition.getY());
				} else {
					menuToShow = interactableObj.getContextMenu();
				}
			}

			if (menuToShow != null) {
				menuToShow.show(e.getComponent(), e.getPoint().x, e.getPoint().y);
				menuToShow.setVisible(true);
			}
			return true;
		}
		return false;
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		boolean altClicked = false;
		boolean doubleClicked = false;

		if (event.isAltDown()) {
			altClicked = true;
		} else if (event.getClickCount() == 2) {
			doubleClicked = true;
		}

		if (altClicked || doubleClicked) {
			PNode node = event.getPickedNode();
			while (node != null) {
				if (node instanceof PiccoloNodeInWorld) {

					WorldObject wo = ((PiccoloNodeInWorld) node).getWorldObject();

					if (wo.isSelectable()) {
						if (doubleClicked) {
							wo.doubleClicked();
						}
						if (altClicked) {
							wo.altClicked();
						}
						break;
					}

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
		if (obj == null || (obj instanceof Window) || (obj instanceof WorldImpl)) {
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

		mouseCanvasPositionPressed = event.getCanvasPosition();
		interactableObj = getInteractableFromEvent(event);

		maybeTriggerPopup(event);
	}

	@Override
	public void mouseReleased(PInputEvent event) {
		super.mouseReleased(event);

		/*
		 * Check the mouse hasn't moved too far off from it's pressed position
		 */
		if (mouseCanvasPositionPressed.distance(event.getCanvasPosition()) < MAX_CONTEXT_MENU_DRAG_DISTANCE) {
			maybeTriggerPopup(event);
		}

	}
}
