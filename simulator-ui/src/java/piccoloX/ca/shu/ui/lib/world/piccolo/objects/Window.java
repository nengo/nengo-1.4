package ca.shu.ui.lib.world.piccolo.objects;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.List;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.EventListener;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.handlers.EventConsumer;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.primitives.Text;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * A Window which can be minimized, normal, maximized and closed. A Window wraps
 * another world object which contains content displayed in the window.
 * 
 * @author Shu Wu
 */
/**
 * @author User
 */
public class Window extends WorldObjectImpl implements Interactable {
	private static final int DEFAULT_HEIGHT = 400;

	private static final int DEFAULT_WIDTH = 600;

	private static final int MENU_BAR_HEIGHT = 27;

	private static final long serialVersionUID = 1L;

	public static final WindowState WINDOW_STATE_DEFAULT = WindowState.NORMAL;

	/**
	 * Updates the application title with the top window
	 */
	public static void updateAppTitle() {
		List<Window> windows = UIEnvironment.getInstance().getUniverse().getWorldWindows();

		if (windows.size() > 0) {
			UIEnvironment.getInstance().setTitle(
					windows.get(windows.size() - 1).getName() + " - "
							+ UIEnvironment.getInstance().getAppWindowTitle());
		} else {
			UIEnvironment.getInstance().restoreDefaultTitle();
		}

	}

	private final MenuBar menubar;

	private Border myBorder;

	private PClip myClippingRectangle;

	private final WorldObjectImpl myContent;

	private EventConsumer myEventConsumer;;

	private final WeakReference<WorldObjectImpl> mySourceRef;

	private RectangularEdge mySourceShadow = null;

	private WindowState myState = WINDOW_STATE_DEFAULT;

	private Rectangle2D savedWindowBounds;

	private Point2D savedWindowOffset;

	private WindowState savedWindowState = WINDOW_STATE_DEFAULT;

	/**
	 * @param source
	 *            parent Node to attach this Window to
	 * @param content
	 *            Node containing the contents of this Window
	 */
	public Window(WorldObjectImpl source, WorldObjectImpl content) {
		super();
		mySourceRef = new WeakReference<WorldObjectImpl>(source);
		setSelectable(true);
		this.myContent = content;

		menubar = new MenuBar(this);

		myClippingRectangle = new PClip();
		myClippingRectangle.addChild(content.getPiccolo());
		myClippingRectangle.setPaint(Style.COLOR_BACKGROUND);
		myBorder = new Border(this, Style.COLOR_FOREGROUND);

		getPiccolo().addChild(myClippingRectangle);
		addChild(menubar);
		addChild(myBorder);

		windowStateChanged();

		addPropertyChangeListener(EventType.PARENTS_BOUNDS, new EventListener() {
			public void propertyChanged(EventType event) {
				if (myState == WindowState.MAXIMIZED) {
					maximizeBounds();
				}
			}
		});

	}

