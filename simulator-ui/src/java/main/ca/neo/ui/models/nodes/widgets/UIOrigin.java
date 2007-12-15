package ca.neo.ui.models.nodes.widgets;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.objects.lines.LineConnector;
import ca.shu.ui.lib.objects.lines.LineWell;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;

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
	 * @param term
	 *            Termination to be disconnected from
	 * @return True if successful
	 */
	public boolean disconnect(UITermination term) {

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

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ConfigException {
		throw new NotImplementedException();
	}

	/**
	 * @param target
	 *            Target to be connected with
	 * @return true is successfully connected
	 */
	protected boolean connect(UITermination target) {
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
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);

		tooltips.addPart(new PropertyPart("Dimensions", ""
				+ getModel().getDimensions()));

		try {
			InstantaneousOutput value = getModel().getValues();

			tooltips.addPart(new PropertyPart("Time: ", "" + value.getTime()));
			tooltips
					.addPart(new PropertyPart("Units: ", "" + value.getUnits()));

		} catch (SimulationException e) {
		}

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
		UIProjection lineEnd = lineWell.createAndAddLineEnd();
		lineEnd.connectTo(term, modifyModel);

	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		UserMessages.showError("POrigin is not configurable yet");
		return null;
	}

	@Override
	public Origin getModel() {
		return (Origin) super.getModel();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);

		lineWell.setVisible(isVisible);

	}

	/**
	 * LineEndWell for this origin
	 * 
	 * @author Shu Wu
	 */
	class MyWell extends LineWell {

		private static final long serialVersionUID = 1L;

		@Override
		protected LineConnector constructLineEnd() {
			UIProjection projection = new UIProjection(this, UIOrigin.this);
			return projection;
		}

		/**
		 * @return new LineEnd created
		 */
		public UIProjection createAndAddLineEnd() {
			UIProjection lineEnd = new UIProjection(this, UIOrigin.this);
			addChild(lineEnd);
			return lineEnd;
		}

	}
}
