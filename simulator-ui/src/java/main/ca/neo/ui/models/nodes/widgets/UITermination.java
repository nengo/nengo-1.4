package ca.neo.ui.models.nodes.widgets;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TitlePart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.util.Configuration;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.lines.ILineEndHolder;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineEndHolderIcon;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * UI Wrapper for a Termination
 * 
 * @author Shu Wu
 */
public class UITermination extends Widget implements ILineEndHolder {

	private static final long serialVersionUID = 1L;

	private static String objToString(Object configValue) {

		if (Util.isArray(configValue)) {
			return Util.arrayToString(configValue);
		} else {
			return configValue.toString();
		}

	}

	private LineEnd lineEnd;

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
		ModelIcon icon = new ModelIcon(this, new LineEndHolderIcon());
		icon.configureLabel(false);

		setIcon(icon);
	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ConfigException {
		throw new NotImplementedException();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);
		if (lineEnd != null) {
			menu.addAction(new RemoveConnectionAction("Disconnect"));
		}

		MenuBuilder configureMenu = menu.createSubMenu("Configure Termination");

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
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);

		tooltips.addPart(new PropertyPart("Dimensions", ""
				+ getModel().getDimensions()));

		tooltips.addPart(new TitlePart("Configuration"));
		Configuration config = getModel().getConfiguration();
		String[] configProperties = config.listPropertyNames();
		for (String element : configProperties) {
			Object propertyValue = config.getProperty(element);

			tooltips.addPart(new PropertyPart(element,
					objToString(propertyValue)));
		}
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return null;
	}

	public LineEnd getLineEnd() {
		return lineEnd;
	}

	@Override
	public Termination getModel() {
		return (Termination) super.getModel();
	}

	public Termination getModelTermination() {
		return getModel();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.shu.ui.lib.objects.lines.ILineAcceptor#setLineEnd(ca.shu.ui.lib.objects.lines.LineEnd)
	 */
	public boolean setLineEnd(LineEnd lineEnd) {
		this.lineEnd = lineEnd;
		if (lineEnd != null) {
			addChild(lineEnd);
			this.lineEnd = lineEnd;

		}
		return true;
	}

	/**
	 * @param newWeights
	 *            Weights matrix to be assigned to this termination
	 */
	public void setWeights(float[][] newWeights) {
		try {
			getModel().getConfiguration().setProperty(Termination.WEIGHTS,
					newWeights);
			popupTransientMsg("Weights changed on Termination");
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

	/**
	 * Action for removing attached connection from the termination
	 * 
	 * @author Shu Wu
	 */
	class RemoveConnectionAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		public RemoveConnectionAction(String actionName) {
			super("Remove connection from Termination", actionName);
		}

		@Override
		protected void action() throws ActionException {
			lineEnd.destroy();
			lineEnd = null;
		}
	}
}
