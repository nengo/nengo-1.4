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
	public UIProjection createAndAddLineEnd() {
		UIProjection lineEnd = new UIProjection(this);
		addChild(lineEnd);
		return lineEnd;
	}


	protected UIOrigin getOriginUI() {
		return myOrigin;
	}
}
