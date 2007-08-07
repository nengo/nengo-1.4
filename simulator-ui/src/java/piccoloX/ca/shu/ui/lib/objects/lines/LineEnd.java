package ca.shu.ui.lib.objects.lines;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class LineEnd extends WorldObjectImpl {
	@Override
	public void destroy() {
		super.destroy();
		setConnectionState(ConnectionState.RECEDED_INTO_WELL, null, true);

	}

	private static final long serialVersionUID = 1L;

	private WorldObject target;
	private LineEndWell well;

	public LineEnd(LineEndWell well) {
		super();
		this.well = well;

		addChild(new LineEndIcon(this));
		setBounds(getFullBounds());
		setChildrenPickable(false);
		// setTangible(false);

		setDraggable(true);
	}

	ConnectionState connectionState = ConnectionState.NOT_CONNECTED;

	public void justDropped() {

		Collection<WorldObject> nodes = this.getWorldLayer()
				.getChildrenAtBounds(localToGlobal(getBounds()));

		Iterator<WorldObject> it = nodes.iterator();
		ConnectionState newState = ConnectionState.NOT_CONNECTED;

		WorldObject newTarget = null;
		while (it.hasNext()) {
			WorldObject node = it.next();

			newState = getConnectionState(node);
			if (newState != ConnectionState.NOT_CONNECTED) {

				if (newState == ConnectionState.CONNECTED) {

					newTarget = node;
				}
				break;
			}

		}

		setConnectionState(newState, newTarget, true);

	}

	/**
	 * @param target
	 * @return Whether the connection was successfully initialized
	 */
	protected boolean initConnection(WorldObject target, boolean modifyModel) {

		return true;
	}

	/**
	 * @param newTarget
	 *            Target to connect to
	 * @param modifyModel
	 *            Whether to modify the model represented by the connection
	 */
	public void connectTo(WorldObject newTarget, boolean modifyModel) {

		setConnectionState(ConnectionState.CONNECTED, newTarget, modifyModel);

	}

	/**
	 * @param newState
	 *            New connection state
	 */
	private void setConnectionState(ConnectionState newState,
			WorldObject newTarget, boolean modifyModel) {

		if (newState == connectionState && newTarget == target) {
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
		if (newState != ConnectionState.CONNECTED
				&& connectionState == ConnectionState.CONNECTED) {
			justDisconnected();

		}

		switch (newState) {
		case CONNECTED:
			if (initConnection(newTarget, modifyModel)) {
				newTarget.addChild(this);
				this.setOffset(0, 0);
			} else {
				this.setOffset(0, -50);
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
		target = newTarget;
		connectionState = newState;
	}

	public WorldObject getTarget() {
		return target;
	}

	/**
	 * Called when the LineEnd is first disconnected from a LineIn
	 */
	protected void justDisconnected() {

	}

	/**
	 * 
	 * @return The root WorldObject which is inside a WorldLayer
	 */
	private WorldObjectImpl getRootOfWell() {
		WorldObjectImpl rootOfWell = well;

		while (rootOfWell != null) {
			WorldObjectImpl parent = (WorldObjectImpl) rootOfWell.getParent();

			if (parent instanceof WorldLayer) {
				return rootOfWell;
			}
			rootOfWell = parent;
		}
		return null;
	}

	// public int tryConnectTo(WorldObject target) {
	// return tryConnectTo(target, true);
	// }

	static enum ConnectionState {
		CONNECTED, NOT_CONNECTED, RECEDED_INTO_WELL
	};

	/**
	 * @param target
	 *            to be connected with
	 * @return State of the connection with that target
	 */
	private ConnectionState getConnectionState(WorldObject target) {
		WorldObjectImpl rootOfWell = getRootOfWell();

		if (target == null) {

			return ConnectionState.NOT_CONNECTED;
		} else if (target == rootOfWell) {
			return ConnectionState.RECEDED_INTO_WELL;
		} else if (canConnectTo(target)) {
			return ConnectionState.CONNECTED;
		}

		return ConnectionState.NOT_CONNECTED;
	}

	// /**
	// *
	// * @param newTarget
	// * Target to be connected with
	// * @param initializeConnection
	// * Whether to initialize the connection before connecting
	// *
	// * @return 0 if successfully connected, 1 if not connected, 2 if the
	// LineEnd
	// * has been receded back into the well
	// *
	// *
	// */
	// public int tryConnectTo(WorldObject newTarget, boolean
	// initializeConnection) {
	//
	// WorldObjectImpl rootOfWell = getRootOfWell();
	//
	// boolean recededIntoWell = false;
	// if (newTarget == null) {
	//
	// return false;
	// } else if (newTarget == rootOfWell) {
	//
	// recededIntoWell = true;
	// connected = false;
	// } else {
	// if (initializeConnection) {
	// if (initConnection(newTarget)) {
	// newTarget.addChild(this);
	// this.target = newTarget;
	// this.setOffset(0, 0);
	// connected = true;
	// }
	// } else {
	// newTarget.addChild(this);
	// this.target = newTarget;
	// this.setOffset(0, 0);
	// connected = true;
	// }
	// }
	//
	// if (recededIntoWell)
	// return true;
	// return connected;
	//
	// }

	/**
	 * Does some tasks before the UI connection is mde
	 * 
	 * @param target
	 *            the object to be connected to
	 * @return true if successfully connected
	 */
	protected boolean canConnectTo(WorldObject target) {
		return true;
	}

	public boolean isConnected() {
		return (connectionState == ConnectionState.CONNECTED);
	}

	public LineEndWell getWell() {
		return well;
	}
}
