package ca.shu.ui.lib.objects;

import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.widgets.AffinityHalo;
import ca.shu.ui.lib.objects.widgets.BoundsHandle;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

class MenuBar extends PPath {
	Window window;
	GText title;

	public MenuBar(Window window) {
		super(new Rectangle2D.Double(0, 0, 1, 1));
		this.window = window;

		init();
	}

	GButton maximizeButton, minimizeButton;

	private void init() {
		title = new GText(window.getName());
		title.setFont(Style.FONT_XLARGE);
		addChild(title);

		maximizeButton = new GButton("+", new Runnable() {
			public void run() {
				window.increaseWindowSize();
			}
		});
		maximizeButton.setFont(Style.FONT_BIG);

		minimizeButton = new GButton("-", new Runnable() {
			public void run() {
				window.decreaseWindowSize();
			}
		});
		minimizeButton.setFont(Style.FONT_BIG);

		addChild(maximizeButton);
		addChild(minimizeButton);
		setPaint(Style.COLOR_BACKGROUND2);
	}

	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();
		title.setBounds(2, 2, getWidth(), getHeight());

		double buttonX = getWidth() - maximizeButton.getWidth() - 2;
		maximizeButton.setOffset(buttonX, 2);
		buttonX -= minimizeButton.getWidth() - 2;
		minimizeButton.setOffset(buttonX, 2);
	}

}

public class Window extends WorldObjectImpl {
	private static final int DEFAULT_HEIGHT = 300;

	private static final int DEFAULT_WIDTH = 400;
	public static final WindowState WINDOW_STATE_DEFAULT = WindowState.WINDOW;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private PPath border;

	private PPath menubar;

	WorldObjectImpl contentNode;

	final int MENU_BAR_HEIGHT = 33;

	WorldObjectImpl attachTo;

	FrameDragHandler frameDragHandler;

	public Window(WorldObjectImpl attachTo, WorldObjectImpl innerNode) {
		super();
		this.attachTo = attachTo;

		this.contentNode = innerNode;
		this.setWidth(DEFAULT_WIDTH);
		this.setHeight(DEFAULT_HEIGHT);

		menubar = new MenuBar(this);

		setFrameVisible(true);
		addChild(menubar);
		addChildW(innerNode);
		frameDragHandler = new FrameDragHandler(this);
		menubar.addInputEventListener(frameDragHandler);

		addInputEventListener(new PInputEventListener() {
			public void processEvent(PInputEvent aEvent, int type) {
				aEvent.setHandled(true);
			}
		});
		windowStateChanged();
	}

	public static enum WindowState {
		MINIMIZED, WINDOW, MAXIMIZED
	};

	WindowState windowState = WINDOW_STATE_DEFAULT;

	/**
	 * TODO: Window state control
	 */

	/**
	 * Decreases the size of the window through state transitions
	 */
	public void decreaseWindowSize() {
		switch (windowState) {
		case MAXIMIZED:
			setWindowState(WindowState.WINDOW);

			break;
		case WINDOW:
			setWindowState(WindowState.MINIMIZED);

			break;

		}

	}

	/**
	 * Increases the size of the window through state transitions
	 */
	public void increaseWindowSize() {
		switch (windowState) {

		case WINDOW:
			setWindowState(WindowState.MAXIMIZED);
			break;
		case MINIMIZED:
			setWindowState(WindowState.WINDOW);
		}

	}

	public void setWindowState(WindowState state) {
		if (state != windowState) {

			/*
			 * Saves the window state
			 */
			if (windowState == WindowState.WINDOW) {
				savedWindowBounds = getBounds();
				savedWindowOffset = getOffset();
			}

			windowState = state;
			windowStateChanged();
		}
	}

	/**
	 * 
	 */

	PBounds savedWindowBounds;
	Point2D savedWindowOffset;

	@Override
	protected void parentBoundsChanged() {
		// TODO Auto-generated method stub
		super.parentBoundsChanged();

		if (windowState == WindowState.MAXIMIZED) {
			maximizeBounds();
		}

	}

	protected void maximizeBounds() {
		setBounds(parentToLocal(getParent().getBounds()));
	}

	protected void windowStateChanged() {
		switch (windowState) {
		case MAXIMIZED:

			if (affinityHalo != null) {
				affinityHalo.destroy();
				affinityHalo = null;
			}

			frameDragHandler.setEnabled(false);
			UIEnvironment.getInstance().addChild(this);

			maximizeBounds();

			BoundsHandle.removeBoundsHandlesFrom(this);
			break;
		case WINDOW:
			if (savedWindowBounds != null) {
				setBounds(savedWindowBounds);
				setOffset(savedWindowOffset);
			}
			frameDragHandler.setEnabled(true);
			attachTo.addChild(this);

			BoundsHandle.addBoundsHandlesTo(this);
			if (affinityHalo == null) {
				affinityHalo = new AffinityHalo(attachTo, this);
			}

			break;
		case MINIMIZED:

			if (affinityHalo != null) {
				affinityHalo.destroy();
				affinityHalo = null;
			}

			removeFromParent();
			windowState = WindowState.MINIMIZED;
		}
	}

	AffinityHalo affinityHalo = null;

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

		// border.setBounds(-2, -2, getWidth() + 4, getHeight() + 4);
		contentNode.setBounds(0, 0, getWidth() - 4, getHeight() - 4
				- MENU_BAR_HEIGHT);
		contentNode.setOffset(2, 2 + MENU_BAR_HEIGHT);
		// }
	}

	public WindowState getWindowState() {
		return windowState;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return contentNode.getName();
	}

}

class FrameDragHandler extends PDragSequenceEventHandler {

	private PNode draggedNode;

	private boolean moveToFrontOnPress = false;
	boolean enabled = true;

	protected boolean isEnabled() {
		return enabled;
	}

	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

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
		if (!enabled)
			return;

		super.startDrag(event);

		if (moveToFrontOnPress) {
			draggedNode.moveToFront();
		}
	}
}
