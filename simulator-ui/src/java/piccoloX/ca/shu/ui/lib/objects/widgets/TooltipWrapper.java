package ca.shu.ui.lib.objects.widgets;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldSky;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

public class TooltipWrapper extends WorldObject implements
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	WorldObject follow;
	WorldObject tooltip;
	PActivity fadeInActivity, fadeInPhase2Activity;
	WorldSky parent;

	public TooltipWrapper(WorldSky parent, WorldObject tooltip,
			WorldObject follow) {
		super();
		this.tooltip = tooltip;
		this.follow = follow;
		this.parent = parent;

		parent.addChild(this);

		tooltip.setDraggable(false);
		addToLayout(tooltip);
		setPickable(false);
		setChildrenPickable(false);
		setPaint(Style.COLOR_BACKGROUND);

		pushState(WorldObject.State.SELECTED);

		setTransparency(0);

		/*
		 * The tooltip will follow where the object it's attached to goes
		 */
		parent.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, this);
		follow.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, this);

	}

	public void fadeIn() {
		fadeInActivity = new Fader(this, 500, 0.5f);
		addActivity(fadeInActivity);

		/*
		 * fade in much more slowly in the second phase.
		 */
		fadeInPhase2Activity = new Fader(this, 2000, 1f);
		fadeInPhase2Activity.startAfter(fadeInActivity);
		addActivity(fadeInPhase2Activity);

	}

	/**
	 * Fades away and destroys itself after
	 */
	public void fadeAndDestroy() {
		PActivity fadeOutActivity = new Fader(this, 500, 0);
		if (fadeInActivity != null) {
			if (fadeInActivity.isStepping())
				fadeOutActivity.startAfter(fadeInActivity);

		}
		if (fadeInPhase2Activity != null) {
			fadeInPhase2Activity
					.terminate(PActivity.TERMINATE_WITHOUT_FINISHING);
		}

		addActivity(fadeOutActivity);

		PActivity destroyActivity = new PActivity(0) {

			@Override
			protected void activityStarted() {
				TooltipWrapper.this.destroy();
			}

		};

		addActivity(destroyActivity);
		destroyActivity.startAfter(fadeOutActivity);

	}

	public void propertyChange(PropertyChangeEvent arg0) {
		updatePosition();

	}

	public void updatePosition() {
		if (follow.isDestroyed()) {

			return;
		}

		PCamera camera = follow.getWorld().getSky();

		Rectangle2D followBounds = follow.objectToSky(follow.getBounds());

		double x = followBounds.getX()
				- ((getWidth() - followBounds.getWidth()) / 2f);
		double y = followBounds.getY() + followBounds.getHeight();

		if (x < 0) {
			x = 0;
		} else if (x + getWidth() > camera.getBounds().getWidth()) {
			x = camera.getBounds().getWidth() - getWidth();

		}
		if ((y + getHeight() > camera.getBounds().getHeight())
				&& ((followBounds.getY() - getHeight()) > 0)) {
			y = followBounds.getY() - getHeight();

		}

		Point2D offset = new Point2D.Double(x, y);
		setOffset(offset);

	}

	@Override
	protected void prepareForDestroy() {
		parent.removePropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM,
				this);
		follow.removePropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, this);
	}

}