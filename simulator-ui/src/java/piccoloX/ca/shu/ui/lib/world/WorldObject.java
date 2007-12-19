package ca.shu.ui.lib.world;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.objects.activities.TransientMessage;
import ca.shu.ui.lib.util.Util;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * World objects are visible UI objects which exist in a World layer (Ground or
 * Sky).
 * 
 * @author Shu Wu
 */
public class WorldObject extends PNode implements INamedObject, IDestroyable {
	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_DESTROYED = "destroyed";

	/**
	 * The property name that identifies a change in this object's global
	 * position
	 */
	public static final String PROPERTY_GLOBAL_BOUNDS = "globalBounds";

	public static final long TIME_BETWEEN_POPUPS = 1500;

	/**
	 * Whether this object has been destroyed
	 */
	private boolean isDestroyed = false;

	/**
	 * Whether this object is selectable by the Selection handler
	 */
	private boolean isSelectable = true;

	private long lastPopupTime = 0;

	/**
	 * This object's name
	 */
	private String myName;

	/**
	 * Creates an unnamed WorldObject
	 */
	public WorldObject() {
		super();
		init("");

	}

	/**
	 * Creates a named WorldObject
	 * 
	 * @param name
	 *            Name of this object
	 */
	public WorldObject(String name) {
		super();
		init(name);

	}

	/**
	 * Initializes this instance
	 * 
	 * @param name
	 *            Name of this Object
	 */
	private void init(String name) {
		this.myName = name;

		// this.setFrameVisible(false);
		this.setSelectable(true);

		addPropertyChangeListener(PROPERTY_TRANSFORM,
				new TransformChangeListener());
	}

	/**
	 * Perform any operations before being destroyed
	 */
	protected void prepareForDestroy() {

	}

	/**
	 * @param scale
	 *            Scale to animate to
	 * @param duration
	 *            Duration of animation
	 */
	public void animateToScale(double scale, long duration) {
		this.animateToPositionScaleRotation(this.getOffset().getX(), this
				.getOffset().getY(), scale, this.getRotation(), duration);
	}

	/*
	 * Modification to PNode's animateToTransform. This animation is sequenced
	 * so that the previous transform animation finishes first (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#animateToTransform(java.awt.geom.AffineTransform,
	 *      long)
	 */
	@Override
	public PTransformActivity animateToTransform(AffineTransform destTransform,
			long duration) {
		if (duration == 0) {
			setTransform(destTransform);
			return null;
		} else {
			PTransformActivity.Target t = new PTransformActivity.Target() {
				public void getSourceMatrix(double[] aSource) {
					WorldObject.this.getTransformReference(true).getMatrix(
							aSource);
				}

				public void setTransform(AffineTransform aTransform) {
					WorldObject.this.setTransform(aTransform);
				}
			};

			PTransformActivity ta = new PTransformActivity(duration,
					PUtil.DEFAULT_ACTIVITY_STEP_RATE, t, destTransform);

			/*
			 * Sequences the animation to occur after
			 */
			if (busyAnimatingUntilTime > System.currentTimeMillis()) {
				ta.setStartTime(busyAnimatingUntilTime);
			} else {
				busyAnimatingUntilTime = System.currentTimeMillis();
			}
			busyAnimatingUntilTime += ta.getDuration();

			addActivity(ta);
			return ta;
		}
	}

	private long busyAnimatingUntilTime = 0;

	public boolean isAnimating() {
		if (busyAnimatingUntilTime < System.currentTimeMillis()) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.IDestroyable#destroy()
	 */
	public final void destroy() {
		if (!isDestroyed) {
			isDestroyed = true;

			prepareForDestroy();

			/*
			 * Notify edges that this object has been destroyed
			 */
			signalGlobalBoundsChanged();

			/*
			 * Removes this object from the world and finish any tasks Convert
			 * to array to allow for concurrent modification
			 */
			Object[] children = getChildrenReference().toArray();

			for (Object child : children) {
				if (child instanceof WorldObject) {
					((WorldObject) child).destroy();
				}
			}

			firePropertyChange(0, PROPERTY_DESTROYED, null, null);
			removeFromParent();
		}
	}

	/**
	 * Called if this object is double clicked on
	 */
	public void doubleClicked() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.NamedObject#getName()
	 */
	public String getName() {
		return myName;
	}

	/**
	 * @return Tooltip object, null if there is none
	 */
	public WorldObject getTooltip() {
		return null;
	}

	/**
	 * @return World which is an ancestor
	 */
	public World getWorld() {
		if (getWorldLayer() != null)
			return getWorldLayer().getWorld();
		else
			return null;
	}

	/**
	 * @return World layer which is an ancestor
	 */
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
	 * @return Whether this Object has been destroyed
	 */
	public boolean isDestroyed() {
		return isDestroyed;
	}

	/**
	 * @return Whether this object is selectable by a Selection Handler
	 */
	public boolean isSelectable() {
		return isSelectable;
	}

	/**
	 * @param position
	 *            Position relative to object
	 * @return Position relative to World's ground layer
	 */
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

	/**
	 * @param rectangle
	 *            relative to object
	 * @return Relative to World's ground layer
	 */
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

	/**
	 * @param position
	 *            relative to object
	 * @return relative to World's sky layer
	 */
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

	/**
	 * @param rectangle
	 *            relative to object
	 * @return relative to World's sky layer
	 */
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

	/**
	 * @param name
	 *            New name for this object
	 */
	public void setName(String name) {
		this.myName = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setParent(PNode newParent) {
		PNode oldParent = getParent();
		super.setParent(newParent);

		if (newParent != oldParent) {

			if (newParent != null)
				signalGlobalBoundsChanged();
		}
	}

	public void setSelected(boolean isSelected) {

	}

	/**
	 * @param isSelectable
	 *            Whether this object is selectable by a Selection handler
	 */
	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		signalGlobalBoundsChanged();
	}

	/**
	 * Show a transient message which appears over the object. The message is
	 * added to the world's sky layer.
	 * 
	 * @param msg
	 */
	public synchronized void showPopupMessage(String msg) {
		if (getWorld() != null) {

			Util.debugMsg("UI Popup Msg: " + msg);

			TransientMessage msgObject = new TransientMessage(msg);

			double offsetX = -(msgObject.getWidth() - getWidth()) / 2d;

			Point2D position = objectToSky(new Point2D.Double(offsetX, -5));

			msgObject.setOffset(position);
			getWorld().getSky().addChild(msgObject);

			long currentTime = System.currentTimeMillis();
			long delay = TIME_BETWEEN_POPUPS - (currentTime - lastPopupTime);

			if (delay < 0) {
				delay = 0;
			}

			msgObject.popup(delay);

			lastPopupTime = currentTime + delay;
		}
	}

	@Override
	public void signalBoundsChanged() {
		super.signalBoundsChanged();
		signalGlobalBoundsChanged();
	}

	/**
	 * Signal to the attached edges that this node's position or transform in
	 * the World has changed
	 */
	public void signalGlobalBoundsChanged() {

		firePropertyChange(0, PROPERTY_GLOBAL_BOUNDS, null, null);

		/*
		 * Updates children edges
		 */
		for (Object each : getChildrenReference()) {
			if (each instanceof WorldObject) {
				WorldObject wo = (WorldObject) each;

				wo.signalGlobalBoundsChanged();
			}
		}

	}

	/**
	 * Listens for transform changes, and signals that the global bounds for
	 * this object have changed
	 * 
	 * @author Shu Wu
	 */
	class TransformChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			signalGlobalBoundsChanged();

		}

	}

}