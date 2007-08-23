package ca.shu.ui.lib.world;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Stack;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.LayoutManager;
import ca.shu.ui.lib.objects.widgets.TransientMsg;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PNodeFilter;

/*
 * TODO: Clean up class, move non-core functionality to child objects
 */
public class WorldObject extends PNode implements NamedObject {
	public static final String PROPERTY_EDGES = "edges";
	public static final String PROPERTY_STATE = "state";

	private static final long serialVersionUID = 1L;

	private PPath borderFrame = null;
	private PActivity currentActivity = null;

	private PPath frame = null;

	private boolean isDestroyed = false;

	private boolean isDraggable = true;

	private boolean isFrameVisible = false;

	private LayoutManager layoutManager;

	private String name;

	private State state = State.DEFAULT;

	private Stack<State> states;

	private TransformChangeListener transformChangeListener;

	public WorldObject() {
		this("");

	}

	public WorldObject(String name) {
		super();

		this.name = name;

		this.setFrameVisible(false);
		this.setDraggable(true);
		transformChangeListener = new TransformChangeListener();
		addPropertyChangeListener(PROPERTY_TRANSFORM, transformChangeListener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#addToLayout(edu.umd.cs.piccolo.PNode,
	 *      boolean)
	 */
	public void addToLayout(PNode node) {

		addChild(node);

		getLayoutManager().positionNode(node);

		PBounds bounds = getLayoutManager().getBounds();
		// if (bounds.height < )

		setBounds(bounds);

	}

	public void animateToBounds(Rectangle2D rect, long duration) {

		this.animateToBounds(rect.getX(), rect.getY(), rect.getWidth(), rect
				.getHeight(), duration);

	}

	public PTransformActivity animateToPosition(double x, double y,
			long duration) {

		return animateToPositionScaleRotation(x, y, 1, 0, duration);
	}

	@Override
	public PTransformActivity animateToPositionScaleRotation(double x,
			double y, double scale, double theta, long duration) {
		// TODO Auto-generated method stub
		PTransformActivity activity = super.animateToPositionScaleRotation(x,
				y, scale, theta, duration);

		if (currentActivity != null && currentActivity.isStepping()) {
			activity.startAfter(currentActivity);
		}

		currentActivity = activity;
		return activity;
	}

	public void animateToScale(double scale, long duration) {
		this.animateToPositionScaleRotation(this.getOffset().getX(), this
				.getOffset().getY(), scale, this.getRotation(), duration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.WorldObject#destroy()
	 */
	public final void destroy() {
		if (!isDestroyed) {
			isDestroyed = true;

			prepareForDestroy();
			/*
			 * Notify edges that this object has been destroyed
			 */
			signalEdgesChanged();

			/*
			 * Removes this object from the world and finish any tasks
			 * 
			 * Convert to array to allow for concurrent modification
			 */
			Object[] children = getChildrenReference().toArray();

			for (int i = 0; i < children.length; i++) {
				Object child = children[i];

				if (child instanceof WorldObject) {
					((WorldObject) child).destroy();
				}
			}
			removeFromParent();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.WorldObject#doubleClicked()
	 */
	public void doubleClicked() {
		if (getWorld() != null) {
			getWorld().zoomToNode(this);
		}
	}

	public void endDrag() {

	}

	/**
	 * Gets the children which intersect the rectangle bounds in the coordinate
	 * system of this object.
	 * 
	 * @param bounds
	 * @param classType
	 * @return Collection of Children
	 */
	@SuppressWarnings("unchecked")
	public Collection<PNode> getChildrenAtBounds(Rectangle2D bounds,
			Class classType) {
		return (Collection<PNode>) (this.getAllNodes(new BoundsFilter(this,
				this.localToGlobal(bounds), classType), null));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getLayoutManager()
	 */
	public LayoutManager getLayoutManager() {
		if (layoutManager == null)
			layoutManager = new LayoutManager();
		return layoutManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.NamedObject#getName()
	 */
	public String getName() {
		if (name == null) {
			return "";
		} else
			return name;
	}

	/**
	 * @return State of this WorldObject
	 */
	public State getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getControls()
	 */
	public WorldObject getTooltip() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#getWorld()
	 */
	public World getWorld() {
		if (getWorldLayer() != null)
			return getWorldLayer().getWorld();
		else
			return null;
	}

	public IWorldLayer getWorldLayer() {
		PNode node = this;

		while (node != null) {
			if (node instanceof IWorldLayer)
				return ((IWorldLayer) node);

			node = node.getParent();
		}

		return null;

	}

	/**
	 * @return Whether this Object has been destroyed (ie. ready for garbage
	 *         collection)
	 */
	public boolean isDestroyed() {
		return isDestroyed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.WorldObject#isDraggable()
	 */
	public boolean isDraggable() {
		return isDraggable;
	}

	/**
	 * @return Whether the Frame is visible
	 */
	public boolean isFrameVisible() {
		return isFrameVisible;
	}

	/**
	 * End of a drag and drop operation
	 */
	public void justDropped() {

	}

	public Point2D objectToGround(Point2D position) {
		IWorldLayer layer = getWorldLayer();

		localToGlobal(position);

		if (layer instanceof WorldSky) {
			layer.getWorld().getSky().localToView(position);
			return position;
		} else if (layer instanceof WorldGround) {
			return position;
		}
		return null;

	}

	public Rectangle2D objectToGround(Rectangle2D rectangle) {
		IWorldLayer layer = getWorldLayer();

		localToGlobal(rectangle);

		if (layer instanceof WorldSky) {
			layer.getWorld().getSky().localToView(rectangle);
			return rectangle;
		} else if (layer instanceof WorldGround) {
			return rectangle;
		}
		return null;

	}

	public Point2D objectToSky(Point2D position) {
		IWorldLayer layer = getWorldLayer();

		localToGlobal(position);

		if (layer instanceof WorldGround) {
			layer.getWorld().getSky().viewToLocal(position);
			return position;
		} else if (layer instanceof WorldSky) {
			return position;
		}
		return null;

	}

	public Rectangle2D objectToSky(Rectangle2D rectangle) {
		IWorldLayer layer = getWorldLayer();

		localToGlobal(rectangle);

		if (layer instanceof WorldGround) {
			layer.getWorld().getSky().viewToLocal(rectangle);
			return rectangle;
		} else if (layer instanceof WorldSky) {
			return rectangle;
		}
		return null;

	}

	public void popState(State state) {
		states.remove(state);
		if (states.size() == 0)
			setState(State.DEFAULT);
		else
			setState(states.peek());

	}

	public void popupTransientMsg(String msg) {

		TransientMsg msgObject = new TransientMsg(msg);

		double offsetX = -(msgObject.getWidth() - getWidth()) / 2d;

		Point2D position = objectToSky(new Point2D.Double(offsetX, -5));

		msgObject.setOffset(position);
		getWorld().getSky().addChild(msgObject);
		msgObject.animate();

	}

	public void pushState(State state) {
		if (states == null) {
			states = new Stack<State>();
		}
		states.push(state);

		setState(state);
	}

	public void setDraggable(boolean isDraggable) {
		this.isDraggable = isDraggable;
	}

	public void setFrameVisible(boolean isVisible) {
		isFrameVisible = isVisible;

		if (isVisible) {
			if (frame == null) {
				frame = PPath.createRectangle(0, 0, 100, 100);

				frame.setPaint(Style.COLOR_BACKGROUND);
				frame.setStrokePaint(Style.COLOR_FOREGROUND);

				// frame.setPickable(true);

				this.addChild(0, frame);
			}
		} else {
			if (frame != null) {
				frame.removeFromParent();
				frame = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.sw.graphics.nodes.WorldO#setLayoutManager(ca.sw.graphics.basics.LayoutManager)
	 */
	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	public void setName(String name) {
		this.name = name;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setParent(PNode newParent) {
		boolean worldChanged = false;

		World newWorld = null;

		if (newParent instanceof WorldObject) {
			newWorld = ((WorldObject) newParent).getWorld();
		}

		if (newWorld != getWorld()) {
			worldChanged = true;
		}

		super.setParent(newParent);

		if (worldChanged) {
			// World has disappeared
			if (newWorld == null) {

				removedFromWorld();

			}
			// Is in a new world
			else {
				addedToWorld();

				ListIterator it = getChildrenIterator();

				while (it.hasNext()) {
					PNode node = (PNode) it.next();

					if (node instanceof WorldObject) {
						((WorldObject) node).addedToWorld();
					}
				}
			}
		}
		signalEdgesChanged();
	}

	@Override
	public void signalBoundsChanged() {
		// TODO Auto-generated method stub
		super.signalBoundsChanged();
		signalEdgesChanged();

	}

	/**
	 * Signal to the attached edges that this node's position or transform in
	 * the World has changed
	 */
	public void signalEdgesChanged() {

		firePropertyChange(0, PROPERTY_EDGES, null, null);

		/*
		 * Updates children edges
		 */
		int count = getChildrenCount();
		for (int i = 0; i < count; i++) {
			PNode each = (PNode) getChildrenReference().get(i);

			if (each instanceof WorldObject) {
				WorldObject wo = (WorldObject) each;

				wo.signalEdgesChanged();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.WorldObject#startDrag()
	 */
	public void startDrag() {

	}

	private void setState(State newState) {
		State oldState = state;
		state = newState;

		firePropertyChange(0, PROPERTY_STATE, oldState, newState);

		stateChanged();
	}

	/**
	 * Called when the World the object lives in has changed
	 */
	protected void addedToWorld() {
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		if (frame != null) {
			frame.setBounds(getBounds());
		}

		if (borderFrame != null) {
			borderFrame.setBounds(getBounds());
		}
	}

	/**
	 * Perform any operations before being destroyed
	 */
	protected void prepareForDestroy() {

	}

	/**
	 * Called when this object is removed from the World
	 */
	protected void removedFromWorld() {

	}

	protected void setBorder(Color borderColor) {
		if (borderColor == null) {
			if (borderFrame != null)
				borderFrame.removeFromParent();
			borderFrame = null;
			return;
		}

		if (borderFrame == null) {

			borderFrame = PPath.createRectangle((float) getX(), (float) getY(),
					(float) getWidth(), (float) getHeight());

			synchronized (borderFrame) {
				borderFrame.setPaint(null);

				addChild(borderFrame);
			}
		}

		borderFrame.setStrokePaint(borderColor);

	}

	// /**
	// * Moves nodes which overlap
	// *
	// */
	// protected void moveOverlappedNodes(WorldObjectImpl callingNode) {
	// if (!isTangible())
	// return;
	//
	// Rectangle2D dBounds = localToGlobal(getBounds());
	//
	// /*
	// * Move nodes which are close to it
	// *
	// */
	//
	// WorldLayer world = getWorldLayer();
	// if (world == null)
	// return;
	//
	// world.globalToLocal(dBounds);
	//
	// Collection<WorldObject> intersectingNodes = world
	// .getChildrenAtBounds(dBounds);
	//
	// // find intersecting nodes
	// Iterator<WorldObject> it = intersectingNodes.iterator();
	// while (it.hasNext()) {
	// WorldObject node = it.next();
	//
	// if (node != this && node != callingNode) {
	// WorldObjectImpl gNode = (WorldObjectImpl) node;
	//
	// if (gNode.isDraggable() && gNode.isTangible()) {
	// Rectangle2D bounds = gNode.localToGlobal(gNode.getBounds());
	//
	// double translateX = Double.MAX_VALUE;
	//
	// double translateY = 0;
	//
	// translateX = dBounds.getMaxX() - bounds.getMinX();
	// double trX = dBounds.getMinX() - bounds.getMaxX();
	// if (Math.abs(trX) < Math.abs(translateX))
	// translateX = trX;
	//
	// translateY = dBounds.getMaxY() - bounds.getMinY();
	// double trY = dBounds.getMinY() - bounds.getMaxY();
	// if (Math.abs(trY) < Math.abs(translateY))
	// translateY = trY;
	//
	// if (Math.abs(translateX) < Math.abs(translateY)) {
	// gNode.translate(translateX, 0);
	// } else
	// gNode.translate(0, translateY);
	//
	// gNode.moveOverlappedNodes(this);
	// }
	//
	// }
	//
	// }
	//
	// }

	/**
	 * Called when the Object's state has changed
	 */
	protected void stateChanged() {

		if (state == State.DEFAULT) {
			setBorder(null);
		} else if (state == State.IN_DRAG) {
			setBorder(Style.COLOR_BORDER_DRAGGED);
		} else if (state == State.SELECTED) {
			setBorder(Style.COLOR_SELECTED);
		}
	}

	public static enum State {
		DEFAULT, IN_DRAG, SELECTED
	}

	class TransformChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			signalEdgesChanged();

		}

	}

}

class BoundsFilter implements PNodeFilter {
	Rectangle2D bounds;

	@SuppressWarnings("unchecked")
	Class classType;

	PBounds localBounds = new PBounds();

	PNode node;

	@SuppressWarnings("unchecked")
	protected BoundsFilter(PNode node, Rectangle2D bounds, Class classType) {
		this.bounds = bounds;
		this.node = node;
		this.classType = classType;

	}

	public boolean accept(PNode node) {
		if (this.node == node) // rejects the parent node... we only want the
			// children
			return false;

		if (!classType.isInstance(node) || node instanceof IWorldLayer)
			return false;

		localBounds.setRect(bounds);
		node.globalToLocal(localBounds);

		boolean boundsIntersects = node.intersects(localBounds);

		return boundsIntersects;
	}

	public boolean acceptChildrenOf(PNode node) {
		if (this.node == node) {
			return true;
		} else {
			return false;
		}
	}

}