package ca.neo.ui.models.nodes.widgets;

import ca.shu.ui.lib.objects.lines.LineConnector;
import ca.shu.ui.lib.objects.lines.LineWell;

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

	@Override
	protected LineConnector constructLineEnd() {
		UIProjection projection = new UIProjection(this);
		return projection;
	}

	/**
	 * @return new LineEnd created
	 */
	public UIProjection createProjection() {
		UIProjection projection = new UIProjection(this);
		addChild(projection);
		return projection;
	}

//	public Iterable<UIProjection> getProjections() {
//		LinkedList<UIProjection> projections = new LinkedList<UIProjection>();
//		for (WorldObject wo : getChildren()) {
//			if (wo instanceof UIProjection) {
//				projections.add((UIProjection) wo);
//			}
//		}
//		return projections;
//	}

	protected UIOrigin getOriginUI() {
		return myOrigin;
	}
}
