package ca.shu.ui.lib.world.impl;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

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
import ca.shu.ui.lib.util.Grid;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldObject;
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

/**
 * Implementation of World. World holds World Objects and has navigation and
 * interaction handlers.
 * 
 * 
 * @author Shu Wu
 * 
 */
public class WorldImpl extends WorldObjectImpl implements World, Interactable,
		PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	static final double CLICK_ZOOM_PADDING = 100;

	static boolean contexualTipsVisible = false;

	public static boolean isContexualTipsVisible() {
		return contexualTipsVisible;
	}

	public static void setContexualTipsVisible(boolean contexualTipsVisible) {
		WorldImpl.contexualTipsVisible = contexualTipsVisible;

	}

	private PInputEventListener mouseHandler;

	private PInputEventListener dragHandler;

	private PInputEventListener eventConsumer;

	private PPanEventHandler panHandler;

	private PInputEventListener scrollZoomHandler;

	private PInputEventListener tooltipHandler;

	private PZoomEventHandler zoomHandler;

	double cameraX = 0;

	double cameraY = 0;

	WorldObjectImpl controls;

	WorldObjectImpl widgetLayer;

	PNode gridLayer = null;

	WorldGround ground;

	PLayer layer;

	WorldSky skyCamera;

	PBasicInputEventHandler statusBarHandler;

	public WorldImpl(String name) {
		super(name);

		addPropertyChangeListener(PNode.PROPERTY_BOUNDS, this);
		addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, this);

		widgetLayer = new WorldObjectImpl();

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
		skyCamera.addInputEventListener(zoomHandler);
		skyCamera.addInputEventListener(panHandler);

		skyCamera.addInputEventListener(tooltipHandler);
		skyCamera.addInputEventListener(dragHandler);
		skyCamera.addInputEventListener(mouseHandler);
		skyCamera.addInputEventListener(scrollZoomHandler);
		addInputEventListener(eventConsumer);
		setStatusBarHandler(new StatusBarHandler(this));

		skyCamera.setPaint(Style.COLOR_BACKGROUND);
		skyCamera.addChild(widgetLayer);
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

	public void hideControls() {
		if (controls == null) {
			return;
		}

		/**
		 * Remove the controls with an animation
		 */
		(new RemoveControlsThread(controls)).start();
		controls = null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.World#showTransientMsg(java.lang.String,
	 *      ca.shu.ui.lib.world.impl.WorldObjectImpl)
	 */
	public void showTransientMsg(String msg, WorldObjectImpl attachTo) {

		GText msgObject = new GText(msg);
		msgObject.setFont(Style.FONT_BOLD);
		msgObject.setTextPaint(Style.COLOR_NOTIFICATION);
		msgObject.setConstrainWidthToTextWidth(true);

		double offsetX = -(msgObject.getWidth() - attachTo.getWidth()) / 2d;

		Point2D position = attachTo
				.objectToSky(new Point2D.Double(offsetX, -5));

		msgObject.setOffset(position);
		getSky().addChild(msgObject);

		(new AnimateMsgObjectThread(msgObject)).start();

	}

	public void showTooltip(WorldObjectImpl pControls, WorldObjectImpl attachTo) {

		hideControls();
		if (attachTo == null) {
			return;
		}
		PCamera camera = getSky();

		Point2D position = attachTo.objectToSky(new Point2D.Double(0, attachTo
				.getHeight()));

		double x = position.getX();
		double y = position.getY();

		this.controls = new WorldObjectImpl();

		pControls.setDraggable(false);
		controls.addToLayout(pControls);

		controls.pushState(WorldObject.State.SELECTED);

		if (x + controls.getWidth() > camera.getBounds().getWidth()) {
			x = camera.getBounds().getWidth() - controls.getWidth();

			// leave some room at the top of the screen
			if (x < 100) {
				x = 100;
			}
		}
		if (y + controls.getHeight() > camera.getBounds().getHeight()) {
			y = camera.getBounds().getHeight() - controls.getHeight();
		}

		position = new Point2D.Double(x, y);

		widgetLayer.moveToFront();
		widgetLayer.addChildFancy(controls);
		widgetLayer.setTransparency(0.5f);

		controls.setOffset(position);

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
		// menu.addSection("View");
		// menu.addAction(new MinimizeAction());

		return menu;

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

class CameraPropertyChangeListener implements PropertyChangeListener {
	PCamera camera;

	PLayer gridLayer;

	public CameraPropertyChangeListener(PCamera camera, PLayer gridLayer) {
		super();
		this.camera = camera;
		this.gridLayer = gridLayer;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		gridLayer.setBounds(camera.getViewBounds());
	}
}

class RemoveControlsThread extends Thread {
	WorldObjectImpl ctrlToRemove;

	public RemoveControlsThread(WorldObjectImpl controls) {
		super();
		this.ctrlToRemove = controls;
	}

	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ctrlToRemove.addActivity(new Fader(ctrlToRemove, 200, false));
			}
		});

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ctrlToRemove.removeFromParent();
			}
		});
	}

}

class AnimateMsgObjectThread extends Thread {
	PNode msgObject;
	static final int ANIMATE_MSG_DURATION = 2500;

	public AnimateMsgObjectThread(PNode msgObject) {
		super();
		this.msgObject = msgObject;
	}

	public void run() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Point2D startingOffset = msgObject.getOffset();
				msgObject
						.animateToPositionScaleRotation(startingOffset.getX(),
								startingOffset.getY() - 50, 1, 0,
								ANIMATE_MSG_DURATION);
			}
		});
		try {
			Thread.sleep(ANIMATE_MSG_DURATION);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		msgObject.removeFromParent();
	}
}