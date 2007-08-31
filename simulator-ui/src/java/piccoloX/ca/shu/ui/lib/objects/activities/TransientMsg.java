package ca.shu.ui.lib.objects.activities;

import java.awt.geom.Point2D;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.objects.TextNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * A message that appears in the World and disappears smoothly after a
 * duration.
 * 
 * @author Shu Wu
 */
public class TransientMsg extends TextNode {

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

	public void startAnimation() {
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
		removeActivity.startAfter(fadeOutActivity);
		addActivity(removeActivity);

	}
}
