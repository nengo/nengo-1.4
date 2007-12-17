package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JPopupMenu;

import ca.neo.ui.actions.ZoomToFitAction;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.handlers.AbstractStatusHandler;
import ca.shu.ui.lib.handlers.EventConsumer;
import ca.shu.ui.lib.handlers.KeyboardFocusHandler;
import ca.shu.ui.lib.handlers.MouseHandler;
import ca.shu.ui.lib.handlers.ScrollZoomHandler;
import ca.shu.ui.lib.handlers.SelectionHandler;
import ca.shu.ui.lib.handlers.TooltipPickHandler;
import ca.shu.ui.lib.handlers.TopWorldStatusHandler;
import ca.shu.ui.lib.objects.TooltipWrapper;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PNodeFilter;

/**
 * Implementation of World. World holds World Objects and has navigation and
 * interaction handlers.
 * 
 * @author Shu Wu
 */
public class World extends WorldObject implements Interactable {

	/**
	 * Padding to use around objects when zooming in on them
	 */
	private static final double OBJECT_ZOOM_PADDING = 100;

	private static final long serialVersionUID = 1L;

	/**
	 * Whether tooltips are enabled
	 */
	private static boolean tooltipsEnabled = true;

	public static final double MAX_ZOOM_SCALE = 5;

	public static final double MIN_ZOOM_SCALE = 0.05;

	public static boolean isTooltipsVisible() {
		return tooltipsEnabled;
	}

	public static void setTooltipsVisible(boolean tooltipsVisible) {
		World.tooltipsEnabled = tooltipsVisible;

	}

	/**
	 * Layer attached to the camera which shows the zoomable grid
	 */
	private PLayer gridLayer;

	/**
	 * If true, then selection mode. If false, then navigation mode.
	 */
	private boolean isSelectionMode;

	/**
	 * PLayer which holds the ground layer
	 */
	private PLayer layer;

	/**
	 * Ground which can be zoomed and navigated
	 */
	private WorldGround myGround;

	/**
	 * Sky, which looks at the ground and whose position and scale remains
	 * static
	 */
	private WorldSky mySkyCamera;

	/**
	 * Panning handler
	 */
	private final PPanEventHandler panHandler;

	/**
	 * Selection handler
	 */
	private SelectionHandler selectionEventHandler;

	/**
	 * Status bar handler
	 */
	private PBasicInputEventHandler statusBarHandler;

	/**
	 * Zoom handler
	 */
	private final PZoomEventHandler zoomHandler;

	/**
	 * Default constructor
	 * 
	 * @param name
	 *            Name of this world
	 */
	public World(String name) {
		super(name);

		/*
		 * Create layer
		 */
		layer = new PLayer();
		getRoot().addChild(layer);

		/*
		 * Create ground
		 */
		myGround = createGround(layer);
		myGround.setSelectable(false);
		layer.addChild(myGround);

		/*
		 * Create camera
		 */
		mySkyCamera = new WorldSky(this);
		mySkyCamera.setPaint(Style.COLOR_BACKGROUND);
		mySkyCamera.addLayer(layer);
		addChild(mySkyCamera);

		/*
		 * Create handlers
		 */
		panHandler = new PPanEventHandler();

		zoomHandler = new PZoomEventHandler();
		zoomHandler.setMinDragStartDistance(20);
		zoomHandler.setMinScale(MIN_ZOOM_SCALE);
		zoomHandler.setMaxScale(MAX_ZOOM_SCALE);

		selectionEventHandler = new SelectionHandler(this);
		selectionEventHandler.setMarqueePaint(Style.COLOR_BORDER_SELECTED);
		selectionEventHandler
				.setMarqueeStrokePaint(Style.COLOR_BORDER_SELECTED);
		selectionEventHandler.setMarqueePaintTransparency(0.1f);

		/*
		 * Attach handlers
		 */
		mySkyCamera.addInputEventListener(zoomHandler);
		mySkyCamera.addInputEventListener(new SwitchSelectionModeHandler());
		mySkyCamera.addInputEventListener(new KeyboardFocusHandler());
		mySkyCamera.addInputEventListener(new TooltipPickHandler(this, 1000,
				1500));
		mySkyCamera.addInputEventListener(new MouseHandler(this));
		mySkyCamera.addInputEventListener(new ScrollZoomHandler());

		addInputEventListener(new EventConsumer());
		setStatusBarHandler(new TopWorldStatusHandler(this));

		/*
		 * Set position and scale
		 */
		// animateToSkyPosition(0, 0);
		setSkyViewScale(0.7f);

		/*
		 * Create the grid
		 */
		gridLayer = Grid.createGrid(getSky(), UIEnvironment.getInstance()
				.getCanvas().getRoot(), Style.COLOR_DARKBORDER, 1500);

		/*
		 * Let the top canvas have a handle on this world
		 */
		UIEnvironment.getInstance().getCanvas().addWorld(this);

		/*
		 * Miscellaneous
		 */
		setSelectable(false);
		setBounds(0, 0, 800, 600);

		initSelectionMode();

	}

