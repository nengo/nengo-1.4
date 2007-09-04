package ca.shu.ui.lib.objects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * A Window which can be minimized, normal, maximized and closed. A Window wraps
 * another world object which contains content displayed in the window.
 * 
 * @author Shu Wu
 */
public class Window extends WorldObject implements Interactable {
	private static final int DEFAULT_HEIGHT = 400;

	private static final int DEFAULT_WIDTH = 600;

	private static final int MENU_BAR_HEIGHT = 27;

	private static final long serialVersionUID = 1L;

	public static final WindowState WINDOW_STATE_DEFAULT = WindowState.NORMAL;

	private final PPath menubar;

	private PClip myClippingRectangle;

	private final WorldObject myContent;

	private final WeakReference<WorldObject> mySourceRef;

	private WindowState savedRestoreState = WINDOW_STATE_DEFAULT;

	private PBounds savedWindowBounds;;

	private Point2D savedWindowOffset;

	private RectangularEdge mySourceShadow = null;

	private WindowState myState = WINDOW_STATE_DEFAULT;

	/**
	 * @param source
	 *            parent Node to attach this Window to
	 * @param content
	 *            Node containing the contents of this Window
	 */
	public Window(WorldObject source, WorldObject content) {
		super();
		mySourceRef = new WeakReference<WorldObject>(source);

		this.myContent = content;

		menubar = new MenuBar(this);

		myClippingRectangle = new PClip();
		myClippingRectangle.addChild(content);
		myClippingRectangle.setPaint(Style.COLOR_BACKGROUND);
		addChild(myClippingRectangle);
		addChild(menubar);
		addChild(new Border(this, Style.COLOR_FOREGROUND));

		windowStateChanged();
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		menubar.setBounds(1, 1, getWidth() - 2, MENU_BAR_HEIGHT);

		myContent.setBounds(0, 0, getWidth() - 4, getHeight() - 4
				- MENU_BAR_HEIGHT);
		myContent.setOffset(2, 2 + MENU_BAR_HEIGHT);

		myClippingRectangle.setPathToRectangle((float) getX(), (float) getY(),
				(float) getWidth(), (float) getHeight());

	}

	protected void maximizeBounds() {
		setOffset(0, 0);
		setBounds(parentToLocal(getParent().getBounds()));
	}

	@Override
	protected void parentBoundsChanged() {
		super.parentBoundsChanged();

		if (myState == WindowState.MAXIMIZED) {
			maximizeBounds();
		}
	}

	@Override
	protected void prepareForDestroy() {
		if (mySourceShadow != null) {
			mySourceShadow.destroy();
			mySourceShadow = null;
		}

		myContent.destroy();
		super.prepareForDestroy();
	}

	protected void windowStateChanged() {
		switch (myState) {
		case MAXIMIZED:

			if (mySourceShadow != null) {
				mySourceShadow.destroy();
				mySourceShadow = null;
			}

			setSelectable(false);
			UIEnvironment.getInstance().getWorld().getSky().addChild(this);

			maximizeBounds();

			BoundsHandle.removeBoundsHandlesFrom(this);
			break;
		case NORMAL:
			WorldObject source = mySourceRef.get();

			if (savedWindowBounds != null) {
				setBounds(savedWindowBounds);
				setOffset(savedWindowOffset);
			} else {
				setWidth(DEFAULT_WIDTH);
				setHeight(DEFAULT_HEIGHT);
				if (source != null) {
					setOffset((getWidth() - source.getWidth()) / -2f, source
							.getHeight() + 20);
				}

			}
			if (source != null) {

				setSelectable(true);

				source.addChild(this);

				BoundsHandle.addBoundsHandlesTo(this);
				if (mySourceShadow == null) {

					mySourceShadow = new RectangularEdge(source, this);
					source.addChild(1, mySourceShadow);
				}
			}

			break;
		case MINIMIZED:

			if (mySourceShadow != null) {
				mySourceShadow.destroy();
				mySourceShadow = null;
			}
			if (mySourceRef.get() != null) {
				mySourceRef.get().addChild(this);
			}
			break;
		}
		if (myState == WindowState.MINIMIZED) {
			setVisible(false);
			setChildrenPickable(false);
			setPickable(false);
		} else {
			setVisible(true);
			setChildrenPickable(true);
			setPickable(true);
		}
	}

