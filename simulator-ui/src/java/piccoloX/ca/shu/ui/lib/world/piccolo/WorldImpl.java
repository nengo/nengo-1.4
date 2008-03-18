package ca.shu.ui.lib.world.piccolo;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.RemoveObjectsAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.ZoomToFitAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.handlers.AbstractStatusHandler;
import ca.shu.ui.lib.world.handlers.EventConsumer;
import ca.shu.ui.lib.world.handlers.KeyboardHandler;
import ca.shu.ui.lib.world.handlers.MouseHandler;
import ca.shu.ui.lib.world.handlers.SelectionHandler;
import ca.shu.ui.lib.world.handlers.TooltipPickHandler;
import ca.shu.ui.lib.world.handlers.TopWorldStatusHandler;
import ca.shu.ui.lib.world.piccolo.objects.TooltipWrapper;
import ca.shu.ui.lib.world.piccolo.objects.Window;
import ca.shu.ui.lib.world.piccolo.primitives.PXGrid;
import ca.shu.ui.lib.world.piccolo.primitives.PXLayer;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Implementation of World. World holds World Objects and has navigation and
 * interaction handlers.
 * 
 * @author Shu Wu
 */
public class WorldImpl extends WorldObjectImpl implements World, Interactable {

	/**
	 * Padding to use around objects when zooming in on them
	 */
	private static final double OBJECT_ZOOM_PADDING = 100;
	private static final long serialVersionUID = 1L;

	/**
	 * Whether tooltips are enabled
	 */
	private static boolean tooltipsEnabled = true;

	public static boolean isTooltipsVisible() {
		return tooltipsEnabled;
	}

	public static void setTooltipsVisible(boolean tooltipsVisible) {
		WorldImpl.tooltipsEnabled = tooltipsVisible;
	}

	/**
	 * Layer attached to the camera which shows the zoomable grid
	 */
	private PXLayer gridLayer;

	/**
	 * If true, then selection mode. If false, then navigation mode.
	 */
	private boolean isSelectionMode;

	private KeyboardHandler keyboardHandler;

	/**
	 * PLayer which holds the ground layer
	 */
	private PXLayer layer;

	/**
	 * Ground which can be zoomed and navigated
	 */
	private WorldGroundImpl myGround;

	/**
	 * Sky, which looks at the ground and whose position and scale remains
	 * static
	 */
	private WorldSkyImpl mySky;

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
	 * @param name
	 * @param ground
	 */
	public WorldImpl(String name, WorldGroundImpl ground) {
		this(name, new WorldSkyImpl(), ground);
	}

	/**
	 * Default constructor
	 * 
	 * @param name
	 *            Name of this world
	 */
	public WorldImpl(String name, WorldSkyImpl sky, WorldGroundImpl ground) {
		super(name);

		/*
		 * Create layer
		 */
		layer = new PXLayer();
		getPRoot().addChild(layer);

		/*
		 * Create ground
		 */
		ground.setWorld(this);
		myGround = ground;
		layer.addChild(myGround.getPiccolo());

		/*
		 * Create sky
		 */
		mySky = sky;
		mySky.addLayer(layer);
		addChild(mySky);

		/*
		 * Create handlers
		 */
		panHandler = new PPanEventHandler();
		keyboardHandler = new KeyboardHandler(this);
		mySky.getCamera().addInputEventListener(keyboardHandler);
		mySky.getCamera().addInputEventListener(new TooltipPickHandler(this, 1000, 0));
		mySky.getCamera().addInputEventListener(new MouseHandler(this));

		selectionEventHandler = new SelectionHandler(this);
		selectionEventHandler.setMarqueePaint(Style.COLOR_BORDER_SELECTED);
		selectionEventHandler.setMarqueeStrokePaint(Style.COLOR_BORDER_SELECTED);
		selectionEventHandler.setMarqueePaintTransparency(0.1f);

		getPiccolo().addInputEventListener(new EventConsumer());
		setStatusBarHandler(new TopWorldStatusHandler(this));

		/*
		 * Set position and scale
		 */
		// animateToSkyPosition(0, 0);
		getSky().setViewScale(0.7f);

		/*
		 * Create the grid
		 */
		gridLayer = PXGrid.createGrid(getSky().getCamera(), UIEnvironment.getInstance()
				.getUniverse().getRoot(), Style.COLOR_DARKBORDER, 1500);

		/*
		 * Let the top canvas have a handle on this world
		 */
		UIEnvironment.getInstance().getUniverse().addWorld(this);

		/*
		 * Miscellaneous
		 */
		setBounds(0, 0, 800, 600);

		initSelectionMode();

	}

	private PRoot getPRoot() {
		/*
		 * This world's root is always to top-level root associated with the
		 * canvas
		 */
		return UIEnvironment.getInstance().getUniverse().getRoot();
	}

