package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.exceptions.ActionException;
import ca.shu.ui.lib.handlers.EventConsumer;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.handlers.KeyboardFocusHandler;
import ca.shu.ui.lib.handlers.MouseHandler;
import ca.shu.ui.lib.handlers.ScrollZoomHandler;
import ca.shu.ui.lib.handlers.SelectionHandler;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.handlers.TooltipHandler;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.objects.widgets.TooltipWrapper;
import ca.shu.ui.lib.util.Grid;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
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
public class World extends WorldObject implements Interactable,
		PropertyChangeListener {

	private static final double CLICK_ZOOM_PADDING = 100;

	private static final long serialVersionUID = 1L;

	private static boolean tooltipsVisible = true;

	public static boolean isTooltipsVisible() {
		return tooltipsVisible;
	}

	public static void setTooltipsVisible(boolean tooltipsVisible) {
		World.tooltipsVisible = tooltipsVisible;

	}

	// private final PInputEventListener dragHandler;

	private final PInputEventListener mouseHandler;

	private final PPanEventHandler panHandler;

	private final PInputEventListener scrollZoomHandler;

	private SelectionHandler selectionEventHandler;

	private PBasicInputEventHandler statusBarHandler;

	private final PInputEventListener tooltipHandler;

	private final PZoomEventHandler zoomHandler;

	private WorldGround ground;

	private PLayer layer;

	private boolean selectionModeEnabled;

	private WorldSky skyCamera;

	private TooltipWrapper tooltipWrapper;

	PLayer gridLayer;

	public World(String name) {
		super(name);

		layer = new PLayer();

		UIEnvironment.getInstance().getRoot().addChild(layer);

		ground = createGround();
		ground.setSelectable(false);
		layer.addChild(ground);

		skyCamera = new WorldSky(this);

		zoomHandler = new PZoomEventHandler();
		zoomHandler.setMinDragStartDistance(20);
		zoomHandler.setMinScale(0.02);
		zoomHandler.setMaxScale(4);

		panHandler = new PPanEventHandler();

		tooltipHandler = new TooltipHandler(this);
		// dragHandler = new DragHandler();
		mouseHandler = new MouseHandler(this);
		scrollZoomHandler = new ScrollZoomHandler();

		selectionEventHandler = new SelectionHandler(this);
		selectionEventHandler.setMarqueePaint(Style.COLOR_BORDER_SELECTED);
		selectionEventHandler
				.setMarqueeStrokePaint(Style.COLOR_BORDER_SELECTED);
		selectionEventHandler.setMarqueePaintTransparency(0.1f);

		/*
		 * Add handlers
		 */
		addPropertyChangeListener(PNode.PROPERTY_BOUNDS, this);
		addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, this);

		skyCamera.addInputEventListener(new SwitchSelectionModeHandler());
		// skyCamera.addInputEventListener(zoomHandler);
		// skyCamera.addInputEventListener(panHandler);

		skyCamera.addInputEventListener(new KeyboardFocusHandler());

		skyCamera.addInputEventListener(tooltipHandler);
		// skyCamera.addInputEventListener(dragHandler);
		skyCamera.addInputEventListener(mouseHandler);
		skyCamera.addInputEventListener(scrollZoomHandler);
		addInputEventListener(new EventConsumer());
		setStatusBarHandler(new StatusBarHandler(this));

		skyCamera.setPaint(Style.COLOR_BACKGROUND);

		setCameraCenterPosition(0, 0);
		setWorldScale(0.7f);
		skyCamera.addLayer(layer);

		addChild(skyCamera);
		setSelectable(false);

		setBounds(0, 0, 800, 600);

		gridLayer = Grid.createGrid(getSky(), UIEnvironment.getInstance()
				.getRoot(), Style.COLOR_DARKBORDER, 1500);

		UIEnvironment.getInstance().getCanvas().addWorld(this);

		initSelectionMode();

	}

	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = new PopupMenuBuilder(getName());

		menu.addAction(new ZoomOutAction());
		MenuBuilder windowsMenu = menu.createSubMenu("Windows");
		windowsMenu.addAction(new CloseAllWindows("Close all"));
		windowsMenu.addAction(new MinimizeAllWindows("Minmize all"));

		return menu;

	}

	protected WorldGround createGround() {
		return new WorldGround(this);

	}

	@Override
	protected void prepareForDestroy() {
		UIEnvironment.getInstance().getCanvas().removeWorld(this);

		gridLayer.removeFromParent();
		layer.removeFromParent();

		super.prepareForDestroy();
	}

	public void addWindow(Window window) {
		getSky().addChild(window);
	}

	@SuppressWarnings("unchecked")
	public void closeAllWindows() {
		Iterator<Window> itW = getAllWindows().iterator();
		while (itW.hasNext()) {
			itW.next().close();
		}

	}

	public boolean containsNode(PNode node) {
		if (getGround().isAncestorOf(node) || getSky().isAncestorOf(node)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
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

		// Iterator it = getSky().getChildrenIterator();

		// while (it.hasNext()) {
		// Object next = it.next();
		// if (next instanceof Window) {
		// windows.add((Window) next);
		//
		// }
		// }
		//
		// it = getGround().getChildrenIterator();
		// while (it.hasNext()) {
		// Object next = it.next();
		// if (next instanceof Window) {
		// windows.add((Window) next);
		//
		// }
		// }

		return windows;
	}

	public WorldGround getGround() {
		return ground;
	}

	public double getGroundScale() {
		return getSky().getViewScale();
	}

	public double getScreenHeight() {
		return getHeight();
	}

	// public void createGrid() {
	//		
	//
	// }

	public double getScreenWidth() {
		return getWidth();
	}

	public WorldSky getSky() {
		return skyCamera;
	}

	// public void hideTooltip() {
	// if (tooltipWrapper == null) {
	// return;
	// }
	//
	// tooltipWrapper.fadeAndDestroy();
	// tooltipWrapper = null;
	// }

	@Override
	public boolean isAncestorOf(PNode node) {
		if (getGround().isAncestorOf(node))
			return true;
		else
			return super.isAncestorOf(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.handlers.IContextMenu#isContextMenuEnabled()
	 */
	public boolean isContextMenuEnabled() {
		return true;
	}

	public boolean isSelectionMode() {
		return selectionModeEnabled;
	}

	@SuppressWarnings("unchecked")
	public void minimizeAllWindows() {

		Iterator<Window> itW = getAllWindows().iterator();
		while (itW.hasNext()) {
			itW.next().setWindowState(Window.WindowState.MINIMIZED);
		}

	}

	public void propertyChange(PropertyChangeEvent arg0) {
		getSky().setBounds(getBounds());
		getGround().setBounds(getBounds());
	}

	public void setBounds(int x, int y, final int w, final int h) {
		skyCamera.setBounds(skyCamera.getX(), skyCamera.getY(), w, h);
		super.setBounds(x, y, w, h);
	}

	public void setCameraCenterPosition(double x, double y) {
		// Point2D position = skyCamera.viewToLocal(new Point2D.Double(x, y));
		//
		// double xOffset = (getWidth() / 2);
		// double yOffset = (getHeight() / 2);

		Rectangle2D newBounds = new Rectangle2D.Double(x, y, 0, 0);

		skyCamera.animateViewToCenterBounds(newBounds, false, 600);
		// skyCamera.setViewOffset(position.getX() + xOffset, position.getY()
		// + yOffset);

	}

	private void initSelectionMode() {
		selectionModeEnabled = false;
		skyCamera.addInputEventListener(zoomHandler);
		skyCamera.addInputEventListener(panHandler);
		skyCamera.addInputEventListener(selectionEventHandler);
	}

	/**
	 * @param enabled
	 *            True if selection mode is enabled, False if navigation
	 */
	public void setSelectionMode(boolean enabled) {
		if (selectionModeEnabled != enabled) {
			selectionModeEnabled = enabled;
			skyCamera.removeInputEventListener(selectionEventHandler);
			if (!selectionModeEnabled) {

				initSelectionMode();
			} else {

				skyCamera.removeInputEventListener(zoomHandler);
				skyCamera.removeInputEventListener(panHandler);
				skyCamera.addInputEventListener(selectionEventHandler);
			}

			layoutChildren();
		}
	}

	public void setStatusBarHandler(StatusBarHandler statusHandler) {
		if (statusBarHandler != null) {
			getSky().removeInputEventListener(statusBarHandler);
		}

		statusBarHandler = statusHandler;

		if (statusBarHandler != null) {
			getSky().addInputEventListener(statusBarHandler);
		}
	}

	public void setWorldScale(float scale) {
		getSky().setViewScale(scale);

	}

	public JPopupMenu showContextMenu(PInputEvent event) {
		return constructMenu().getJPopupMenu();
	}

	public TooltipWrapper showTooltip(WorldObject follow) {

		tooltipWrapper = new TooltipWrapper(getSky(), follow.getTooltip(),
				follow);

		tooltipWrapper.fadeIn();
		tooltipWrapper.updatePosition();
		return tooltipWrapper;

	}

	public Point2D skyToGround(Point2D position) {
		skyCamera.localToView(position);

		return position;
	}

	public PTransformActivity zoomToBounds(Rectangle2D bounds) {
		PBounds biggerBounds = new PBounds(bounds.getX() - CLICK_ZOOM_PADDING,
				bounds.getY() - CLICK_ZOOM_PADDING, bounds.getWidth()
						+ CLICK_ZOOM_PADDING * 2, bounds.getHeight()
						+ CLICK_ZOOM_PADDING * 2);

		return getSky().animateViewToCenterBounds(biggerBounds, true, 1000);

	}

	public PTransformActivity zoomToFit() {
		return zoomToBounds(getGround().getUnionOfChildrenBounds(null));

	}

	public PTransformActivity zoomToNode(WorldObject node) {
		Rectangle2D bounds = node.getParent().localToGlobal(
				node.getFullBounds());

		return zoomToBounds(bounds);
	}

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

	class SwitchSelectionModeHandler extends PBasicInputEventHandler {

		@Override
		public void keyTyped(PInputEvent event) {
			if (event.getKeyChar() == 's' || event.getKeyChar() == 'S') {
				UIEnvironment.getInstance()
						.setSelectionMode(!isSelectionMode());

			}

		}

	}

	class ZoomOutAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ZoomOutAction() {
			super("Fit on screen");
		}

		@Override
		protected void action() throws ActionException {
			zoomToFit();
		}

	}
}
