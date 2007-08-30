package ca.shu.ui.lib.objects.lines;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.exceptions.ActionException;
import ca.shu.ui.lib.handlers.Interactable;
import ca.shu.ui.lib.objects.GDirectedEdge;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;

public class LineEnd extends WorldObject implements Interactable {
	private static final long serialVersionUID = 1L;

	private ConnectionState connectionState = ConnectionState.NOT_CONNECTED;

	private final Edge edge;
	private final LineEndWellIcon myIcon;
	private ILineAcceptor myTarget;
	private final LineEndWell well;

	public LineEnd(LineEndWell well) {
		super();
		this.well = well;

		edge = new Edge(well, this);
		well.getWorldLayer().addChild(edge);

		myIcon = new LineEndWellIcon();
		myIcon.setColor(Style.COLOR_LINEEND);
		addChild(myIcon);
		setBounds(getFullBounds());
		setChildrenPickable(false);
		// setTangible(false);
		edge.setPointerVisible(true);

		setSelectable(true);
	}

	/**
	 * @param newTarget
	 *            Target to connect to
	 * @param modifyModel
	 *            Whether to modify the model represented by the connection
	 */
	public void connectTo(ILineAcceptor newTarget, boolean modifyModel) {

		setConnectionState(ConnectionState.CONNECTED, newTarget, modifyModel);

	}

	public ILineAcceptor getTarget() {
		return myTarget;
	}

	public LineEndWell getWell() {
		return well;
	}

	public boolean isConnected() {
		return (connectionState == ConnectionState.CONNECTED);
	}

	public boolean isContextMenuEnabled() {
		return true;
	}

	@Override
	public void justDropped() {

		ArrayList<PNode> results = new ArrayList<PNode>(20);

		((WorldObject) getWorldLayer()).findIntersectingNodes(
				localToGlobal(getBounds()), results);

		Iterator<PNode> it = results.iterator();
		ConnectionState newState = ConnectionState.NOT_CONNECTED;

		ILineAcceptor newTarget = null;
		while (it.hasNext()) {
			PNode node = it.next();

			newState = getConnectionState(node);
			if (newState != ConnectionState.NOT_CONNECTED) {

				if (newState == ConnectionState.CONNECTED) {

					newTarget = (ILineAcceptor) node;
				}
				break;
			}

		}

		setConnectionState(newState, newTarget, true);

	}

	public void setPointerVisible(boolean visible) {
		edge.setPointerVisible(visible);
	}

	public JPopupMenu showContextMenu(PInputEvent event) {

		// delegate the context menu from the target if it's attached
		if (isConnected() && (myTarget instanceof Interactable)) {
			return ((Interactable) myTarget).showContextMenu(event);
		}

		PopupMenuBuilder menu = new PopupMenuBuilder("Line End");
		menu.addAction(new StandardAction("Remove") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				destroy();

			}

		});
		return menu.getJPopupMenu();
	}

	/**
	 * @param target
	 *            to be connected with
	 * @return State of the connection with that target
	 */
	private ConnectionState getConnectionState(PNode target) {

		if (target == null) {

			return ConnectionState.NOT_CONNECTED;
		} else if (target == well) {
			return ConnectionState.RECEDED_INTO_WELL;
		} else if (target == myTarget) {
			return ConnectionState.CONNECTED;
		} else if (target instanceof ILineAcceptor) {
			ILineAcceptor targetAcc = (ILineAcceptor) target;

			/**
			 * check that there isn't a line connected to it already
			 */
			if (targetAcc.getLineEnd() == null && canConnectTo(targetAcc)) {
				return ConnectionState.CONNECTED;
			}

		}

		return ConnectionState.NOT_CONNECTED;
	}

	/**
	 * @param newState
	 *            New connection state
	 */
	private void setConnectionState(ConnectionState newState,
			ILineAcceptor newTarget, boolean initConnection) {

		if (newState == connectionState && newTarget == myTarget) {
			if (connectionState == ConnectionState.CONNECTED) {
				/*
				 * move the LineEnd back to the center of the target
				 */
				this.setOffset(0, 0);
			}
			return;
		}

		/*
		 * discover transitions;
		 */
		if (myTarget != null && myTarget != newTarget) {
			myTarget.setLineEnd(null);
			justDisconnected();
		}
		myTarget = null;

		switch (newState) {
		case CONNECTED:
			if (initConnection(newTarget, initConnection)) {
				newTarget.setLineEnd(this);

				this.setOffset(0, 0);
				myTarget = newTarget;
				justConnected();
			} else {
				popupTransientMsg("*** WARNING Connection refused ***");
				this.translate(-50, -50);
				setConnectionState(ConnectionState.NOT_CONNECTED, null, true);
			}
			break;
		case NOT_CONNECTED:
			/*
			 * attach to the well if there is no target
			 */
			Point2D position = well
					.globalToLocal(localToGlobal(new Point2D.Double(0, 0)));

			setOffset(position);
			well.addChild(this);

			break;

		case RECEDED_INTO_WELL:
			/*
			 * remove the lineEnd if its put back in the well's parent
			 */
			if (!isDestroyed())
				destroy();
			break;
		}

		connectionState = newState;
	};

	/**
	 * Does some tasks before the UI connection is mde
	 * 
	 * @param target
	 *            the object to be connected to
	 * @return true if successfully connected
	 */
	protected boolean canConnectTo(ILineAcceptor target) {
		return true;
	}

	protected GDirectedEdge getEdge() {
		return edge;
	}

	/**
	 * @param target
	 * @return Whether the connection was successfully initialized
	 */
	protected boolean initConnection(ILineAcceptor target, boolean modifyModel) {

		return true;
	}

	// public int tryConnectTo(WorldObject target) {
	// return tryConnectTo(target, true);
	// }
	protected void justConnected() {

	}

	/**
	 * Called when the LineEnd is first disconnected from a LineIn
	 */
	protected void justDisconnected() {

	}

	@Override
	protected void prepareForDestroy() {
		super.prepareForDestroy();
		setConnectionState(ConnectionState.RECEDED_INTO_WELL, null, true);

	}

	static enum ConnectionState {
		CONNECTED, NOT_CONNECTED, RECEDED_INTO_WELL
	}

}

/**
 * This edge is only visible when the LineEndWell is visible or the LineEnd is
 * connected
 * 
 * @author Shu Wu
 * 
 */
class Edge extends GDirectedEdge {

	private static final long serialVersionUID = 1L;

	public Edge(LineEndWell startNode, LineEnd endNode) {
		super(startNode, endNode);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void paint(PPaintContext paintContext) {
		/*
		 * Only paint this edge, if the LineEndWell is visible, or the LineEnd
		 * is connected
		 */
		if (getStartNode().getVisible()
				|| ((LineEnd) getEndNode()).isConnected())
			super.paint(paintContext);
	}

}