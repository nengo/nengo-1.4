package ca.neo.ui.models.nodes.widgets;

import java.util.Vector;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.objects.DirectedEdge;
import ca.shu.ui.lib.objects.lines.ILineEndHolder;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineEndWell;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.WorldObject;

/**
 * UI Wrapper for an Origin
 * 
 * @author Shu Wu
 * 
 */
/**
 * @author Shu
 */
public class UIOrigin extends Widget {

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Origin";

	private Vector<MyLineEnd> ends;

	private MyWell lineWell;

	public UIOrigin(UINeoNode nodeParent, Origin origin) {
		super(nodeParent, origin);
		setName(origin.getName());
		init();
	}

	public UIOrigin(UINeoNode nodeParent) {
		super(nodeParent);
		init();
	}

	private void init() {
		lineWell = new MyWell();

		ModelIcon icon = new ModelIcon(this, lineWell);
		icon.configureLabel(false);
		setIcon(icon);

		this.setSelectable(false);
	}

	/**
	 * @param end
	 *            LineEnd to be added
	 */
	private void addLineEnd(MyLineEnd end) {
		if (ends == null)
			ends = new Vector<MyLineEnd>();
		ends.add(end);
	}

	/**
	 * @param term
	 *            Termination to be disconnected from
	 * @return True if successful
	 */
	private boolean disconnectModelFrom(UITermination term) {

		if (term != null) {
			Util.debugMsg("Projection removed " + term.getName());
			try {
				getNodeParent().getParentNetwork().removeProjection(
						term.getModelTermination());
				return true;
			} catch (StructuralException e) {
				UserMessages.showWarning("Could not disconnect: "
						+ e.toString());
			}
			return false;
		} else {
			return true;
		}

	}

	/**
	 * @param end
	 *            Line End to be removed
	 */
	private void removeLineEnd(MyLineEnd end) {
		ends.remove(end);
	}

	@Override
	protected Object configureModel(ConfigParam configuredProperties)
			throws ConfigException {
		throw new NotImplementedException();
	}

	/**
	 * @param target
	 *            Target to be connected with
	 * @return true is successfully connected
	 */
	protected boolean connectModelTo(UITermination target) {
		Util.debugMsg("Projection added " + target.getName());
		try {

			getNodeParent().getParentNetwork().addProjection(getModel(),
					target.getModelTermination());

			return true;
		} catch (StructuralException e) {
			UserMessages.showWarning("Could not connect: " + e.getMessage());
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

	@Override
	protected void prepareForDestroy() {

		/*
		 * Removes line ends
		 */
		if (ends != null) {
			Object[] endsAr = ends.toArray();
			for (Object element : endsAr) {
				((WorldObject) (element)).destroy();
			}
		}

		super.prepareForDestroy();
	}

	/**
	 * Connect to a Termination
	 * 
	 * @param term
	 *            Termination to connect to
	 */
	public void connectTo(UITermination term) {
		connectTo(term, true);
	}

	/**
	 * @param term
	 *            Termination to connect to
	 * @param modifyModel
	 *            if true, the Network model will be updated to reflect this
	 *            connection
	 */
	public void connectTo(UITermination term, boolean modifyModel) {
		MyLineEnd lineEnd = lineWell.createAndAddLineEnd();
		lineEnd.connectTo(term, modifyModel);

	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		UserMessages.showError("POrigin is not configurable yet");
		return null;
	}

	@Override
	public Origin getModel() {
		return (Origin) super.getModel();
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

	/**
	 * Line Ends for this origin
	 * 
	 * @author Shu Wu
	 */
	class MyLineEnd extends LineEnd {

		private static final long serialVersionUID = 1L;

		public MyLineEnd(LineEndWell well) {

			super(well);
			addLineEnd(this);
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
				 * Recursive connections are represented by an upward arcing
				 * edge
				 */
				UINeoNode nodeParent = UIOrigin.this.getNodeParent();
				getEdge().setLineShape(DirectedEdge.EdgeShape.UPWARD_ARC);
				getEdge().setMinArcRadius(
						nodeParent.localToParent(nodeParent.getBounds())
								.getWidth());
				setPointerVisible(false);
			} else {
				getEdge().setLineShape(DirectedEdge.EdgeShape.STRAIGHT);
				setPointerVisible(true);
			}

		}

		@Override
		protected boolean canConnectTo(ILineEndHolder target) {
			if ((target instanceof UITermination)) {
				return true;
			} else
				return false;

		}

		@Override
		protected boolean initConnection(ILineEndHolder target,
				boolean modifyModel) {
			if (!(target instanceof UITermination))
				return false;
			if (modifyModel) {
				if (UIOrigin.this.connectModelTo((UITermination) target)) {
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
			if (((UITermination) getTarget()).getNodeParent() == getNodeParent()) {

				setRecursive(true);
			}
		}

		@Override
		protected void justDisconnected() {
			super.justDisconnected();
			setRecursive(false);

			WorldObject oldTarget = (WorldObject) getTarget();
			if (disconnectModelFrom((UITermination) getTarget())) {

				oldTarget.popupTransientMsg("Projection removed from Network");
			}

		}

		@Override
		protected void prepareForDestroy() {
			removeLineEnd(this);
			super.prepareForDestroy();
		}

	}

	/**
	 * LineEndWell for this origin
	 * 
	 * @author Shu Wu
	 */
	class MyWell extends LineEndWell {

		private static final long serialVersionUID = 1L;

		@Override
		protected LineEnd constructLineEnd() {
			return new MyLineEnd(this);
		}

		/**
		 * @return new LineEnd created
		 */
		public MyLineEnd createAndAddLineEnd() {
			MyLineEnd lineEnd = new MyLineEnd(this);
			addChild(lineEnd);
			return lineEnd;
		}

	}
}
