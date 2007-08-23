package ca.shu.ui.lib.activities;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

public class Fader extends PInterpolatingActivity {

	PNode node;

	float startingTransparency;

	float targetTransparency;

	public Fader(PNode node, long duration, float targetTransparency) {
		this(node, duration, 25, targetTransparency);
	}

	public Fader(PNode node, long duration, long stepRate,
			float targetTransparency) {
		super(duration, stepRate);
		// TODO Auto-generated constructor stub
		this.node = node;

		this.targetTransparency = targetTransparency;

	}

	@Override
	public void setRelativeTargetValue(float zeroToOne) {

		super.setRelativeTargetValue(zeroToOne);

		float transparency = startingTransparency
				+ ((targetTransparency - startingTransparency) * (zeroToOne));

		node.setTransparency(transparency);

	}

	@Override
	protected void activityStarted() {
		startingTransparency = node.getTransparency();
	}

}
