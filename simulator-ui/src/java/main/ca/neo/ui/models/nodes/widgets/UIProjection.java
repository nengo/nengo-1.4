package ca.neo.ui.models.nodes.widgets;

import ca.neo.ui.models.UINeoNode;
import ca.shu.ui.lib.objects.lines.ILineTermination;
import ca.shu.ui.lib.objects.lines.LineConnector;
import ca.shu.ui.lib.world.piccolo.primitives.PXEdge;

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
			getEdge().setLineShape(PXEdge.EdgeShape.UPWARD_ARC);
			getEdge().setMinArcRadius(nodeParent.localToParent(nodeParent.getBounds()).getWidth());
			setPointerVisible(false);
		} else {
			getEdge().setLineShape(PXEdge.EdgeShape.STRAIGHT);
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
	protected void connectToTermination() {
		/*
		 * Detect recurrent connections
		 */
		if ((getTermination()).getNodeParent() == getOriginUI().getNodeParent()) {
			setRecursive(true);
		}
	}

	@Override
	protected void disconnectFromTermination() {
		if (getTermination() != null) {
			setRecursive(false);
			getTermination().disconnect();
		}
	}

	@Override
	protected boolean initTarget(ILineTermination target, boolean modifyModel) {
		if (!(target instanceof UITermination)) {
			return false;
		}

		if (((UITermination) target).connect(getOriginUI(), modifyModel)) {
			return true;
		} else {
			return false;
		}
	}

	public UIOrigin getOriginUI() {
		return ((UIProjectionWell) getWell()).getOriginUI();
	}

	@Override
	public UITermination getTermination() {
		return (UITermination) super.getTermination();
	}

}
