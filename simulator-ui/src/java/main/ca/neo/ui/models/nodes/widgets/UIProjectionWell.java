package ca.neo.ui.models.nodes.widgets;

import ca.shu.ui.lib.objects.lines.LineWell;
import ca.shu.ui.lib.objects.models.ModelObject;

/**
 * LineEndWell for this origin
 * 
 * @author Shu Wu
 */
public class UIProjectionWell extends LineWell {

	private static final long serialVersionUID = 1L;
	private UIOrigin myOrigin;

	public UIProjectionWell(UIOrigin myOrigin) {
		super();
		this.myOrigin = myOrigin;
	}

	/**
	 * @return new LineEnd created
	 */
	public UIProjection createProjection() {
		final UIProjection projection = new UIProjection(this);
		addChild(projection);

		/*
		 * TODO: Remove listener when not needed
		 */
		myOrigin.addModelListener(new ModelObject.ModelListener() {
			public void modelDestroyed(Object model) {
				projection.disconnectFromTermination();

			}
		});

		return projection;
	}

	// public Iterable<UIProjection> getProjections() {
	// LinkedList<UIProjection> projections = new LinkedList<UIProjection>();
	// for (WorldObject wo : getChildren()) {
	// if (wo instanceof UIProjection) {
	// projections.add((UIProjection) wo);
	// }
	// }
	// return projections;
	// }

	protected UIOrigin getOriginUI() {
		return myOrigin;
	}
}