	private void initSelectionMode() {
		isSelectionMode = false;
		mySkyCamera.addInputEventListener(panHandler);
		mySkyCamera.addInputEventListener(selectionEventHandler);
	}

	/**
	 * Create context menu
	 * 
	 * @return Menu builder
	 */
	protected void constructMenu(PopupMenuBuilder menu) {

		menu.addAction(new ZoomToFitAction("Zoom to fit", this));
		MenuBuilder windowsMenu = menu.addSubMenu("Windows");
		windowsMenu.addAction(new CloseAllWindows("Close all"));
		windowsMenu.addAction(new MinimizeAllWindows("Minmize all"));

	}

	/**
	 * Create the ground
	 * 
	 * @return ground
	 */
	protected WorldGround createGround(PLayer pLayer) {
		return new WorldGround(this, pLayer);
	}

	@Override
	protected void prepareForDestroy() {
		UIEnvironment.getInstance().getCanvas().removeWorld(this);

		gridLayer.removeFromParent();
		layer.removeFromParent();

		super.prepareForDestroy();
	}

	/**
	 * Sets the view position of the sky, and animates to it.
	 * 
	 * @param x
	 *            X Position relative to ground
	 * @param y
	 *            Y Position relative to ground
	 */
	public void animateToSkyPosition(double x, double y) {
		Rectangle2D newBounds = new Rectangle2D.Double(x, y, 0, 0);

		mySkyCamera.animateViewToCenterBounds(newBounds, false, 600);
	}

	/**
	 * Closes all windows which exist in this world
	 */
	public void closeAllWindows() {
		for (Window window : getAllWindows()) {
			window.close();
		}

	}

	/**
	 * @return A collection of all the windows in this world
	 */
	public Collection<Window> getAllWindows() {
		Vector<Window> windows = new Vector<Window>(10);

		PNodeFilter filter = new PNodeFilter() {

			public boolean accept(PNode node) {
				if (node instanceof Window)
					return true;
				else
					return false;

			}

			public boolean acceptChildrenOf(PNode node) {
				if (node instanceof Window) {
					return false;
				} else {
					return true;
				}
			}

		};

		getSky().getAllNodes(filter, windows);
		getGround().getAllNodes(filter, windows);

		return windows;
	}

	/**
	 * @return ground
	 */
	public WorldGround getGround() {
		return myGround;
	}

	@Override
	public PRoot getRoot() {
		/*
		 * This world's root is always to top-level root associated with the
		 * canvas
		 */
		return UIEnvironment.getInstance().getCanvas().getRoot();
	}

	/**
	 * Returns a copy of currently selected nodes
	 * 
	 * @return Selection Currently Selected nodes
	 */
	public Collection<WorldObject> getSelection() {
		return selectionEventHandler.getSelection();
	}

	/**
	 * @return sky
	 */
	public WorldSky getSky() {
		return mySkyCamera;
	}

	/*
	 * Returns true if the node exists in this world (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#isAncestorOf(edu.umd.cs.piccolo.PNode)
	 */
	@Override
	public boolean isAncestorOf(PNode node) {
		if (node == this)
			return true;

		if (getGround().isAncestorOf(node))
			return true;
		else
			return super.isAncestorOf(node);
	}

	/**
	 * @return if true, selection mode is enabled. if false, navigation mode is
	 *         enabled instead.
	 */
	public boolean isSelectionMode() {
		return isSelectionMode;
	}

	/**
	 * Minimizes all windows that exist in this world
	 */
	public void minimizeAllWindows() {
		for (Window window : getAllWindows()) {
			window.setWindowState(Window.WindowState.MINIMIZED);
		}
	}

	/*
	 * Set the bounds of the sky be the same as that of the world
	 * 
	 * @see edu.umd.cs.piccolo.PNode#setBounds(double, double, double, double)
	 */
	@Override
	public boolean setBounds(double x, double y, double w, double h) {
		mySkyCamera.setBounds(x, y, w, h);

		return super.setBounds(x, y, w, h);
	}

