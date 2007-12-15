package ca.neo.ui.models.nodes.widgets;

import java.awt.Color;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Network;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.StructuralException;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.tooltips.TooltipProperty;
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

	private UIProjectionWell lineWell;

	public UIOrigin(UINeoNode nodeParent) {
		super(nodeParent);
		init();
	}

	public UIOrigin(UINeoNode nodeParent, Origin origin) {
		super(nodeParent, origin);

		init();
	}

	private void init() {
		lineWell = new UIProjectionWell(this);
		ModelIcon icon = new ModelIcon(this, lineWell);
		icon.configureLabel(false);
		setIcon(icon);

		this.setSelectable(false);
		updateModel();
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

		tooltips.addPart(new TooltipProperty("Dimensions", ""
				+ getModel().getDimensions()));

		try {
			InstantaneousOutput value = getModel().getValues();

			tooltips
					.addPart(new TooltipProperty("Time: ", "" + value.getTime()));
			tooltips.addPart(new TooltipProperty("Units: ", ""
					+ value.getUnits()));

		} catch (SimulationException e) {
		}

	}

	@Override
	protected void expose(Network network, String exposedName) {
		network.exposeOrigin(getModel(), exposedName);
	}

	@Override
	protected String getExposedName(Network network) {
		return network.getExposedOriginName(getModel());
	}

	@Override
	protected String getModelName() {
		return getModel().getName();
	}

	@Override
	protected void unExpose(Network network) {

		if (getExposedName() != null) {
			network.hideOrigin(getExposedName());
		} else {
			UserMessages.showWarning("Could not unexpose this origin");
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
	public PropertyDescriptor[] getConfigSchema() {
		UserMessages.showError("POrigin is not configurable yet");
		return null;
	}
	public Color getColor() {
		return lineWell.getColor();
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
}
