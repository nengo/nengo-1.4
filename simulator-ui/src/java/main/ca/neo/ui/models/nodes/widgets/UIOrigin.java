package ca.neo.ui.models.nodes.widgets;

import java.awt.Color;

import ca.neo.model.InstantaneousOutput;
import ca.neo.model.Network;
import ca.neo.model.Origin;
import ca.neo.model.SimulationException;
import ca.neo.model.nef.impl.DecodedOrigin;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.util.UserMessages;

/**
 * UI Wrapper for an Origin
 * 
 * @author Shu Wu
 * 
 */
/**
 * @author Shu
 */
public abstract class UIOrigin extends Widget {

	/**
	 * Factory method for creating a UI Wrapper around a origin
	 * 
	 * @param uiNodeParent
	 *            UINeoNode to attach the UITermination object to the right
	 *            parent.
	 * @param origin
	 * @return UI Origin Wrapper
	 */
	public static UIOrigin createOriginUI(UINeoNode uiNodeParent, Origin origin) {

		if (origin instanceof DecodedOrigin) {
			return new UIDecodedOrigin(uiNodeParent, (DecodedOrigin) origin);
		} else {
			return new UIGenericOrigin(uiNodeParent, origin);
		}
	}

	private static final long serialVersionUID = 1L;

	private static final String typeName = "Origin";

	private boolean isExposed = false;

	private UIProjectionWell lineWell;

	private Color lineWellDefaultColor;

	protected UIOrigin(UINeoNode nodeParent, Origin origin) {
		super(nodeParent, origin);

		init();
	}

	private void init() {
		lineWell = new UIProjectionWell(this);
		lineWellDefaultColor = lineWell.getColor();
		ModelIcon icon = new ModelIcon(this, lineWell);
		icon.configureLabel(false);
		setIcon(icon);

		attachViewToModel();
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);

		tooltips.addProperty("Dimensions", "" + getModel().getDimensions());

		try {
			InstantaneousOutput value = getModel().getValues();

			tooltips.addProperty("Time: ", "" + value.getTime());
			tooltips.addProperty("Units: ", "" + value.getUnits());

		} catch (SimulationException e) {
		}

	}

	/**
	 * Destroys the Origin model
	 */
	protected abstract void destroyOriginModel();

	@Override
	protected void exposeModel(UINetwork networkUI, String exposedName) {
		networkUI.getModel().exposeOrigin(getModel(), exposedName);
		networkUI.showOrigin(exposedName);
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
	protected final void prepareToDestroyModel() {
		destroyOriginModel();
		super.prepareToDestroyModel();
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
		/*
		 * Check if we're already connection to that termination
		 */
		if (term.getConnector() != null && term.getConnector().getOriginUI() == this) {
			return;
		}

		UIProjection lineEnd = lineWell.createProjection();

		if (!lineEnd.tryConnectTo(term, modifyModel)) {
			UserMessages.showWarning("Could not connect");
		}
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
	public void setExposed(boolean isExposed) {
		if (this.isExposed == isExposed) {
			return;
		}
		this.isExposed = isExposed;

		if (isExposed) {
			lineWell.setColor(Widget.EXPOSED_COLOR);
		} else {
			lineWell.setColor(lineWellDefaultColor);
		}

	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);

		lineWell.setVisible(isVisible);
	}
}

class UIGenericOrigin extends UIOrigin {

	protected UIGenericOrigin(UINeoNode nodeParent, Origin origin) {
		super(nodeParent, origin);
	}

	@Override
	protected void destroyOriginModel() {
		UserMessages.showWarning("This origin model could not be removed, it will only be hidden.");
	}

}