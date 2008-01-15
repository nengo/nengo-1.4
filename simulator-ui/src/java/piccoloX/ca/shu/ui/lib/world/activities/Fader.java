package ca.shu.ui.lib.world.activities;

import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * Activity which gradually changes the transparency of an node
 * 
 * @author Shu Wu
 */
public class Fader extends PInterpolatingActivity {

	private PNode node;

	private float startingTransparency;

	private float targetTransparency;

	/**
	 * @param target
	 *            Node target
	 * @param duration
	 *            Duration of the activity
	 * @param finalOpacity
	 *            Transparency target
	 */
	public Fader(WorldObjectImpl target, long duration, float finalOpacity) {
		super(duration,
				(int) (1000 / UIEnvironment.ANIMATION_TARGET_FRAME_RATE));
		this.node = target.getPiccolo();
		this.targetTransparency = finalOpacity;
	}

	@Override
	protected void activityStarted() {
		startingTransparency = node.getTransparency();
	}

	@Override
	public void setRelativeTargetValue(float zeroToOne) {

		super.setRelativeTargetValue(zeroToOne);

		float transparency = startingTransparency
				+ ((targetTransparency - startingTransparency) * (zeroToOne));

		node.setTransparency(transparency);

	}

}
