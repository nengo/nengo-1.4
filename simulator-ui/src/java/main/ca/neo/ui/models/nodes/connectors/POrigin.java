package ca.neo.ui.models.nodes.connectors;

import java.util.Vector;

import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.objects.Tooltip;
import ca.shu.ui.lib.objects.lines.ILineAcceptor;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineEndWell;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;

/**
 * Proxy UI Object for a Origin model
 * 
 * @author Shu Wu
 * 
 */
public class POrigin extends PWidget {

	private static final long serialVersionUID = 1L;

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);

		lineWell.setVisible(isVisible);

	}

	static final String typeName = "Origin";

	OriginWell lineWell;

	public POrigin(PNeoNode nodeParent, Origin origin) {
		super(nodeParent);

		setModel(origin);
		lineWell = new OriginWell();

		setIcon(new OriginIcon(this, lineWell));

		this.setDraggable(false);

	}

	public boolean disconnectModelFrom(PTermination target) {
		if (target != null) {
			Util.debugMsg("Projection removed " + target.getName());
			try {
				getNodeParent().getNetworkViewer().getNetwork()
						.removeProjection(target.getModelTermination());
				return true;
			} catch (StructuralException e) {
				Util.Warning("Could not disconnect: " + e.toString());
			}
			return false;
		} else {
			return true;
		}

	}

	public void connectTo(PTermination term) {
		connectTo(term, true);
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
		OriginEnd lineEnd = lineWell.createConnection();
		lineEnd.connectTo(term, modifyModel);

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

			getNodeParent().getNetworkViewer().getNetwork().addProjection(
					getModelOrigin(), target.getModelTermination());

			// getWorld().showTooltip(new ConnectedTooltip("Connected"),
			// target);

			return true;
		} catch (StructuralException e) {
			Util.Warning("Could not connect: " + e.getMessage());
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
	protected void prepareForDestroy() {

		/*
		 * Removes line ends
		 */
		if (ends != null) {
			Object[] endsAr = ends.toArray();
			for (int i = 0; i < endsAr.length; i++) {
				((WorldObject) (endsAr[i])).destroy();
			}
		}

		super.prepareForDestroy();
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
		protected void prepareForDestroy() {
			removeOriginEnd(this);
			super.prepareForDestroy();
		}

		/**
		 * 
		 * @param well
		 * @param modifyModelOnFirstConnection
		 *            Whether to modify the NEO Model on the first connection
		 */
		public OriginEnd(LineEndWell well) {

			super(well);
			addOriginEnd(this);
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean canConnectTo(ILineAcceptor target) {
			if ((target instanceof PTermination)) {
				return true;
			} else
				return false;

		}

		@Override
		protected boolean initConnection(ILineAcceptor target, boolean modifyModel) {
			if (!(target instanceof PTermination))
				return false;
			if (modifyModel) {
				if (POrigin.this.connectModelTo((PTermination) target)) {
					getWorld().showTransientMsg("Projection added to Network",
							this);
					return true;
				} else {
					return false;
				}
			}
			return true;
		}

		@Override
		protected void justDisconnected() {
			super.justDisconnected();
			if (disconnectModelFrom((PTermination) getTarget())) {

				getWorld().showTransientMsg("Projection removed from Network",
						this);
			}

		}

	}

	class OriginIcon extends ModelIcon {

		private static final long serialVersionUID = 1L;

		public OriginIcon(PModel parent, WorldObject lineEnd) {
			super(parent, lineEnd);

			configureLabel(false);

			// TODO Auto-generated constructor stub
		}

	}

	class OriginWell extends LineEndWell {

		private static final long serialVersionUID = 1L;

		@Override
		protected LineEnd constructLineEnd() {
			return new OriginEnd(this);
		}

		/**
		 * 
		 * @param modifyModel
		 *            whether this UI connection will modify the NEO model
		 *            underneath
		 * @return
		 */
		public OriginEnd createConnection() {
			OriginEnd lineEnd = new OriginEnd(this);
			super.addLineEnd(lineEnd);
			return lineEnd;
		}

	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		Util.Error("POrigin is not configurable yet");
		return null;
	}
}

class ConnectedTooltip extends Tooltip {

	private static final long serialVersionUID = 1L;

	public ConnectedTooltip(String msg) {
		super();
		this.addToLayout(new GText(msg));
	}

}