	/**
	 * @param enabled
	 *            True if selection mode is enabled, False if navigation
	 */
	public void setSelectionMode(boolean enabled) {
		if (isSelectionMode != enabled) {
			isSelectionMode = enabled;
			mySkyCamera.removeInputEventListener(selectionEventHandler);
			if (!isSelectionMode) {
				initSelectionMode();
			} else {
				mySkyCamera.removeInputEventListener(panHandler);
				mySkyCamera.addInputEventListener(selectionEventHandler);
			}

			layoutChildren();
		}
	}

	/**
	 * Set the scale at which to view the ground from the sky
	 * 
	 * @param scale
	 *            Scale at which to view the ground
	 */
	public void setSkyViewScale(float scale) {
		getSky().setViewScale(scale);

	}

	/**
	 * Set the status bar handler, there can be only one.
	 * 
	 * @param statusHandler
	 *            New Status bar handler
	 */
	public void setStatusBarHandler(AbstractStatusHandler statusHandler) {
		if (statusBarHandler != null) {
			getSky().removeInputEventListener(statusBarHandler);
		}

		statusBarHandler = statusHandler;

		if (statusBarHandler != null) {
			getSky().addInputEventListener(statusBarHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.handlers.Interactable#showContextMenu(edu.umd.cs.piccolo.event.PInputEvent)
	 */
	public JPopupMenu showContextMenu() {
		PopupMenuBuilder menu = new PopupMenuBuilder(getName());
		constructMenu(menu);

		return menu.toJPopupMenu();
	}

	/**
	 * @return Context menu for currently selected items, null is none is to be
	 *         shown
	 */
	public JPopupMenu showSelectionContextMenu() {
		return null;
	}

	/**
	 * @param objectSelected
	 *            Object to show the tooltip for
	 * @return Tooltip shown
	 */
	public TooltipWrapper showTooltip(WorldObject objectSelected) {

		TooltipWrapper tooltip = new TooltipWrapper(getSky(), objectSelected
				.getTooltip(), objectSelected);

		tooltip.fadeIn();

		return tooltip;

	}

	/**
	 * @param position
	 *            Position in sky
	 * @return Position on ground
	 */
	public Point2D skyToGround(Point2D position) {
		mySkyCamera.localToView(position);

		return position;
	}

	public PTransformActivity zoomToBounds(Rectangle2D bounds) {
		return zoomToBounds(bounds, 1000);
	}

	/**
	 * Animate the sky to look at a portion of the ground at bounds
	 * 
	 * @param bounds
	 *            Bounds to look at
	 * @return Reference to the activity which is animating the zoom and
	 *         positioning
	 */
	public PTransformActivity zoomToBounds(Rectangle2D bounds, long time) {
		PBounds biggerBounds = new PBounds(bounds.getX() - OBJECT_ZOOM_PADDING,
				bounds.getY() - OBJECT_ZOOM_PADDING, bounds.getWidth()
						+ OBJECT_ZOOM_PADDING * 2, bounds.getHeight()
						+ OBJECT_ZOOM_PADDING * 2);

		return getSky().animateViewToCenterBounds(biggerBounds, true, time);

	}

	/**
	 * Animate the sky to view all object on the ground
	 * 
	 * @return reference to animation activity
	 */
	public PTransformActivity zoomToFit() {
		return zoomToBounds(getGround().getUnionOfChildrenBounds(null));

	}

	/**
	 * @param object
	 *            Object to zoom to
	 * @return reference to animation activity
	 */
	public PTransformActivity zoomToObject(WorldObject object) {
		Rectangle2D bounds = object.getParent().localToGlobal(
				object.getFullBounds());

		return zoomToBounds(bounds);
	}

	/**
	 * Action to close all windows
	 * 
	 * @author Shu Wu
	 */
	class CloseAllWindows extends StandardAction {

		private static final long serialVersionUID = 1L;

		public CloseAllWindows(String actionName) {
			super("Close all windows", actionName);
		}

		@Override
		protected void action() throws ActionException {
			closeAllWindows();

		}

	}

	/**
	 * Action to minimize all windows
	 * 
	 * @author Shu Wu
	 */
	class MinimizeAllWindows extends StandardAction {

		private static final long serialVersionUID = 1L;

		public MinimizeAllWindows(String actionName) {
			super("Minimize all windows", actionName);
		}

		@Override
		protected void action() throws ActionException {
			minimizeAllWindows();

		}

	}

	/**
	 * Action to switch the selection mode
	 * 
	 * @author Shu Wu
	 */
	class SwitchSelectionModeHandler extends PBasicInputEventHandler {

		@Override
		public void keyTyped(PInputEvent event) {
			if (event.getKeyChar() == 's' || event.getKeyChar() == 'S') {
				UIEnvironment.getInstance()
						.setSelectionMode(!isSelectionMode());

			}

		}

	}
}
