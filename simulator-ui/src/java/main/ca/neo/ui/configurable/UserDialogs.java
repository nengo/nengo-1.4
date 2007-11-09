package ca.neo.ui.configurable;

import ca.neo.ui.configurable.descriptors.PBoolean;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.configurable.descriptors.PString;
import ca.neo.ui.configurable.managers.UserConfigurer;

/**
 * Creates various dialogs and returns user results
 * 
 * @author Shu Wu
 */
public class UserDialogs {

	public static Float showDialogFloat(String dialogName, Float defaultValue)
			throws ConfigException {
		return (Float) showDialog("Config",
				new PFloat(dialogName, defaultValue));
	}

	public static Boolean showDialogBoolean(String dialogName,
			Boolean defaultValue) throws ConfigException {
		return (Boolean) showDialog("Config", new PBoolean(dialogName,
				defaultValue));
	}

	public static Integer showDialogInteger(String dialogName, int defaultValue)
			throws ConfigException {
		return (Integer) showDialog("Config",
				new PInt(dialogName, defaultValue));
	}

	public static String showDialogString(String dialogName, String defaultValue)
			throws ConfigException {
		return (String) showDialog("Config", new PString(dialogName,
				defaultValue));
	}

	public static Object showDialog(String dialogName,
			PropertyDescriptor descriptor) throws ConfigException {
		return showDialog(dialogName, new PropertyDescriptor[] { descriptor })
				.getProperty(descriptor);
	}

	public static PropertySet showDialog(String dialogName,
			PropertyDescriptor[] descriptors) throws ConfigException {
		UserMultiPropDialog dialog = new UserMultiPropDialog(dialogName,
				descriptors);

		return dialog.configureAndGetResult();

	}
}

/**
 * Creates a configuration dialog from configuration descriptors
 * 
 * @author Shu Wu
 */
class UserMultiPropDialog {
	private PropertyDescriptor[] configParameters;
	private PropertySet configResults;
	private String dialogName;

	public UserMultiPropDialog(String dialogName,
			PropertyDescriptor[] configParameters) {
		this.dialogName = dialogName;
		this.configParameters = configParameters;
	}

	public PropertySet configureAndGetResult() throws ConfigException {

		Configr myConfigurable = new Configr();
		UserConfigurer userConfigurer = new UserConfigurer(myConfigurable);

		userConfigurer.configureAndWait();
		return configResults;
	}

	private class Configr implements IConfigurable {

		public void completeConfiguration(PropertySet configParameters)
				throws ConfigException {
			configResults = configParameters;
		}

		public PropertyDescriptor[] getConfigSchema() {
			return configParameters;
		}

		public String getTypeName() {
			return dialogName;
		}

		public void preConfiguration(PropertySet props) throws ConfigException {
			// do nothing
		}

	}

}
