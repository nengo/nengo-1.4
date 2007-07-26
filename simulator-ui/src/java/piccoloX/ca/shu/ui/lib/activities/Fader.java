package ca.shu.ui.lib.activities;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

public class Fader extends PInterpolatingActivity {
	boolean fadeIn; // true = fade in, false = fade out

	float startingTransparency;

	float targetTransparency;

	PNode node;

	public Fader(PNode node, long duration, boolean fadeIn) {
		this(node, duration, 25, fadeIn);
	}

	public Fader(PNode node, long duration, long stepRate, boolean fadeIn) {
		super(duration, stepRate);
		// TODO Auto-generated constructor stub
		this.node = node;
		this.fadeIn = fadeIn;
		if (fadeIn) {
			startingTransparency = 0;
			targetTransparency = node.getTransparency();
		} else {
			startingTransparency = 1;
			targetTransparency = 0;
		}

		if (fadeIn)
			node.setTransparency(0);
		else
			node.setTransparency(1);
	}

	@Override
	protected void activityStarted() {
		// TODO Auto-generated method stub
		super.activityStarted();

		// startingTransparency = node.getTransparency();
	}

	@Override
	public void setRelativeTargetValue(float zeroToOne) {
		// TODO Auto-generated method stub
		super.setRelativeTargetValue(zeroToOne);

		float transparency = startingTransparency
				+ ((targetTransparency - startingTransparency) * (zeroToOne));
		// System.out.println(transparency);
		node.setTransparency(transparency);

	}

//	@Override
//	protected void activityFinished() {
//		// TODO Auto-generated method stub
//		super.activityFinished();
//
//		// if the node has faded out, set it invisible so it can't be interacted
//		// with
//		if (!fadeIn) {
//			node.setVisible(false);
//		}
//	}

}