	protected void maximizeBounds() {
		setOffset(0, 0);
		setBounds(parentToLocal(getParent().getBounds()));
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
		menubar.updateButtons();
		switch (myState) {
		case MAXIMIZED:

			if (mySourceShadow != null) {
				mySourceShadow.destroy();
				mySourceShadow = null;
			}
			myBorder.setVisible(false);
			UIEnvironment.getInstance().addWorldWindow(this);

			if (myEventConsumer == null) {
				myEventConsumer = new EventConsumer();
				addInputEventListener(myEventConsumer);
			}

			maximizeBounds();

			BoundsHandle.removeBoundsHandlesFrom(this);
			break;
		case NORMAL:
			WorldObjectImpl source = mySourceRef.get();

			if (savedWindowBounds != null) {
				setBounds(savedWindowBounds);
				setOffset(savedWindowOffset);
			} else {
				setWidth(DEFAULT_WIDTH);
				setHeight(DEFAULT_HEIGHT);
				if (source != null) {
					setOffset((getWidth() - source.getWidth()) / -2f, source.getHeight() + 20);
				}

			}
			if (source != null) {
				if (myEventConsumer != null) {
					removeInputEventListener(myEventConsumer);
					myEventConsumer = null;
				}

				source.addChild(this);
				myBorder.setVisible(true);

				BoundsHandle.addBoundsHandlesTo(this);
				if (mySourceShadow == null) {

					mySourceShadow = new RectangularEdge(source, this);
					source.addChild(mySourceShadow, 0);
				}
			} else {
				Util.Assert(false, "Window still active after source destroyed");
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
		updateAppTitle();
		layoutChildren();
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

	public JPopupMenu getContextMenu() {
		if (getWindowContent() instanceof Interactable) {
			return ((Interactable) (getWindowContent())).getContextMenu();
		}
		return null;
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

	@Override
	public void layoutChildren() {
		super.layoutChildren();

		menubar.setBounds(0, 0, getWidth(), MENU_BAR_HEIGHT);

		myContent.setBounds(0, 0, getWidth() - 4, getHeight() - 4 - MENU_BAR_HEIGHT);
		myContent.setOffset(2, 2 + MENU_BAR_HEIGHT);

		myClippingRectangle.setPathToRectangle((float) getX(), (float) getY(), (float) getWidth(),
				(float) getHeight());

	}

	@Override
	public void moveToFront() {
		super.moveToFront();
		if (mySourceRef.get() != null) {
			mySourceRef.get().moveToFront();
		}
	}

	public void restoreSavedWindow() {
		setWindowState(savedWindowState);
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
				savedWindowState = myState;
			}

			myState = state;
			windowStateChanged();
		}
	}

	public static enum WindowState {
		MAXIMIZED, MINIMIZED, NORMAL
	}

}

class MenuBar extends WorldObjectImpl implements PInputEventListener {

	private static final long serialVersionUID = 1L;
	private AbstractButton maximizeButton, minimizeButton, closeButton, normalButton;
	private Window myWindow;

	private PPath rectangle;
	private Text title;

	public MenuBar(Window window) {
		super();
		this.myWindow = window;
		init();
	}

	private void init() {
		getPiccolo().addInputEventListener(this);
		rectangle = new PPath(new Rectangle2D.Double(0, 0, 1, 1));

		getPiccolo().addChild(rectangle);

		title = new Text(myWindow.getName());
		title.setFont(Style.FONT_LARGE);
		addChild(title);

		normalButton = new ImageButton("images/icons/restore.gif", new Runnable() {
			public void run() {
				myWindow.setWindowState(Window.WindowState.NORMAL);
			}
		});

		maximizeButton = new ImageButton("images/icons/maximize.gif", new Runnable() {
			public void run() {
				myWindow.setWindowState(Window.WindowState.MAXIMIZED);
			}
		});

		minimizeButton = new ImageButton("images/icons/minimize.gif", new Runnable() {
			public void run() {
				myWindow.setWindowState(Window.WindowState.MINIMIZED);
			}
		});

		closeButton = new ImageButton("images/icons/close.gif", new Runnable() {
			public void run() {
				myWindow.close();
			}
		});

		addChild(maximizeButton);
		addChild(normalButton);
		addChild(minimizeButton);
		addChild(closeButton);

		rectangle.setPaint(Style.COLOR_BACKGROUND2);
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();
		title.setBounds(4, 3, getWidth(), getHeight());

		double buttonX = getWidth() - closeButton.getWidth();
		closeButton.setOffset(buttonX, 0);
		buttonX -= closeButton.getWidth();
		maximizeButton.setOffset(buttonX, 0);
		normalButton.setOffset(buttonX, 0);
		buttonX -= minimizeButton.getWidth();
		minimizeButton.setOffset(buttonX, 0);
		rectangle.setBounds(getBounds());
	}

	public void processEvent(PInputEvent event, int type) {
		if (type == MouseEvent.MOUSE_CLICKED && event.getClickCount() == 2) {
			myWindow.cycleVisibleWindowState();
		}
	}

	@Override
	public void setOffset(double arg0, double arg1) {
		// TODO Auto-generated method stub
		super.setOffset(arg0, arg1);
	}

	public void updateButtons() {
		boolean isWindowMaximized = (myWindow.getWindowState() == Window.WindowState.MAXIMIZED);

		if (isWindowMaximized) {
			maximizeButton.removeFromParent();
			addChild(normalButton);
		} else {
			normalButton.removeFromParent();
			addChild(maximizeButton);
		}

	}

}
