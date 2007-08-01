package ca.shu.ui.lib.objects.lines;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import ca.shu.ui.lib.world.WorldLayer;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class LineEnd extends WorldObjectImpl {
	private static final long serialVersionUID = 1L;

	WorldObject target;
	LineEndWell well;

	public LineEnd(LineEndWell well) {
		super();
		this.well = well;

		addChild(new LineEndIcon());
		setBounds(getFullBounds());
		setChildrenPickable(false);
		setTangible(false);

		setDraggable(true);
	}

	public void justDropped() {

		Collection<WorldObject> nodes = this.getWorldLayer()
				.getChildrenAtBounds(localToGlobal(getBounds()));

		Iterator<WorldObject> it = nodes.iterator();
		while (it.hasNext()) {
			WorldObject node = it.next();

			// if (canConnectTo(node)) {

			// if (((ILineAcceptor) node).connect(this)) {

			if (tryConnectTo(node))
				return;
			// }
			// }
		}

		/*
		 * Not connected anymore, disconnect from what it was connected to
		 */
		tryConnectTo(null);
	}

	public WorldObject getTarget() {
		return target;
	}

	protected void justDisconnected() {

	}

	boolean isConnected = false;

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

	/**
	 * 
	 * @param target
	 *            Target to be connected with
	 * @return true is sucessfully connected
	 */
	public boolean tryConnectTo(WorldObject target) {
		boolean connected = false;

		WorldObjectImpl rootOfWell = getRootOfWell();

		if (target == null) {
			/*
			 * attach to the well if there is no target
			 */
			Point2D position = well
					.globalToLocal(localToGlobal(new Point2D.Double(0, 0)));

			setOffset(position);
			well.addChild(this);
		} else if (target == rootOfWell) {

			/*
			 * remove the lineEnd if its put back in the well's parent
			 */
			removeFromParent();
			connected = true;
		} else if (initConnection(target)) {
			// System.out.println("**Dropped into line in!");
			target.addChild(this);
			this.target = target;
			this.setOffset(0, 0);
			connected = true;

		}

		/*
		 * Disconnect
		 */
		if (!connected && isConnected) {
			isConnected = false;

			justDisconnected();
		}
		isConnected = connected;
		return isConnected;

	}

	/**
	 * Does some tasks before the UI connection is mde
	 * 
	 * @param target
	 *            the object to be connected to
	 * @return true if successfully connected
	 */
	protected boolean initConnection(WorldObject target) {
		return true;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
}
