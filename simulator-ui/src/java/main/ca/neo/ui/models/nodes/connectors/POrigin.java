package ca.neo.ui.models.nodes.connectors;

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

	public boolean disconnect(PTermination target) {

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
		LineEnd lineEnd = lineWell.createConnection(modifyModel);
		lineEnd.tryConnectTo(term);

	}

	/**
	 * 
	 * @param target
	 *            Target to be connected with
	 * @return true is successfully connected
	 */
	protected boolean connectModelTo(PTermination target) {

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

	@Override
	public void modelRemoved() {
		super.modelRemoved();

		nodeParent.removeWidget(this);

	}

	class OriginEnd extends LineEnd {

		boolean modifyModel;

		public OriginEnd(LineEndWell well, boolean modifyModel) {
			super(well);
			this.modifyModel = modifyModel;
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean initConnection(WorldObject target) {
			if (!(target instanceof PTermination))
				return false;
			if (modifyModel) {
				return POrigin.this.connectModelTo((PTermination) target);
			}

			return true;
		}

		@Override
		protected void justDisconnected() {
			super.justDisconnected();
			POrigin.this.disconnect((PTermination) getTarget());

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
		public LineEnd createConnection(boolean modifyModel) {
			LineEnd lineEnd = new OriginEnd(this, modifyModel);
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
