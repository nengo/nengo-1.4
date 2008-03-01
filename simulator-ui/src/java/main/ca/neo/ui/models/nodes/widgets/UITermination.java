package ca.neo.ui.models.nodes.widgets;

import java.awt.Color;

import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.util.Configuration;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.lines.ILineTermination;
import ca.shu.ui.lib.objects.lines.LineConnector;
import ca.shu.ui.lib.objects.lines.LineTerminationIcon;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.world.WorldObject;

/**
 * UI Wrapper for a Termination
 * 
 * @author Shu Wu
 */
public class UITermination extends Widget implements ILineTermination {

	private static final long serialVersionUID = 1L;

	private static String objToString(Object configValue) {

		if (Util.isArray(configValue)) {
			return Util.arrayToString(configValue);
		} else {
			return configValue.toString();
		}

	}

	private boolean isExposed = false;

	private LineTerminationIcon myIcon;

	private Color myIconDefaultColor;

	public UITermination(UINeoNode nodeParent, Termination term) {
		super(nodeParent, term);
		setName(term.getName());
		init();

	}

	private void init() {
		myIcon = new LineTerminationIcon();
		myIconDefaultColor = myIcon.getColor();
		ModelIcon iconWr = new ModelIcon(this, myIcon);
		iconWr.configureLabel(false);

		setIcon(iconWr);
	}

	/**
	 * @param target
	 *            Target to be connected with
	 * @return true is successfully connected
	 */
	protected boolean connect(UIOrigin source, boolean modifyModel) {
		if (isConnected()) {
			/*
			 * Cannot connect if already connected
			 */
			return false;
		}

		boolean successful = false;
		if (modifyModel) {

			try {
				if (getNodeParent().getNetworkParent() == null) {
					throw new StructuralException(
							"Can't create projection because termination is not within the scope of a Network");
				}

				getNodeParent().getNetworkParent().getModel().addProjection(source.getModel(),
						getModel());
				getNodeParent().showPopupMessage(
						"NEW Projection to " + getNodeParent().getName() + "." + getName());
				successful = true;
			} catch (StructuralException e) {
				disconnect();
				UserMessages.showWarning("Could not connect: " + e.getMessage());
			}
		} else {
			successful = true;
		}

		return successful;
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);

		tooltips.addProperty("Dimensions", "" + getModel().getDimensions());

		tooltips.addTitle("Configuration");
		Configuration config = getModel().getConfiguration();
		String[] configProperties = config.listPropertyNames();
		for (String element : configProperties) {
			Object propertyValue = config.getProperty(element);

			tooltips.addProperty(element, objToString(propertyValue));
		}
	}

	@Override
	protected void constructWidgetMenu(AbstractMenuBuilder menu) {
		super.constructWidgetMenu(menu);

		if (getConnector() != null) {
			menu.addAction(new RemoveConnectionAction("Disconnect", getConnector()));
		}
	}

	@Override
	protected void exposeModel(UINetwork networkUI, String exposedName) {
		networkUI.getModel().exposeTermination(getModel(), exposedName);
		networkUI.showTermination(exposedName);
	}

	@Override
	protected String getExposedName(Network network) {
		return network.getExposedTerminationName(getModel());
	}

	@Override
	protected String getModelName() {
		return getModel().getName();
	}

	@Override
	protected void unExpose(Network network) {
		if (getExposedName() != null) {
			network.hideTermination(getExposedName());
		} else {
			UserMessages.showWarning("Could not unexpose this termination");
		}
	}

	/**
	 * @param term
	 *            Termination to be disconnected from
	 * @return True if successful
	 */
	public void disconnect() {
		if (!isConnected()) {
			Util.debugMsg("Tryng to disconnect termination which isn't connected");
			return;
		}

		try {
			getNodeParent().getNetworkParent().getModel().removeProjection(getModel());
			getNodeParent().showPopupMessage(
					"REMOVED Projection to " + getNodeParent().getName() + "." + getName());

		} catch (StructuralException e) {
			UserMessages.showWarning("Problem trying to disconnect: " + e.toString());
		}
	}

	public Color getColor() {
		return myIcon.getColor();
	}

	public UIProjection getConnector() {
		for (WorldObject wo : getChildren()) {
			if (wo instanceof LineConnector) {
				if (wo instanceof UIProjection) {
					return (UIProjection) wo;
				} else {
					Util.Assert(false, "Unexpected projection type");
				}
			}
		}
		return null;
	}

	@Override
	public Termination getModel() {
		return (Termination) super.getModel();
	}

	@Override
	public String getTypeName() {
		return "Termination";
	}

	/**
	 * @return Termination weights matrix
	 */
	public float[][] getWeights() {
		return (float[][]) getModel().getConfiguration().getProperty(Termination.WEIGHTS);
	}

	public boolean isConnected() {
		return (getConnector() != null);
	}

	@Override
	public void setExposed(boolean isExposed) {
		if (this.isExposed == isExposed) {
			return;
		}
		this.isExposed = isExposed;
		if (isExposed) {
			myIcon.setColor(Widget.EXPOSED_COLOR);
			myIcon.moveToFront();
		} else {
			myIcon.setColor(myIconDefaultColor);
			myIcon.moveToBack();
		}
	}

}

/**
 * Action for removing attached connection from the termination
 * 
 * @author Shu Wu
 */
class RemoveConnectionAction extends StandardAction {
	private static final long serialVersionUID = 1L;
	private LineConnector lineEndToRemove;

	public RemoveConnectionAction(String actionName, LineConnector lineEndToRemove) {
		super("Remove connection from Termination", actionName);
		this.lineEndToRemove = lineEndToRemove;
	}

	@Override
	protected void action() throws ActionException {
		lineEndToRemove.destroy();
		lineEndToRemove = null;
	}
}