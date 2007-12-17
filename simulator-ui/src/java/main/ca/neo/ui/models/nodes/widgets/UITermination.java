package ca.neo.ui.models.nodes.widgets;

import java.awt.Color;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.UserDialogs;
import ca.neo.ui.configurable.descriptors.PCouplingMatrix;
import ca.neo.ui.configurable.managers.ConfigManager;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.tooltips.TooltipProperty;
import ca.neo.ui.models.tooltips.TooltipTitle;
import ca.neo.util.Configuration;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.lines.ILineTermination;
import ca.shu.ui.lib.objects.lines.LineConnector;
import ca.shu.ui.lib.objects.lines.LineTerminationIcon;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;

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

	private LineTerminationIcon myIcon;

	public UITermination(UINeoNode nodeParent) {
		super(nodeParent);
		init();
	}

	public UITermination(UINeoNode nodeParent, Termination term) {
		super(nodeParent, term);
		setName(term.getName());
		init();

	}

	private void init() {
		myIcon = new LineTerminationIcon();
		ModelIcon iconWr = new ModelIcon(this, myIcon);
		iconWr.configureLabel(false);

		setIcon(iconWr);
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ConfigException {
		throw new NotImplementedException();
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);

		tooltips.addPart(new TooltipProperty("Dimensions", ""
				+ getModel().getDimensions()));

		tooltips.addPart(new TooltipTitle("Configuration"));
		Configuration config = getModel().getConfiguration();
		String[] configProperties = config.listPropertyNames();
		for (String element : configProperties) {
			Object propertyValue = config.getProperty(element);

			tooltips.addPart(new TooltipProperty(element,
					objToString(propertyValue)));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void constructWidgetMenu(AbstractMenuBuilder menu) {
		super.constructWidgetMenu(menu);

		if (getConnector() != null) {
			menu.addAction(new RemoveConnectionAction("Disconnect",
					getConnector()));
		}

		AbstractMenuBuilder configureMenu = menu.addSubMenu("Configure");

		/*
		 * Reflectively build property editors
		 */
		Configuration configuration = getModel().getConfiguration();
		String[] propertyNames = configuration.listPropertyNames();

		for (String propertyName : propertyNames) {
			Class type = configuration.getType(propertyName);
			if (type == float[][].class) {
				configureMenu.addAction(new EditWeightsAction("Weights"));
			} else {
				configureMenu.addAction(new ConfigurePropertyAction(
						propertyName, type));
			}

		}
	}

	@Override
	protected void expose(Network network, String exposedName) {
		network.exposeTermination(getModel(), exposedName);
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

	public Color getColor() {
		return myIcon.getColor();
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return null;
	}

	public LineConnector getConnector() {
		List<?> children = getChildrenReference();

		for (Object obj : children) {
			if (obj instanceof LineConnector)
				return (LineConnector) obj;
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
		return (float[][]) getModel().getConfiguration().getProperty(
				Termination.WEIGHTS);
	}

	private boolean isConnected;

	/**
	 * @param term
	 *            Termination to be disconnected from
	 * @return True if successful
	 */
	public void disconnect() {
		if (!isConnected) {
			return;
		}

		try {
			getNodeParent().getParentNetwork().removeProjection(getModel());
			getNodeParent().showPopupMessage(
					"Projection to " + getName() + " REMOVED");
			isConnected = false;
		} catch (StructuralException e) {
			UserMessages.showWarning("Could not disconnect: " + e.toString());
		}
	}

	/**
	 * @param target
	 *            Target to be connected with
	 * @return true is successfully connected
	 */
	protected boolean connect(UIOrigin source, boolean modifyModel) {
		if (isConnected) {
			disconnect();
		}
		if (modifyModel) {
			try {

				getNodeParent().showPopupMessage(
						"Projection to " + getName() + " ADDED");
				getNodeParent().getParentNetwork().addProjection(
						source.getModel(), getModel());

			} catch (StructuralException e) {
				UserMessages
						.showWarning("Could not connect: " + e.getMessage());
			}
		}
		isConnected = true;

		return isConnected;
	}

	/**
	 * @param newWeights
	 *            Weights matrix to be assigned to this termination
	 */
	public void setWeights(float[][] newWeights) {
		try {
			getModel().getConfiguration().setProperty(Termination.WEIGHTS,
					newWeights);
			showPopupMessage("Weights changed on Termination");
		} catch (StructuralException e) {
			UserMessages.showWarning("Could not modify weights: "
					+ e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	class ConfigurePropertyAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		private String propertyName;

		private Class propertyType;

		ConfigurePropertyAction(String propertyName, Class type) {
			super(propertyName);
			this.propertyName = propertyName;
			this.propertyType = type;
		}

		@Override
		protected void action() throws ActionException {
			Object propertyValue = null;
			Object defaultPropertyValue = getModel().getConfiguration()
					.getProperty(propertyName); // get the current value of this
			// property

			try {
				if (propertyType == Boolean.class) {
					propertyValue = UserDialogs.showDialogBoolean(propertyName,
							(Boolean) defaultPropertyValue);
				} else if (propertyType == Integer.class) {
					propertyValue = UserDialogs.showDialogInteger(propertyName,
							(Integer) defaultPropertyValue);
				} else if (propertyType == Float.class) {
					propertyValue = UserDialogs.showDialogFloat(propertyName,
							(Float) defaultPropertyValue);
				} else if (propertyType == String.class) {
					propertyValue = UserDialogs.showDialogString(propertyName,
							(String) defaultPropertyValue);
				} else {
					UserMessages
							.showWarning("Could not configure because the property type is unsupported by this UI");
					return;
				}

				try {
					getModel().getConfiguration().setProperty(propertyName,
							propertyValue);
				} catch (StructuralException e) {
					e.printStackTrace();
				}
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
			}
		}
	}

	/**
	 * Action for editing termination weights matrix
	 * 
	 * @author Shu Wu
	 */
	class EditWeightsAction extends ReversableAction {
		private static final long serialVersionUID = 1L;

		float[][] oldWeights;

		public EditWeightsAction(String actionName) {
			super("Edit weights at Termination " + getName(), actionName);
		}

		@Override
		protected void action() throws ActionException {
			PropertyDescriptor pCouplingMatrix = new PCouplingMatrix(
					getWeights());
			try {
				PropertySet result = ConfigManager.configure(
						new PropertyDescriptor[] { pCouplingMatrix },
						"Coupling matrix", UIEnvironment.getInstance(),
						ConfigManager.ConfigMode.STANDARD);

				setWeights((float[][]) result.getProperty(pCouplingMatrix));
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
				throw new UserCancelledException();
			}

		}

		@Override
		protected void undo() throws ActionException {
			setWeights(oldWeights);
		}
	}

	@Override
	protected void prepareForDestroy() {
		disconnect();
		super.prepareForDestroy();
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

	public RemoveConnectionAction(String actionName,
			LineConnector lineEndToRemove) {
		super("Remove connection from Termination", actionName);
		this.lineEndToRemove = lineEndToRemove;
	}

	@Override
	protected void action() throws ActionException {
		lineEndToRemove.destroy();
		lineEndToRemove = null;
	}
}