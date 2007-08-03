package ca.neo.ui.models.nodes.connectors;

import java.util.Vector;

import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.IconWrapper;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.objects.Tooltip;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineEndWell;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;

/**
 * Proxy UI Object for a Origin model
 * 
 * @author Shu Wu
 * 
 */
public class POrigin extends PModel {

	private static final long serialVersionUID = 1L;

	static final String typeName = "Origin";

	PNeoNode nodeParent;

	OriginWell lineWell;

	public POrigin(PNeoNode nodeParent, Origin origin) {
		super();
		this.nodeParent = nodeParent;

		setModel(origin);
		lineWell = new OriginWell();

		setIcon(new OriginIcon(this, lineWell));

		this.setDraggable(false);

	}

	public boolean disconnectModelFrom(PTermination target) {
		Util.debugMsg("Projection removed " + target.getName());
		try {
			nodeParent.getNetworkViewer().getModel().removeProjection(
					target.getModelTermination());
			return true;
		} catch (StructuralException e) {
			Util.Warning("Could not disconnect: " + e.toString());
		}
		return false;
	}

	public void connectTo(PTermination term) {
		connectTo(term, false);
	}

	/**
	 * 
	 * @param term
	 *            the termination to be connected to
	 * @param modifyModel
	 *            if true, the Network model will be updated to reflect this
	 *            connection
	 */
	public void connectTo(PTermination term, boolean modifyModel) {
		OriginEnd lineEnd = lineWell.createConnection(modifyModel);
		lineEnd.tryConnectTo(term, modifyModel);

	}

	/**
	 * 
	 * @param target
	 *            Target to be connected with
	 * @return true is successfully connected
	 */
	protected boolean connectModelTo(PTermination target) {
		Util.debugMsg("Projection added " + target.getName());
		try {

			nodeParent.getNetworkViewer().getModel().addProjection(
					getModelOrigin(), target.getModelTermination());

			// getWorld().showTooltip(new ConnectedTooltip("Connected"),
			// target);

			return true;
		} catch (StructuralException e) {
			Util.Warning("Could not connect: " + e.toString());
			return false;
		}
	}

	@Override
	public String getName() {

		return getModelOrigin().getName();
	}

	public Origin getModelOrigin() {
		return (Origin) getModel();
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.world.impl.WorldObjectImpl#destroy()
	 */
	@Override
	public void destroy() {

		/*
		 * Removes line ends
		 */
		if (ends != null) {
			Object[] endsAr = ends.toArray();
			for (int i = 0; i < endsAr.length; i++) {
				((WorldObjectImpl) (endsAr[i])).destroy();
			}
		}

		nodeParent.removeWidget(this);

		super.destroy();
	}

	private Vector<OriginEnd> ends;

	protected void addOriginEnd(OriginEnd end) {
		if (ends == null)
			ends = new Vector<OriginEnd>();
		ends.add(end);
	}

	protected void removeOriginEnd(OriginEnd end) {
		ends.remove(end);
	}

	class OriginEnd extends LineEnd {

		@Override
		public void destroy() {
			removeOriginEnd(this);
			justDisconnected();
			super.destroy();
		}

		/*
		 * Whether NEO model is updated with projection on initialization. This
		 * is set to false, when the model already has the projection (ie. a
		 * preloaded NEO Model)
		 */
		boolean modifyModel;

		/**
		 * 
		 * @param well
		 * @param modifyModelOnFirstConnection
		 *            Whether to modify the NEO Model on the first connection
		 */
		public OriginEnd(LineEndWell well, boolean modifyModelOnFirstConnection) {

			super(well);
			this.modifyModel = modifyModelOnFirstConnection;
			addOriginEnd(this);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean initConnection(WorldObject target) {
			if (!(target instanceof PTermination))
				return false;
			if (modifyModel) {
				return POrigin.this.connectModelTo((PTermination) target);
			}
			modifyModel = true;
			return true;
		}

		@Override
		protected void justDisconnected() {
			super.justDisconnected();
			POrigin.this.disconnectModelFrom((PTermination) getTarget());

		}

	}

	class OriginIcon extends IconWrapper {

		private static final long serialVersionUID = 1L;

		public OriginIcon(PModel parent, WorldObjectImpl lineEnd) {
			super(parent, lineEnd);

			configureLabel(false);

			// TODO Auto-generated constructor stub
		}

	}

	class OriginWell extends LineEndWell {

		private static final long serialVersionUID = 1L;

		@Override
		protected LineEnd constructLineEnd() {
			return new OriginEnd(this, true);
		}

		/**
		 * 
		 * @param modifyModel
		 *            whether this UI connection will modify the NEO model
		 *            underneath
		 * @return
		 */
		public OriginEnd createConnection(boolean modifyModel) {
			OriginEnd lineEnd = new OriginEnd(this, modifyModel);
			super.addLineEnd(lineEnd);
			return lineEnd;
		}

	}
}

class ConnectedTooltip extends Tooltip {

	public ConnectedTooltip(String msg) {
		super();
		this.addToLayout(new GText(msg));
	}

}
