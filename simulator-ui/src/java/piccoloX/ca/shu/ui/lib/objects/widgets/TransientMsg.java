package ca.shu.ui.lib.objects.widgets;

import java.awt.geom.Point2D;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.objects.GText;
import edu.umd.cs.piccolo.activities.PActivity;

public class TransientMsg extends GText {

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

	public void animate() {
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
