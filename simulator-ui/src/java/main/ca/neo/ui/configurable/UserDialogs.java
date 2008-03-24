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
		return (Float) showDialog("Config", new PFloat(dialogName, defaultValue));
	}

	public static Boolean showDialogBoolean(String dialogName, Boolean defaultValue)
			throws ConfigException {
		return (Boolean) showDialog("Config", new PBoolean(dialogName, defaultValue));
	}

	public static Integer showDialogInteger(String dialogName, int defaultValue)
			throws ConfigException {
		return (Integer) showDialog("Config", new PInt(dialogName, defaultValue));
	}

	public static String showDialogString(String dialogName, String defaultValue)
			throws ConfigException {
		return (String) showDialog("Config", new PString(dialogName, null, defaultValue));
	}

	public static Object showDialog(String dialogName, Property descriptor) throws ConfigException {
		return showDialog(dialogName, new Property[] { descriptor }).getValue(descriptor);
	}

	public static ConfigResult showDialog(String dialogName, Property[] descriptors)
			throws ConfigException {
		UserMultiPropDialog dialog = new UserMultiPropDialog(dialogName, descriptors);

		return dialog.configureAndGetResult();

	}
}

/**
 * Creates a configuration dialog from configuration descriptors
 * 
 * @author Shu Wu
 */
class UserMultiPropDialog {
	private Property[] propertiesSchema;
	private ConfigResult configResults;
	private String dialogName;

	public UserMultiPropDialog(String dialogName, Property[] configParameters) {
		this.dialogName = dialogName;
		this.propertiesSchema = configParameters;
	}

	public ConfigResult configureAndGetResult() throws ConfigException {

		Configr myConfigurable = new Configr();
		UserConfigurer userConfigurer = new UserConfigurer(myConfigurable);

		userConfigurer.configureAndWait();
		return configResults;
	}

	private class Configr implements IConfigurable {

		public void completeConfiguration(ConfigResult configParameters) throws ConfigException {
			configResults = configParameters;
		}

		public ConfigSchema getSchema() {
			return new ConfigSchemaImpl(propertiesSchema);
		}

		public String getTypeName() {
			return dialogName;
		}

		public void preConfiguration(ConfigResult props) throws ConfigException {
			// do nothing
		}

		public String getDescription() {
			return getTypeName();
		}

	}

}
