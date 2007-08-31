package ca.shu.ui.lib.objects;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.activities.Fader;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.WorldSky;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

public class TooltipWrapper extends WorldObject implements
		PropertyChangeListener {

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
	public TooltipWrapper(WorldSky parent, WorldObject tooltip,
			WorldObject target) {
		super();
		this.tooltip = tooltip;
		this.target = target;
		this.parent = parent;

		parent.addChild(this);

		tooltip.setSelectable(false);
		addChild(tooltip);
		setPickable(false);
		setChildrenPickable(false);
		setPaint(Style.COLOR_BACKGROUND);

		tooltip.addPropertyChangeListener(PNode.PROPERTY_BOUNDS,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						updateBounds();
					}
				});

		addChild(new Border(this, Style.COLOR_TOOLTIP_BORDER));

		setTransparency(0);
		updateBounds();

		/*
		 * The tooltip will follow where the object it's attached to goes
		 */
		parent.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, this);
		target.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, this);
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

		PCamera camera = target.getWorld().getSky();

		Rectangle2D followBounds = target.objectToSky(target.getBounds());

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
		target.removePropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, this);

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

	/**
	 * Fades in, in an animated sequence
	 */
	public void fadeIn() {
		fadeInActivity = new Fader(this, 500, 0.5f);
		addActivity(fadeInActivity);

		/*
		 * fade in more slowly in the second phase.
		 */
		fadeInPhase2Activity = new Fader(this, 2000, 1f);
		fadeInPhase2Activity.startAfter(fadeInActivity);
		addActivity(fadeInPhase2Activity);

	}

	/*
	 * Listens for position changes of the target, and view changes of the sky
	 * which this tooltip is attached to.(non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		updatePosition();

	}

}