	/**
	 * Closes the window
	 */
	public void close() {
		destroy();
	}

	/**
	 * Increases the size of the window through state transitions
	 */
	public void cycleVisibleWindowState() {
		switch (myState) {
		case MAXIMIZED:
			setWindowState(WindowState.NORMAL);
			break;
		case NORMAL:
			setWindowState(WindowState.MAXIMIZED);
			break;
		case MINIMIZED:
			setWindowState(WindowState.NORMAL);
		}

	}

	@Override
	public void doubleClicked() {
		cycleVisibleWindowState();

	}

	@Override
	public String getName() {
		return myContent.getName();
	}

	/**
	 * @return Node representing the contents of the Window
	 */
	public WorldObject getWindowContent() {
		return myContent;
	}

	public WindowState getWindowState() {
		return myState;
	}

	public boolean isContextMenuEnabled() {
		if (getWindowContent() instanceof Interactable) {
			return ((Interactable) (getWindowContent())).isContextMenuEnabled();
		}
		return false;
	}

	/**
	 * Decreases the size of the window through state transitions
	 */
	public void minimizeWindow() {
		setWindowState(WindowState.MINIMIZED);

	}

	public void restoreWindow() {
		setWindowState(savedRestoreState);
	}

	public void setWindowState(WindowState state) {

		if (state != myState) {

			/*
			 * Saves the window bounds and offset
			 */
			if (myState == WindowState.NORMAL) {
				savedWindowBounds = getBounds();
				savedWindowOffset = getOffset();
			}
			/*
			 * Saves the previous window state
			 */
			if (state == WindowState.MINIMIZED) {
				savedRestoreState = myState;
			}

			myState = state;
			windowStateChanged();
		}
	}

	public JPopupMenu showContextMenu() {
		if (getWindowContent() instanceof Interactable) {
			return ((Interactable) (getWindowContent())).showContextMenu();
		}
		return null;
	}

	public static enum WindowState {
		MAXIMIZED, MINIMIZED, NORMAL
	}

}

class MenuBar extends PPath {

	private static final long serialVersionUID = 1L;
	private TextButton cycleButton, minimizeButton, closeButton;
	private TextNode title;

	private Window window;

	public MenuBar(Window window) {
		super(new Rectangle2D.Double(0, 0, 1, 1));
		this.window = window;

		init();
	}

	private void init() {
		title = new TextNode(window.getName());
		title.setFont(Style.FONT_LARGE);
		addChild(title);

		cycleButton = new TextButton("=", new Runnable() {
			public void run() {
				window.cycleVisibleWindowState();
			}
		});
		cycleButton.setFont(Style.FONT_WINDOW_BUTTONS);

		minimizeButton = new TextButton("-", new Runnable() {
			public void run() {
				window.minimizeWindow();
			}
		});
		minimizeButton.setFont(Style.FONT_WINDOW_BUTTONS);

		closeButton = new TextButton("X", new Runnable() {
			public void run() {
				window.close();
			}
		});
		closeButton.setFont(Style.FONT_BIG);

		addChild(cycleButton);
		addChild(minimizeButton);
		addChild(closeButton);
		setPaint(Style.COLOR_BACKGROUND2);
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		title.setBounds(4, 3, getWidth(), getHeight());

		double buttonX = getWidth() - closeButton.getWidth() - 2;
		closeButton.setOffset(buttonX, 2);
		buttonX -= closeButton.getWidth() - 2;
		cycleButton.setOffset(buttonX, 2);
		buttonX -= minimizeButton.getWidth() - 2;
		minimizeButton.setOffset(buttonX, 2);
	}

}
