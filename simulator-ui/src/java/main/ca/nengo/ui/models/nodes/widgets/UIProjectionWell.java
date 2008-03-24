package ca.nengo.ui.models.nodes.widgets;

import ca.shu.ui.lib.objects.lines.LineWell;
import ca.shu.ui.lib.objects.models.ModelObject.ModelListener;

/**
 * LineEndWell for this origin
 * 
 * @author Shu Wu
 */
public class UIProjectionWell extends LineWell {

	private static final long serialVersionUID = 1L;
	private final UIOrigin myOrigin;

	public UIProjectionWell(UIOrigin myOrigin) {
		super();
		this.myOrigin = myOrigin;
	}

	/**
	 * @return new LineEnd created
	 */
	public UIProjection createProjection() {
		UIProjection projection = new UIProjection(this);
		addChild(projection);

		final RemoveProjectionListener removeProjectionListener = new RemoveProjectionListener(
				projection);
		myOrigin.addModelListener(removeProjectionListener);

		/*
		 * Remove the listener when the projection is destroyed
		 */
		projection.addPropertyChangeListener(Property.REMOVED_FROM_WORLD, new Listener() {

			public void propertyChanged(Property event) {
				myOrigin.removeModelListener(removeProjectionListener);
			}
		});

		return projection;
	}

	class RemoveProjectionListener implements ModelListener {
		private UIProjection projection;

		public RemoveProjectionListener(UIProjection projection) {
			super();
			this.projection = projection;
		}

		public void modelDestroyed(Object model) {
			projection.disconnectFromTermination();
			projection.destroy();
		}

		public void modelDestroyStarted(Object model) {
			// Do nothing
		}

	}

	protected UIOrigin getOriginUI() {
		return myOrigin;
	}
}
