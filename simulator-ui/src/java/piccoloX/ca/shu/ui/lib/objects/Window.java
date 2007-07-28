package ca.shu.ui.lib.objects;

import java.awt.event.InputEvent;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.widgets.AffinityHalo;
import ca.shu.ui.lib.objects.widgets.BoundsHandle;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

public class Window extends WorldObjectImpl {
	private static final int DEFAULT_HEIGHT = 300;

	private static final int DEFAULT_WIDTH = 400;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private PPath border;

	private PPath menubar;

	WorldObjectImpl contentNode;

	final int MENU_BAR_HEIGHT = 33;

	GText title;

	WorldObjectImpl attachTo;

	public Window(WorldObjectImpl attachTo, WorldObjectImpl innerNode) {
		super();
		this.attachTo = attachTo;

		this.contentNode = innerNode;
		this.setWidth(DEFAULT_WIDTH);
		this.setHeight(DEFAULT_HEIGHT);

		menubar = PPath.createRectangle(0, 0, 1, 1);
		menubar.setPaint(Style.BACKGROUND2_COLOR);

		title = new GText(innerNode.getName());
		title.setFont(Style.FONT_XLARGE);
		menubar.addChild(title);

		// border = PPath.createRectangle(0, 0, 1, 1);
		// border.setPaint(GDefaults.BACKGROUND_COLOR);
		// border.setStrokePaint(GDefaults.FOREGROUND_COLOR);

		setFrameVisible(true);

		// addChild(border);
		addChild(menubar);
		addChildW(innerNode);
		// PBoundsHandle.addBoundsHandlesTo(innerNode);

		menubar.addInputEventListener(new FrameDragHandler(this));
		BoundsHandle.addBoundsHandlesTo(this);

		addInputEventListener(new PInputEventListener() {
			public void processEvent(PInputEvent aEvent, int type) {
				aEvent.setHandled(true);
			}
		});
		restore();
	}

	public static enum WindowState {
		MINIMIZED, RESTORED, FULL_SCREEN
	};

	WindowState windowState = WindowState.RESTORED;

	public void minimize() {
		removeFromParent();
		windowState = WindowState.MINIMIZED;
		affinityHalo.removeFromParent();
		// affinityHalo.setVisible(false);
	}

	AffinityHalo affinityHalo = null;

	public void restore() {
		attachTo.addChild(this);
		windowState = WindowState.RESTORED;

		if (affinityHalo != null) {
			affinityHalo.removeFromParent();
		}
		affinityHalo = new AffinityHalo(attachTo, this);

	}

	@Override
	public boolean setBounds(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		boolean rtnValue = super.setBounds(x, y, width, height);

		contentNode.setBounds(contentNode.getX(), contentNode.getY(), width,
				height);

		return rtnValue;

	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		// Bounds always start at 0,0
		PBounds bounds = this.getBounds();
		this.translate(bounds.getOrigin().getX(), bounds.getOrigin().getY());
		bounds.setOrigin(0, 0);
		this.setBounds(bounds);
		// System.out.println(getBounds().getOrigin());

		// if (isInitialized) {
		menubar.setBounds(1, 1, getWidth() - 2, MENU_BAR_HEIGHT);
		title.setBounds(3, 3, getWidth(), MENU_BAR_HEIGHT);

		// border.setBounds(-2, -2, getWidth() + 4, getHeight() + 4);
		contentNode.setBounds(0, 0, getWidth() - 4, getHeight() - 4
				- MENU_BAR_HEIGHT);
		contentNode.setOffset(2, 2 + MENU_BAR_HEIGHT);
		// }
	}

	public WindowState getWindowState() {
		return windowState;
	}

}

class FrameDragHandler extends PDragSequenceEventHandler {

	private PNode draggedNode;

	private boolean moveToFrontOnPress = false;

	public FrameDragHandler(PNode draggedNode) {
		super();
		this.draggedNode = draggedNode;
		setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
	}

	public boolean getMoveToFrontOnPress() {
		return moveToFrontOnPress;
	}

	public void setMoveToFrontOnPress(boolean moveToFrontOnPress) {
		this.moveToFrontOnPress = moveToFrontOnPress;
	}

	protected void drag(PInputEvent event) {
		super.drag(event);
		PDimension d = event.getDeltaRelativeTo(draggedNode);
		draggedNode.localToParent(d);
		draggedNode.offset(d.getWidth(), d.getHeight());
	}

	protected void endDrag(PInputEvent event) {
		super.endDrag(event);
	}

	/**
	 * Returns a string representing the state of this node. This method is
	 * intended to be used only for debugging purposes, and the content and
	 * format of the returned string may vary between implementations. The
	 * returned string may be empty but may not be <code>null</code>.
	 * 
	 * @return a string representation of this node's state
	 */
	protected String paramString() {
		StringBuffer result = new StringBuffer();

		result.append("draggedNode=" + draggedNode == null ? "null"
				: draggedNode.toString());
		if (moveToFrontOnPress)
			result.append(",moveToFrontOnPress");
		result.append(',');
		result.append(super.paramString());

		return result.toString();
	}

	protected boolean shouldStartDragInteraction(PInputEvent event) {
		if (super.shouldStartDragInteraction(event)) {
			return event.getPickedNode() != event.getTopCamera();
		}
		return false;
	}

	// ****************************************************************
	// Debugging - methods for debugging
	// ****************************************************************

	protected void startDrag(PInputEvent event) {
		super.startDrag(event);

		if (moveToFrontOnPress) {
			draggedNode.moveToFront();
		}
	}
}
