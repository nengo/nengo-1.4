package ca.shu.ui.lib.world.piccolo.objects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldSky;
import ca.shu.ui.lib.world.WorldObject.Listener;
import ca.shu.ui.lib.world.activities.Fader;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import edu.umd.cs.piccolo.activities.PActivity;

public class TooltipWrapper extends WorldObjectImpl implements Listener {

	private static final long serialVersionUID = 1L;
	private PActivity fadeInActivity, fadeInPhase2Activity;
	private WorldObject target;
	private WorldSky parent;
	private WorldObject tooltip;

	/**
	 * @param parent
	 *            Parent which will hold this wrapper
	 * @param tooltip
	 *            Tooltip object
	 * @param target
	 *            Target which this tooltip shall be attached to
	 */
	public TooltipWrapper(WorldSky parent, WorldObject tooltip, WorldObject target) {
		super();
		this.tooltip = tooltip;
		this.target = target;
		this.parent = parent;

		parent.addChild(this);

		addChild(tooltip);
		setPickable(false);
		setChildrenPickable(false);
		setPaint(Style.COLOR_BACKGROUND);

		addChild(new Border(this, Style.COLOR_TOOLTIP_BORDER));

		updateBounds();

		/*
		 * The tooltip will follow where the object it's attached to goes
		 */
		tooltip.addPropertyChangeListener(Property.BOUNDS_CHANGED, this);
		parent.addPropertyChangeListener(Property.VIEW_TRANSFORM, this);
		target.addPropertyChangeListener(Property.FULL_BOUNDS, this);
		updatePosition();
	}

	/**
	 * Updates the bounds of this node in response to the tooltip it's carrying
	 */
	private void updateBounds() {
		tooltip.setOffset(5, 5);
		setBounds(0, 0, tooltip.getWidth() + 10, tooltip.getHeight() + 10);

	}

	/**
	 * Updates the position of this node in response to its target
	 */
	private void updatePosition() {
		if (target.isDestroyed()) {

			return;
		}

		WorldSky camera = target.getWorld().getSky();

		Rectangle2D followBounds = target.objectToSky(target.getBounds());

		double x = followBounds.getX() - ((getWidth() - followBounds.getWidth()) / 2f);
		double y = followBounds.getY() + followBounds.getHeight();

		if (x + getWidth() > camera.getBounds().getWidth()) {
			x = camera.getBounds().getWidth() - getWidth();
		}

		if (x < 0) {
			x = 0;
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
		tooltip.removePropertyChangeListener(Property.BOUNDS_CHANGED, this);
		parent.removePropertyChangeListener(Property.VIEW_TRANSFORM, this);
		target.removePropertyChangeListener(Property.FULL_BOUNDS, this);
	}

	/**
	 * Fades away in an animated sequence, and then destroy itself
	 */
	public void fadeAndDestroy() {
		PActivity fadeOutActivity = new Fader(this, 500, 0);
		if (fadeInActivity != null) {

			if ((fadeInActivity.getStartTime() + fadeInActivity.getDuration()) > System
					.currentTimeMillis())
				fadeOutActivity.startAfter(fadeInActivity);

		}
		if (fadeInPhase2Activity != null) {
			fadeInPhase2Activity.terminate(PActivity.TERMINATE_WITHOUT_FINISHING);
		}

		getPiccolo().addActivity(fadeOutActivity);

		PActivity destroyActivity = new PActivity(0) {

			@Override
			protected void activityStarted() {
				TooltipWrapper.this.destroy();
			}

		};

		getPiccolo().addActivity(destroyActivity);
		destroyActivity.startAfter(fadeOutActivity);

	}

	/**
	 * Fades in, in an animated sequence
	 */
	public void fadeIn() {
		setTransparency(0);
		fadeInActivity = new Fader(this, 100, 1f);
		getPiccolo().addActivity(fadeInActivity);

		// /*
		// * fade in more slowly in the second phase.
		// */
		// fadeInPhase2Activity = new Fader(this, 1000, 1f);
		// fadeInPhase2Activity.startAfter(fadeInActivity);
		// addActivity(fadeInPhase2Activity);

	}

	/*
	 * Listens for position changes of the target, and view changes of the sky
	 * which this tooltip is attached to.(non-Javadoc)
	 */
	public void propertyChanged(Property event) {
		if (event == Property.BOUNDS_CHANGED) {
			updateBounds();
		} else {
			updatePosition();
		}

	}

}