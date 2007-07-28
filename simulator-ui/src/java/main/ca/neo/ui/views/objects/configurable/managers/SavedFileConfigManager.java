package ca.neo.ui.views.objects.configurable.managers;

import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.PropertiesDialog;

public class SavedFileConfigManager implements IConfigurationManager {
	String configFileName;

	public SavedFileConfigManager() {
		this(PropertiesDialog.DEFAULT_PROPERTY_FILE_NAME);

	}

	/**
	 * @param configFileName
	 *            name of the file containing a set of properties to be used in
	 *            constructing the model
	 */
	public SavedFileConfigManager(String configFileName) {
		this.configFileName = configFileName;

	}

	public void configure(IConfigurable configurable) {
		configurable.loadPropertiesFromFile(configFileName);
		configurable.completeConfiguration();
	}
}
