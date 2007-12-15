package ca.neo.ui.models.nodes.widgets;

import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.objects.DirectedEdge;
import ca.shu.ui.lib.objects.lines.ILineTermination;
import ca.shu.ui.lib.objects.lines.LineConnector;

/**
 * Line Ends for this origin
 * 
 * @author Shu Wu
 */
public class UIProjection extends LineConnector {

	private static final long serialVersionUID = 1L;

	public UIProjection(UIProjectionWell well) {
		super(well);
	}

	/**
	 * Sets whether the type of connection represented by this Line End is
	 * recursive (if it origin and termination are on the same model).
	 * 
	 * @param isRecursive
	 *            Whether this connection is recurisve
	 */
	private void setRecursive(boolean isRecursive) {
		if (isRecursive) {
			/*
			 * Recursive connections are represented by an upward arcing edge
			 */
			UINeoNode nodeParent = getOriginUI().getNodeParent();
			getEdge().setLineShape(DirectedEdge.EdgeShape.UPWARD_ARC);
			getEdge()
					.setMinArcRadius(
							nodeParent.localToParent(nodeParent.getBounds())
									.getWidth());
			setPointerVisible(false);
		} else {
			getEdge().setLineShape(DirectedEdge.EdgeShape.STRAIGHT);
			setPointerVisible(true);
		}

	}

	@Override
	protected boolean canConnectTo(ILineTermination target) {
		if ((target instanceof UITermination)) {
			if (!((UITermination) target).isModelBusy()) {
				return true;
			} else {
				return false;
			}
		} else
			return false;

	}

	@Override
	protected boolean initConnection(ILineTermination target,
			boolean modifyModel) {
		if (!(target instanceof UITermination))
			return false;
		if (modifyModel) {
			if (getOriginUI().connect((UITermination) target)) {
				popupTransientMsg("Projection added to Network");

				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void justConnected() {
		/*
		 * Detect recurrent connections
		 */
		if ((getTermination()).getNodeParent() == getOriginUI().getNodeParent()) {

			setRecursive(true);
		}
	}

	@Override
	protected void justDisconnected() {
		setRecursive(false);

		UITermination termination = getTermination();
		if (getOriginUI().disconnect(termination)) {

			termination.popupTransientMsg("Projection removed from Network");
		}

	}

	@Override
	protected void prepareForDestroy() {
		super.prepareForDestroy();
	}

	public UIOrigin getOriginUI() {
		return ((UIProjectionWell) getWell()).getOriginUI();
	}

	@Override
	public UITermination getTermination() {
		return (UITermination) super.getTermination();
	}

}
