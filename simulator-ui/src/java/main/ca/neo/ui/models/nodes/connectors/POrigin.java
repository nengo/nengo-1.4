package ca.neo.ui.models.nodes.connectors;

import java.util.Vector;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.ui.configurable.managers.PropertySet;
import ca.neo.ui.configurable.struct.PropDescriptor;
import ca.neo.ui.exceptions.ModelConfigurationException;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.objects.GEdge;
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

	static final String typeName = "Origin";

	private Vector<MyLineEnd> ends;

	MyWell lineWell;

	public POrigin(PNeoNode nodeParent, Origin origin) {
		super(nodeParent);

		setModel(origin);
		lineWell = new MyWell();

		setIcon(new MyIcon(this, lineWell));

		this.setDraggable(false);

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
		MyLineEnd lineEnd = lineWell.createConnection();
		lineEnd.connectTo(term, modifyModel);

	}

	public boolean disconnectModelFrom(PTermination target) {
		if (target != null) {
			Util.debugMsg("Projection removed " + target.getName());
			try {
				getNodeParent().getParentNetwork().removeProjection(
						target.getModelTermination());
				return true;
			} catch (StructuralException e) {
				Util.UserWarning("Could not disconnect: " + e.toString());
			}
			return false;
		} else {
			return true;
		}

	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		Util.UserError("POrigin is not configurable yet");
		return null;
	}

	@Override
	public Origin getModel() {
		return (Origin) super.getModel();
	}

	@Override
	public String getName() {

		return getModel().getName();
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);

		lineWell.setVisible(isVisible);

	}

	protected void addOriginEnd(MyLineEnd end) {
		if (ends == null)
			ends = new Vector<MyLineEnd>();
		ends.add(end);
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ModelConfigurationException {
		throw new NotImplementedException();
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

			getNodeParent().getParentNetwork().addProjection(getModel(),
					target.getModelTermination());

			// getWorld().showTooltip(new ConnectedTooltip("Connected"),
			// target);

			return true;
		} catch (StructuralException e) {
			Util.UserWarning("Could not connect: " + e.getMessage());
			return false;
		}
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();

		tooltips.addPart(new PropertyPart("Dimensions", ""
				+ getModel().getDimensions()));

		try {
			InstantaneousOutput value = getModel().getValues();

			tooltips.addPart(new PropertyPart("Time: ", "" + value.getTime()));
			tooltips
					.addPart(new PropertyPart("Units: ", "" + value.getUnits()));

		} catch (SimulationException e) {
		}

		return tooltips;
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

	protected void removeOriginEnd(MyLineEnd end) {
		ends.remove(end);
	}

	class MyIcon extends ModelIcon {

		private static final long serialVersionUID = 1L;

		public MyIcon(PModel parent, WorldObject lineEnd) {
			super(parent, lineEnd);

			configureLabel(false);

			// TODO Auto-generated constructor stub
		}

	}

	class MyLineEnd extends LineEnd {

		private static final long serialVersionUID = 1L;

		/**
		 * 
		 * @param well
		 * @param modifyModelOnFirstConnection
		 *            Whether to modify the NEO Model on the first connection
		 */
		public MyLineEnd(LineEndWell well) {

			super(well);
			addOriginEnd(this);
		}

		private void setConnectionRecursive(boolean isRecursive) {
			if (isRecursive) {
				PNeoNode nodeParent = POrigin.this.getNodeParent();
				getEdge().setLineShape(GEdge.LineShape.UPWARD_ARC);
				getEdge().setMinArcRadius(
						nodeParent.localToParent(nodeParent.getBounds())
								.getWidth());
				setPointerVisible(false);
			} else {
				getEdge().setLineShape(GEdge.LineShape.STRAIGHT);
				setPointerVisible(true);
			}

		}

		@Override
		protected boolean canConnectTo(ILineAcceptor target) {
			if ((target instanceof PTermination)) {
				return true;
			} else
				return false;

		}

		@Override
		protected boolean initConnection(ILineAcceptor target,
				boolean modifyModel) {
			if (!(target instanceof PTermination))
				return false;
			if (modifyModel) {
				if (POrigin.this.connectModelTo((PTermination) target)) {
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
			super.justConnected();

			/*
			 * Detect recurrent connections
			 */
			if (((PTermination) getTarget()).getNodeParent() == getNodeParent()) {

				setConnectionRecursive(true);
			}
		}

		@Override
		protected void justDisconnected() {
			super.justDisconnected();
			setConnectionRecursive(false);

			WorldObject oldTarget = (WorldObject) getTarget();
			if (disconnectModelFrom((PTermination) getTarget())) {

				oldTarget.popupTransientMsg("Projection removed from Network");
			}

		}

		@Override
		protected void prepareForDestroy() {
			removeOriginEnd(this);
			super.prepareForDestroy();
		}

	}

	class MyWell extends LineEndWell {

		private static final long serialVersionUID = 1L;

		/**
		 * 
		 * @param modifyModel
		 *            whether this UI connection will modify the NEO model
		 *            underneath
		 * @return
		 */
		public MyLineEnd createConnection() {
			MyLineEnd lineEnd = new MyLineEnd(this);
			addChild(lineEnd);
			return lineEnd;
		}

		@Override
		protected LineEnd constructLineEnd() {
			return new MyLineEnd(this);
		}

	}
}

class ConnectedTooltip extends Tooltip {

	private static final long serialVersionUID = 1L;

	public ConnectedTooltip(String msg) {
		super();
		this.addToLayout(new GText(msg));
	}

}
