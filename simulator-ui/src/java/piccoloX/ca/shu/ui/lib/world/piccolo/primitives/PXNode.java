package ca.shu.ui.lib.world.piccolo.primitives;

import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.world.PaintContext;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * @author Shu Wu
 */
public class PXNode extends PNode implements PiccoloNodeInWorld {
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY_REMOVED_FROM_WORLD = "destroyed";
	/**
	 * The property name that identifies a change in this object's global
	 * position
	 */
	public static final String PROPERTY_GLOBAL_BOUNDS = "globalBounds";

	public static final String PROPERTY_PARENT_BOUNDS = "parentBounds";

	private long busyAnimatingUntilTime = 0;
	private WorldObject worldObjectParent;

	public PXNode() {
		super();

		addPropertyChangeListener(PNode.PROPERTY_TRANSFORM, new TransformChangeListener());
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

	@Override
	protected void paint(PPaintContext paintContext) {
		super.paint(paintContext);
		if (worldObjectParent != null) {
			PaintContext convertedPaintContext = new PaintContext(paintContext.getGraphics(),
					paintContext.getScale());

			worldObjectParent.paint(convertedPaintContext);
		}
	}

	@Override
	protected final void layoutChildren() {
		/*
		 * Delegate layout out children to the WorldObject
		 */
		if (worldObjectParent != null) {
			worldObjectParent.layoutChildren();
		}
	}

	/*
	 * Modification to PNode's animateToTransform. This animation is sequenced
	 * so that the previous transform animation finishes first (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#animateToTransform(java.awt.geom.AffineTransform,
	 *      long)
	 */
	@Override
	public PTransformActivity animateToTransform(AffineTransform destTransform, long duration) {
		if (duration == 0) {
			setTransform(destTransform);
			return null;
		} else {
			PTransformActivity.Target t = new PTransformActivity.Target() {
				public void getSourceMatrix(double[] aSource) {
					PXNode.this.getTransformReference(true).getMatrix(aSource);
				}

				public void setTransform(AffineTransform aTransform) {
					PXNode.this.setTransform(aTransform);
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

	public void removeFromWorld() {
		/*
		 * Notify edges that this object has been destroyed
		 */
		signalGlobalBoundsChanged();
		firePropertyChange(0, PROPERTY_REMOVED_FROM_WORLD, null, null);
		removeFromParent();
	}

	public WorldObject getWorldObject() {
		return worldObjectParent;
	}

	public boolean isAnimating() {
		if (busyAnimatingUntilTime < System.currentTimeMillis()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setParent(PNode newParent) {
		PNode oldParent = getParent();
		super.setParent(newParent);

		if (newParent != oldParent) {

			if (newParent != null)
				signalGlobalBoundsChanged();
		}
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		signalGlobalBoundsChanged();
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
			if (each instanceof PXNode) {
				PXNode wo = (PXNode) each;

				wo.signalGlobalBoundsChanged();
			}
		}

	}

	public void setWorldObject(WorldObject worldObjectParent) {
		this.worldObjectParent = worldObjectParent;

	}

	@Override
	protected void parentBoundsChanged() {
		firePropertyChange(0, PROPERTY_PARENT_BOUNDS, null, null);
	}

	@Override
	public void addChild(int index, PNode child) {
		super.addChild(index, child);
		if (worldObjectParent != null && child != null && child instanceof PiccoloNodeInWorld) {
			worldObjectParent.childAdded(((PiccoloNodeInWorld) child).getWorldObject());
		}

	}

	@Override
	public PNode removeChild(int arg0) {
		PNode node = super.removeChild(arg0);

		if (worldObjectParent != null && node != null && node instanceof PiccoloNodeInWorld) {
			worldObjectParent.childRemoved(((PiccoloNodeInWorld) node).getWorldObject());
		}

		return node;
	}
}
