package ca.shu.ui.lib.objects.lines;

import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.DirectedEdge;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.IDroppable;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Shu
 */
public abstract class LineConnector extends WorldObject implements
		Interactable, IDroppable {

	private static final long serialVersionUID = 1L;

	private final Edge myEdge;

	private final LineOriginIcon myIcon;

	private ILineTermination myTermination;

	private final LineWell myWell;

	public LineConnector(LineWell well) {
		super();
		this.myWell = well;

		myEdge = new Edge(well, this);
		myEdge.setPointerVisible(true);
		well.getWorldLayer().addChild(myEdge);

		myIcon = new LineOriginIcon();
		myIcon.setColor(Style.COLOR_LINEEND);
		addChild(myIcon);

		setBounds(parentToLocal(getFullBounds()));
		setChildrenPickable(false);
		setSelectable(true);
	}

	private boolean tryConnectTo(ILineTermination newTermination,
			boolean modifyModel) {

		if (newTermination != getTermination()) {
			if (canConnectTo(newTermination)) {
				if (!initConnection(newTermination, modifyModel)) {
					newTermination = null;
				}
			}
		}
		updateTermination(newTermination);

		return (newTermination != null);
	}

	private void updateTermination(ILineTermination term) {
		if (term != null) {
			this.setOffset(0, 0);
		}

		if (term != myTermination) {
			if (myTermination != null) {
				justDisconnected();
			}

			myTermination = term;

			if (term != null) {
				((WorldObject) term).addChild(this);
				justConnected();
			}
		}
	}

	protected abstract boolean canConnectTo(ILineTermination termination);

	protected DirectedEdge getEdge() {
		return myEdge;
	}

	/**
	 * @param target
	 * @return Whether the connection was successfully initialized
	 */
	protected boolean initConnection(ILineTermination target,
			boolean modifyModel) {

		return true;
	}

	/**
	 * Called when the LineEnd is first connected to a Line end holder
	 */
	protected abstract void justConnected();

	/**
	 * Called when the LineEnd is first disconnected from a Line end holder
	 */
	protected abstract void justDisconnected();

	@Override
	protected void prepareForDestroy() {
		super.prepareForDestroy();
	}

	/**
	 * @param newTarget
	 *            Target to connect to
	 * @param modifyModel
	 *            Whether to modify the model represented by the connection
	 */
	public void connectTo(ILineTermination newTarget, boolean modifyModel) {
		boolean success = tryConnectTo(newTarget, modifyModel);

		if (!success) {
			UserMessages.showWarning("Could not connect");
		}

	}

	public LineWell getOrigin() {
		return myWell;
	}

	public ILineTermination getTermination() {
		return myTermination;
	}

	public void justDropped(List<WorldObject> targets) {
		boolean success = false;
		boolean attemptedConnection = false;

		for (WorldObject target : targets) {
			if (target == getOrigin()) {
				// Connector has been receded back into the origin
				destroy();
				target = null;
			}

			if (target instanceof ILineTermination) {
				if (canConnectTo((ILineTermination) target)) {
					attemptedConnection = true;
					success = tryConnectTo((ILineTermination) target, true);
					if (success) {
						break;
					}
				}
			}
		}

		if (!success) {
			updateTermination(null);
			Point2D position = myWell
					.globalToLocal(localToGlobal(new Point2D.Double(0, 0)));

			setOffset(position);
			myWell.addChild(this);
			
			if (attemptedConnection) {
				translate(-40, -20);
			}
			
		}
	}

	/**
	 * @param visible
	 *            Whether the edge associated with this LineEnd has it's
	 *            direction pointer visible
	 */
	public void setPointerVisible(boolean visible) {
		myEdge.setPointerVisible(visible);
	}

	public JPopupMenu showContextMenu() {

		/*
		 * delegate the context menu from the target if it's attached
		 */
		if ((myTermination != null) && (myTermination instanceof Interactable)) {
			return ((Interactable) myTermination).showContextMenu();
		}

		PopupMenuBuilder menu = new PopupMenuBuilder("Line End");
		menu.addAction(new StandardAction("Remove") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void action() throws ActionException {
				destroy();

			}

		});
		return menu.toJPopupMenu();
	}
}

/**
 * This edge is only visible when the LineEndWell is visible or the LineEnd is
 * connected
 * 
 * @author Shu Wu
 */
class Edge extends DirectedEdge {

	private static final long serialVersionUID = 1L;

	public Edge(LineWell startNode, LineConnector endNode) {
		super(startNode, endNode);
	}

	@Override
	protected void paint(PPaintContext paintContext) {
		/*
		 * Only paint this edge, if the LineEndWell is visible, or the LineEnd
		 * is connected
		 */
		if (getStartNode().getVisible()
				|| ((LineConnector) getEndNode()).getTermination() != null) {
			super.paint(paintContext);
		}
	}

}