	private void initSelectionMode() {
		isSelectionMode = false;
		mySky.getCamera().addInputEventListener(panHandler);
		mySky.getCamera().addInputEventListener(selectionEventHandler);
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

	protected void constructSelectionMenu(Collection<WorldObject> selection, PopupMenuBuilder menu) {
		menu.addAction(new RemoveObjectsAction(selection, "Remove"));

	}

	@Override
	protected void prepareForDestroy() {
		UIEnvironment.getInstance().getUniverse().removeWorld(this);

		keyboardHandler.destroy();
		gridLayer.removeFromParent();
		layer.removeFromParent();
		
		getGround().destroy();
		getSky().destroy();

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

		mySky.animateViewToCenterBounds(newBounds, false, 600);
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
		Collection<Window> skyWindows = getSky().getWindows();
		Collection<Window> groundWindows = getGround().getWindows();

		ArrayList<Window> allWindows = new ArrayList<Window>(skyWindows.size()
				+ groundWindows.size());

		for (Window window : skyWindows) {
			allWindows.add(window);
		}
		for (Window window : groundWindows) {
			allWindows.add(window);
		}

		return allWindows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.handlers.Interactable#showContextMenu(edu.umd.cs.piccolo.event.PInputEvent)
	 */
	public JPopupMenu getContextMenu() {
		PopupMenuBuilder menu = new PopupMenuBuilder(getName());
		constructMenu(menu);

		return menu.toJPopupMenu();
	}

	/**
	 * @return ground
	 */
	public WorldGroundImpl getGround() {
		return myGround;
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
	 * @return Context menu for currently selected items, null is none is to be
	 *         shown
	 */
	public JPopupMenu getSelectionMenu(Collection<WorldObject> selection) {

		if (selection.size() > 1) {

			PopupMenuBuilder menu = new PopupMenuBuilder(selection.size() + " Objects selected");

			constructSelectionMenu(selection, menu);

			return menu.toJPopupMenu();
		} else {
			return null;
		}

	}

	/**
	 * @return sky
	 */
	public WorldSkyImpl getSky() {
		return mySky;
	}

	public boolean isAncestorOf(WorldObject wo) {
		if (wo == this)
			return true;

		if (getGround().isAncestorOf(wo)) {
			return true;
		} else {
			return super.isAncestorOf(wo);
		}
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

	public boolean setBounds(double x, double y, double w, double h) {
		mySky.setBounds(x, y, w, h);
		return super.setBounds(x, y, w, h);
	}

	/**
	 * @param enabled
	 *            True if selection mode is enabled, False if navigation
	 */
	public void setSelectionMode(boolean enabled) {
		if (isSelectionMode != enabled) {
			isSelectionMode = enabled;
			mySky.getCamera().removeInputEventListener(selectionEventHandler);
			if (!isSelectionMode) {
				initSelectionMode();
			} else {
				mySky.getCamera().removeInputEventListener(panHandler);
				mySky.getCamera().addInputEventListener(selectionEventHandler);
			}

			// layoutChildren();
		}
	}

	/**
	 * Set the status bar handler, there can be only one.
	 * 
	 * @param statusHandler
	 *            New Status bar handler
	 */
	public void setStatusBarHandler(AbstractStatusHandler statusHandler) {
		if (statusBarHandler != null) {
			getSky().getCamera().removeInputEventListener(statusBarHandler);
		}

		statusBarHandler = statusHandler;

		if (statusBarHandler != null) {
			getSky().getCamera().addInputEventListener(statusBarHandler);
		}
	}

	/**
	 * @param objectSelected
	 *            Object to show the tooltip for
	 * @return Tooltip shown
	 */
	public TooltipWrapper showTooltip(WorldObject objectSelected) {

		TooltipWrapper tooltip = new TooltipWrapper(getSky(), objectSelected.getTooltip(),
				objectSelected);

		tooltip.fadeIn();

		return tooltip;

	}

	/**
	 * @param position
	 *            Position in sky
	 * @return Position on ground
	 */
	public Point2D skyToGround(Point2D position) {
		mySky.localToView(position);

		return position;
	}

	public void zoomToBounds(Rectangle2D bounds) {
		zoomToBounds(bounds, 1000);
	}

	/**
	 * Animate the sky to look at a portion of the ground at bounds
	 * 
	 * @param bounds
	 *            Bounds to look at
	 * @return Reference to the activity which is animating the zoom and
	 *         positioning
	 */
	public void zoomToBounds(Rectangle2D bounds, long time) {
		PBounds biggerBounds = new PBounds(bounds.getX() - OBJECT_ZOOM_PADDING, bounds.getY()
				- OBJECT_ZOOM_PADDING, bounds.getWidth() + OBJECT_ZOOM_PADDING * 2, bounds
				.getHeight()
				+ OBJECT_ZOOM_PADDING * 2);

		getSky().animateViewToCenterBounds(biggerBounds, true, time);

	}

	public void zoomToFit() {
		zoomToBounds(getGround().getPiccolo().getUnionOfChildrenBounds(null));

	}

	/**
	 * @param object
	 *            Object to zoom to
	 * @return reference to animation activity
	 */
	public void zoomToObject(WorldObject object) {
		Rectangle2D bounds = object.getParent().localToGlobal(object.getFullBounds());

		zoomToBounds(bounds);
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

}
