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
import ca.shu.ui.lib.handlers.ContextMenuHandler;
import ca.shu.ui.lib.handlers.DragHandler;
import ca.shu.ui.lib.handlers.IContextMenu;
import ca.shu.ui.lib.handlers.MouseHandler;
import ca.shu.ui.lib.handlers.ScrollZoomHandler;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.handlers.TooltipHandler;
import ca.shu.ui.lib.util.Grid;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PBounds;

public class WorldImpl extends WorldObjectImpl implements World, IContextMenu,
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

	double cameraX = 0;

	double cameraY = 0;

	WorldObjectImpl controls;

	WorldObjectImpl controlsHolder;

	PNode gridLayer = null;

	WorldGround ground;

	PLayer layer;

	Point2D position;

	WorldSky skyCamera;

	PBasicInputEventHandler statusBarHandler;

	public WorldImpl(String titleStr, PRoot root) {
		super(titleStr);

		addPropertyChangeListener(PNode.PROPERTY_BOUNDS, this);
		addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, this);

		controlsHolder = new WorldObjectImpl();

		layer = new PLayer();

		if (root == null) {
			Util
					.Error("World can only be created when it is attached to a root");
			return;
		}
		root.addChild(layer);

		ground = new WorldGround(this);
		ground.setDraggable(false);
		layer.addChild(ground);

		skyCamera = new WorldSky(this);

		PZoomEventHandler zoomHandler = new PZoomEventHandler();
		zoomHandler.setMinDragStartDistance(20);
		zoomHandler.setMinScale(0.02);
		zoomHandler.setMaxScale(4);
		skyCamera.addInputEventListener(zoomHandler);

		PPanEventHandler panHandler = new PPanEventHandler();
		// panHandler.setAutopan(true);
		skyCamera.addInputEventListener(panHandler);

		skyCamera.addInputEventListener(new MouseHandler(this));
		skyCamera.addInputEventListener(new TooltipHandler(this));
		skyCamera.addInputEventListener(new DragHandler());
		skyCamera.addInputEventListener(new ContextMenuHandler(this));
		skyCamera.addInputEventListener(new ScrollZoomHandler());
		skyCamera.setPaint(Style.COLOR_BACKGROUND);
		skyCamera.addChild(controlsHolder);
		setCameraCenterPosition(0, 0);
		setWorldScale(0.7f);
		skyCamera.addLayer(layer);
		setStatusBarHandler(new StatusBarHandler(this));

		addChild(skyCamera);

		setDraggable(false);
		// PBoundsHandle.addBoundsHandlesTo(this);

		addInputEventListener(new PInputEventListener() {
			public void processEvent(PInputEvent aEvent, int type) {
				// System.out.println("Event handled 2");
				aEvent.setHandled(true);
			}
		});

		gridLayer = Grid.createGrid(getSky(), root, Style.COLOR_DARKBORDER,
				1500);

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

	public Point2D getPositionInGround(WorldObject wo) {
		WorldLayer layer = wo.getWorldLayer();
		Point2D position;

		position = wo.localToGlobal(new Point2D.Double(0, 0));

		if (layer instanceof WorldSky) {
			skyCamera.localToView(position);
			return position;
		} else if (layer instanceof WorldGround) {
			return position;
		}
		return null;

	}

	public Point2D getPositionInSky(WorldObjectImpl wo) {
		WorldLayer layer = wo.getWorldLayer();
		Point2D position;

		position = wo.localToGlobal(new Point2D.Double(0, 0));

		if (layer instanceof WorldGround) {
			skyCamera.viewToLocal(position);
			return position;
		} else if (layer instanceof WorldSky) {
			return position;
		}
		return null;

	}

	public double getScreenHeight() {
		return getHeight();
	}

	public double getScreenWidth() {
		return getWidth();
	}

	// public void createGrid() {
	//		
	//
	// }

	public WorldSky getSky() {
		return skyCamera;
	}

	public void hideControls() {
		if (controls == null) {
			return;
		}

		(new RemoveControlsThread(controls)).start();
		controls = null;
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

	public void showTooltip(WorldObjectImpl pControls,
			WorldObjectImpl nodeAttacedTo) {

		hideControls();
		if (nodeAttacedTo == null) {
			return;
		}
		PCamera camera = getSky();

		position = nodeAttacedTo.getOffset();
		if (camera.isAncestorOf(nodeAttacedTo)) {

			position = nodeAttacedTo.localToGlobal(new Point2D.Double(0,
					nodeAttacedTo.getHeight()));
		} else {

			position = nodeAttacedTo.getOffset();
			position = nodeAttacedTo.localToGlobal(new Point2D.Double(0,
					nodeAttacedTo.getHeight()));
			position = camera.viewToLocal(position);
		}
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

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (controls != null) {
					controlsHolder.bringToFront();
					controlsHolder.addChildFancy(controls);
					controlsHolder.setTransparency(0.5f);

					controls.setOffset(position);
				}
			}
		});

	}

	// public void showHelperMsg(String msg,
	// WorldObjectImpl nodeAttacedTo) {
	// hideControls();
	// if (nodeAttacedTo == null) {
	// return;
	// }
	// PCamera camera = getSky();
	//
	// position = nodeAttacedTo.getOffset();
	// if (camera.isAncestorOf(nodeAttacedTo)) {
	//
	// position = nodeAttacedTo.localToGlobal(new Point2D.Double(0,
	// nodeAttacedTo.getHeight()));
	// } else {
	//
	// position = nodeAttacedTo.getOffset();
	// position = nodeAttacedTo.localToGlobal(new Point2D.Double(0,
	// nodeAttacedTo.getHeight()));
	// position = camera.viewToLocal(position);
	// }
	// double x = position.getX();
	// double y = position.getY();
	//
	// this.controls = new WorldObjectImpl();
	//
	// // pControls.setDraggable(false);
	// controls.addToLayout(pControls);
	//
	// controls.pushState(WorldObject.State.SELECTED);
	//
	// if (x + controls.getWidth() > camera.getBounds().getWidth()) {
	// x = camera.getBounds().getWidth() - controls.getWidth();
	//
	// // leave some room at the top of the screen
	// if (x < 100) {
	// x = 100;
	// }
	// }
	// if (y + controls.getHeight() > camera.getBounds().getHeight()) {
	// y = camera.getBounds().getHeight() - controls.getHeight();
	// }
	//
	// position = new Point2D.Double(x, y);
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// if (controls != null) {
	// controlsHolder.bringToFront();
	// controlsHolder.addChildFancy(controls);
	// controlsHolder.setTransparency(0.5f);
	//
	// controls.setOffset(position);
	// }
	// }
	// });
	// }

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

	public PTransformActivity zoomToNode(WorldObject node) {
		Rectangle2D bounds = node.localToGlobal(node.getFullBounds());
		return zoomToBounds(bounds);
	}

	public PTransformActivity zoomToFit() {
		return zoomToBounds(getGround().getFullBounds());

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

	public JPopupMenu showPopupMenu(PInputEvent event) {
		return constructPopupMenu().getJPopupMenu();
	}

	protected PopupMenuBuilder constructPopupMenu() {
		PopupMenuBuilder menu = new PopupMenuBuilder(getName());

		menu.addAction(new ZoomOutAction());
		// menu.addSection("View");
		// menu.addAction(new MinimizeAction());

		return menu;

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
