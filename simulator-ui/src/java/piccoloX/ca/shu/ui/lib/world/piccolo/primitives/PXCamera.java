package ca.shu.ui.lib.world.piccolo.primitives;

import java.awt.geom.AffineTransform;

import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PUtil;

public class PXCamera extends PCamera implements PiccoloNodeInWorld {

	private static final long serialVersionUID = 1L;

	private PTransformActivity currentActivity;

	private WorldObject wo;

	/*
	 * Modification to PNode's animateToTransform. This animation is sequenced
	 * so that the previous transform animation finishes first (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#animateToTransform(java.awt.geom.AffineTransform,
	 *      long)
	 */
	@Override
	public PTransformActivity animateViewToTransform(AffineTransform destTransform, long duration) {
		if (duration == 0) {
			setViewTransform(destTransform);
			return null;
		} else {
			PTransformActivity.Target t = new PTransformActivity.Target() {
				public void getSourceMatrix(double[] aSource) {
					PXCamera.this.getViewTransform().getMatrix(aSource);
				}

				public void setTransform(AffineTransform aTransform) {
					PXCamera.this.setViewTransform(aTransform);
				}
			};

			if (currentActivity != null) {
				currentActivity.terminate(PActivity.TERMINATE_WITHOUT_FINISHING);
			}

			currentActivity = new PTransformActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE, t,
					destTransform);

			addActivity(currentActivity);
			return currentActivity;
		}
	}

	public WorldObject getWorldObject() {
		return wo;
	}

	public boolean isAnimating() {
		return false;
	}

	public void setWorldObject(WorldObject worldObjectParent) {
		this.wo = worldObjectParent;
	}
}
