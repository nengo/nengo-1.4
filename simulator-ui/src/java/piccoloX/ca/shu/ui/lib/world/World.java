package ca.shu.ui.lib.world;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPopupMenu;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.handlers.DragHandler;
import ca.shu.ui.lib.handlers.EventConsumer;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.handlers.MouseHandler;
import ca.shu.ui.lib.handlers.ScrollZoomHandler;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.handlers.TooltipHandler;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.objects.widgets.TooltipWrapper;
import ca.shu.ui.lib.util.Grid;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
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
 * 
 * @author Shu Wu
 * 
 */
public class World extends WorldObject implements Interactable,
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	static final double CLICK_ZOOM_PADDING = 100;

	static final float TOOLTIP_TRANSPARENCY = 0.5f;

	static boolean tooltipsVisible = true;

	public static boolean isTooltipsVisible() {
		return tooltipsVisible;
	}

	public static void setTooltipsVisible(boolean tooltipsVisible) {
		World.tooltipsVisible = tooltipsVisible;

	}

	private PInputEventListener dragHandler;

	private PInputEventListener eventConsumer;

	private PInputEventListener mouseHandler;

	private PPanEventHandler panHandler;

	private PInputEventListener scrollZoomHandler;

	private PInputEventListener tooltipHandler;

	private PZoomEventHandler zoomHandler;

	double cameraX = 0;

	double cameraY = 0;

	TooltipWrapper tooltipWrapper;

	PNode gridLayer = null;

	WorldGround ground;

	PLayer layer;

	WorldSky skyCamera;

	PBasicInputEventHandler statusBarHandler;

	public World(String name) {
		super(name);

		layer = new PLayer();

		UIEnvironment.getInstance().getRoot().addChild(layer);

		ground = new WorldGround(this);
		ground.setDraggable(false);
		layer.addChild(ground);

		skyCamera = new WorldSky(this);

		zoomHandler = new PZoomEventHandler();
		zoomHandler.setMinDragStartDistance(20);
		zoomHandler.setMinScale(0.02);
		zoomHandler.setMaxScale(4);

		panHandler = new PPanEventHandler();

		tooltipHandler = new TooltipHandler(this);
		dragHandler = new DragHandler();
		mouseHandler = new MouseHandler(this);
		scrollZoomHandler = new ScrollZoomHandler();
		eventConsumer = new EventConsumer();

		/*
		 * Add handlers
		 */
		addPropertyChangeListener(PNode.PROPERTY_BOUNDS, this);
		addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, this);

		skyCamera.addInputEventListener(zoomHandler);
		skyCamera.addInputEventListener(panHandler);

		skyCamera.addInputEventListener(tooltipHandler);
		skyCamera.addInputEventListener(dragHandler);
		skyCamera.addInputEventListener(mouseHandler);
		skyCamera.addInputEventListener(scrollZoomHandler);
		addInputEventListener(eventConsumer);
		setStatusBarHandler(new StatusBarHandler(this));

		skyCamera.setPaint(Style.COLOR_BACKGROUND);

		setCameraCenterPosition(0, 0);
		setWorldScale(0.7f);
		skyCamera.addLayer(layer);

		addChild(skyCamera);

		setDraggable(false);
		// PBoundsHandle.addBoundsHandlesTo(this);
		setBounds(0, 0, 800, 600);

		gridLayer = Grid.createGrid(getSky(), UIEnvironment.getInstance()
				.getRoot(), Style.COLOR_DARKBORDER, 1500);

		// System.out.println(this+"Finished
		// Constructing MiniWorld");
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

	@SuppressWarnings("unchecked")
	public void minimizeAllWindows() {

		Iterator<Window> itW = getAllWindows().iterator();
		while (itW.hasNext()) {
			itW.next().decreaseWindowSize();
		}

	}

	public boolean containsNode(PNode node) {
		if (getGround().isAncestorOf(node) || getSky().isAncestorOf(node)) {
			return true;
		}
		return false;
	}

	public WorldGround getGround() {
		return ground;
	}

	public double getGroundScale() {
		return getSky().getViewScale();
	}

	// public void createGrid() {
	//		
	//
	// }

	public double getScreenHeight() {
		return getHeight();
	}

	public double getScreenWidth() {
		return getWidth();
	}

	public WorldSky getSky() {
		return skyCamera;
	}

	public void hideTooltip() {
		if (tooltipWrapper == null) {
			return;
		}

		tooltipWrapper.fadeAndDestroy();
		tooltipWrapper = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.handlers.IContextMenu#isContextMenuEnabled()
	 */
	public boolean isContextMenuEnabled() {
		return true;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		getSky().setBounds(getBounds());
		getGround().setBounds(getBounds());
	}

	@Override
	public void removedFromWorld() {
		// TODO Auto-generated method stub
		super.removedFromWorld();

		gridLayer.removeFromParent();
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

	public void showTooltip(WorldObject tooltipObject, WorldObject follow) {

		hideTooltip();
		if (follow == null) {
			return;
		}

		tooltipWrapper = new TooltipWrapper(getSky(), tooltipObject, follow);

		tooltipWrapper.fadeIn();
		tooltipWrapper.updatePosition();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.World#showTransientMsg(java.lang.String,
	 *      ca.shu.ui.lib.world.impl.WorldObjectImpl)
	 */
	public void showTransientMsg(String msg, WorldObject attachTo) {

		TransientMsg msgObject = new TransientMsg(msg);

		double offsetX = -(msgObject.getWidth() - attachTo.getWidth()) / 2d;

		Point2D position = attachTo
				.objectToSky(new Point2D.Double(offsetX, -5));

		msgObject.setOffset(position);
		getSky().addChild(msgObject);
		msgObject.animate();

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
		return zoomToBounds(getGround().getFullBounds());

	}

	public PTransformActivity zoomToNode(WorldObject node) {
		Rectangle2D bounds = node.getParent().localToGlobal(
				node.getFullBounds());

		return zoomToBounds(bounds);
	}

	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = new PopupMenuBuilder(getName());

		menu.addAction(new ZoomOutAction());
		MenuBuilder windowsMenu = menu.createSubMenu("Windows");
		windowsMenu.addAction(new MinimizeAllWindows("Close all"));
		windowsMenu.addAction(new CloseAllWindows("Minmize all"));

		return menu;

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

	@Override
	protected void prepareForDestroy() {

		layer.removeFromParent();

		super.prepareForDestroy();
	}

}

class TransientMsg extends GText {

	private static final long serialVersionUID = 1L;
	static final int ANIMATE_MSG_DURATION = 2500;

	public TransientMsg(String text) {
		super(text);
		setFont(Style.FONT_BOLD);
		setTextPaint(Style.COLOR_NOTIFICATION);
		setConstrainWidthToTextWidth(true);
		setPickable(false);
		setChildrenPickable(false);

	}

	public void animate() {
		Point2D startingOffset = getOffset();
		animateToPositionScaleRotation(startingOffset.getX(), startingOffset
				.getY() - 50, 1, 0, ANIMATE_MSG_DURATION);

		PActivity fadeOutActivity = new Fader(this, ANIMATE_MSG_DURATION, 0f);
		addActivity(fadeOutActivity);

		PActivity removeActivity = new PActivity(0) {

			@Override
			protected void activityStarted() {
				TransientMsg.this.removeFromParent();
			}

		};
		addActivity(removeActivity);
		removeActivity.startAfter(fadeOutActivity);
	}
}

