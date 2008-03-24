package ca.neo.ui.models.nodes.widgets;

import java.awt.Color;

import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.impl.DecodedTermination;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.nodes.UINetwork;
import ca.neo.ui.models.tooltips.TooltipBuilder;
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
public abstract class UITermination extends Widget implements ILineTermination {
	private static final long serialVersionUID = 1L;

	private static String objToString(Object configValue) {

		if (Util.isArray(configValue)) {
			return Util.arrayToString(configValue);
		} else {
			return configValue.toString();
		}

	}

	/**
	 * Factory method for creating a UI Wrapper around a termination
	 * 
	 * @param uiNodeParent
	 *            UINeoNode to attach the UITermination object to the right
	 *            parent.
	 * @param termination
	 * @return UI Termination Wrapper
	 */
	public static UITermination createTerminationUI(UINeoNode uiNodeParent, Termination termination) {
		if (termination instanceof DecodedTermination) {
			return new UIDecodedTermination(uiNodeParent, (DecodedTermination) termination);
		} else {
			return new UIGenericTermination(uiNodeParent, termination);
		}
	}

	private boolean isExposed = false;

	private LineTerminationIcon myIcon;

	private Color myIconDefaultColor;

	protected UITermination(UINeoNode nodeParent, Termination term) {
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
		if (getConnector() != null) {
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

//		tooltips.addTitle("Configuration");
		tooltips.addProperty("Time Constant", String.valueOf(getModel().getTau()));
		tooltips.addProperty("Modulatory", String.valueOf(getModel().getModulatory()));
	}

	@Override
	protected void constructWidgetMenu(AbstractMenuBuilder menu) {
		super.constructWidgetMenu(menu);

		if (getConnector() != null) {
			menu.addAction(new DisconnectAction("Disconnect"));
		}
	}

	/**
	 * Destroys the termination model
	 */
	protected abstract void destroyTerminationModel();

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
	protected final void prepareToDestroyModel() {
		disconnect();
		destroyTerminationModel();
		super.prepareToDestroyModel();
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
		if (getConnector() != null) {
			try {
				getNodeParent().getNetworkParent().getModel().removeProjection(getModel());
				getNodeParent().showPopupMessage(
						"REMOVED Projection to " + getNodeParent().getName() + "." + getName());

				getConnector().destroy();
			} catch (StructuralException e) {
				UserMessages.showWarning("Problem trying to disconnect: " + e.toString());
			}
		} else {
			/*
			 * Not connected
			 */
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
//	public float[][] getWeights() {
//		return (float[][]) getModel().getConfiguration().getProperty(Termination.WEIGHTS);
//	}

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

	/**
	 * Action for removing attached connection from the termination
	 * 
	 * @author Shu Wu
	 */
	class DisconnectAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		public DisconnectAction(String actionName) {
			super("Remove connection from Termination", actionName);
		}

		@Override
		protected void action() throws ActionException {
			disconnect();
		}
	}
}

class UIGenericTermination extends UITermination implements ILineTermination {

	protected UIGenericTermination(UINeoNode nodeParent, Termination term) {
		super(nodeParent, term);
	}

	@Override
	protected boolean showRemoveModelAction() {
		return false;
	}

	@Override
	protected void destroyTerminationModel() {
		/*
		 * Do nothing
		 */
	}

}
