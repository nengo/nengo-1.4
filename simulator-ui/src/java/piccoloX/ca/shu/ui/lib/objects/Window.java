package ca.shu.ui.lib.objects;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPopupMenu;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.handlers.EventConsumer;
import ca.shu.ui.lib.handlers.IContextMenu;
import ca.shu.ui.lib.objects.widgets.AffinityHalo;
import ca.shu.ui.lib.objects.widgets.BoundsHandle;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

public class Window extends WorldObjectImpl {
	public static final WindowState WINDOW_STATE_DEFAULT = WindowState.WINDOW;

	private static final int DEFAULT_HEIGHT = 400;

	private static final int DEFAULT_WIDTH = 600;

	private static final long serialVersionUID = 1L;

	private PInputEventListener eventConsumer;

	private PPath menubar;

	AffinityHalo affinityHalo = null;

	WorldObjectImpl attachTo;

	WorldObjectImpl contentNode;

	final int MENU_BAR_HEIGHT = 27;

	MenuBarHandler menuBarHandler;

	/**
	 * 
	 */

	PBounds savedWindowBounds;;

	Point2D savedWindowOffset;

	/**
	 * TODO: Window state control
	 */

	WindowState windowState = WINDOW_STATE_DEFAULT;

	/**
	 * 
	 * @param attachTo
	 *            parent Node to attach this Window to
	 * @param contentNode
	 *            Node containing the contents of this Window
	 */
	public Window(WorldObjectImpl attachTo, WorldObjectImpl contentNode) {
		super();
		this.attachTo = attachTo;

		this.contentNode = contentNode;
		this.setWidth(DEFAULT_WIDTH);
		this.setHeight(DEFAULT_HEIGHT);

		menubar = new MenuBar(this);

		setFrameVisible(true);
		addChild(menubar);
		addChildW(contentNode);
		menuBarHandler = new MenuBarHandler(this);
		menubar.addInputEventListener(menuBarHandler);

		eventConsumer = new EventConsumer();
		addInputEventListener(new EventConsumer());
		windowStateChanged();
	}

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

	@Override
	public void destroy() {
		super.destroy();

		menubar.removeInputEventListener(menuBarHandler);
		removeInputEventListener(eventConsumer);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return contentNode.getName();
	}

	public WindowState getWindowState() {
		return windowState;
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

	protected void maximizeBounds() {
		setBounds(parentToLocal(getParent().getBounds()));
	}

	@Override
	protected void parentBoundsChanged() {
		// TODO Auto-generated method stub
		super.parentBoundsChanged();

		if (windowState == WindowState.MAXIMIZED) {
			maximizeBounds();
		}

	}

	protected void windowStateChanged() {
		switch (windowState) {
		case MAXIMIZED:

			if (affinityHalo != null) {
				affinityHalo.destroy();
				affinityHalo = null;
			}

			menuBarHandler.setEnabled(false);
			UIEnvironment.getInstance().addChild(this);

			maximizeBounds();

			BoundsHandle.removeBoundsHandlesFrom(this);
			break;
		case WINDOW:
			if (savedWindowBounds != null) {
				setBounds(savedWindowBounds);
				setOffset(savedWindowOffset);
			}
			menuBarHandler.setEnabled(true);
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

	public static enum WindowState {
		MAXIMIZED, MINIMIZED, WINDOW
	}

}

class MenuBar extends PPath {

	private static final long serialVersionUID = 1L;
	GButton maximizeButton, minimizeButton;
	GText title;

	Window window;

	public MenuBar(Window window) {
		super(new Rectangle2D.Double(0, 0, 1, 1));
		this.window = window;

		init();
	}

	private void init() {
		title = new GText(window.getName());
		title.setFont(Style.FONT_LARGE);
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
		title.setBounds(4, 3, getWidth(), getHeight());

		double buttonX = getWidth() - maximizeButton.getWidth() - 2;
		maximizeButton.setOffset(buttonX, 2);
		buttonX -= minimizeButton.getWidth() - 2;
		minimizeButton.setOffset(buttonX, 2);
	}

}

class MenuBarHandler extends PDragSequenceEventHandler {

	private boolean moveToFrontOnPress = false;

	private Window window;
	boolean enabled = true;

	public MenuBarHandler(Window window) {
		super();
		this.window = window;
		// setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
	}

	public boolean getMoveToFrontOnPress() {
		return moveToFrontOnPress;
	}

	@Override
	public void mouseClicked(PInputEvent event) {
		super.mouseClicked(event);

		/*
		 * Opens the context menu associated with the Content Node of the Window
		 * Object
		 */
		if (event.getButton() == MouseEvent.BUTTON3) {
			if (window.contentNode instanceof IContextMenu) {
				IContextMenu menuNode = ((IContextMenu) (window.contentNode));

				if (menuNode.isContextMenuEnabled()) {
					JPopupMenu menu = menuNode.showContextMenu(event);

					if (menu != null) {
						menu.setVisible(true);
						MouseEvent e = (MouseEvent) event.getSourceSwingEvent();

						menu.show(e.getComponent(), e.getPoint().x, e
								.getPoint().y);
					}
				}
			}
		}
	}

	public void setMoveToFrontOnPress(boolean moveToFrontOnPress) {
		this.moveToFrontOnPress = moveToFrontOnPress;
	}

	protected void drag(PInputEvent event) {
		super.drag(event);
		PDimension d = event.getDeltaRelativeTo(window);
		window.localToParent(d);
		window.offset(d.getWidth(), d.getHeight());
	}

	protected void endDrag(PInputEvent event) {
		super.endDrag(event);
	}

	protected boolean isEnabled() {
		return enabled;
	}

	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	// ****************************************************************
	// Debugging - methods for debugging
	// ****************************************************************

	protected boolean shouldStartDragInteraction(PInputEvent event) {
		if (super.shouldStartDragInteraction(event)
				&& event.getButton() == MouseEvent.BUTTON1) {
			return event.getPickedNode() != event.getTopCamera();
		}
		return false;
	}

	protected void startDrag(PInputEvent event) {
		if (!enabled)
			return;

		super.startDrag(event);

		if (moveToFrontOnPress) {
			window.moveToFront();
		}
	}
}
