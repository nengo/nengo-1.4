package ca.neo.ui.views.objects.configurable.managers;

import ca.neo.ui.views.objects.configurable.IConfigurable;

public class SavedConfig extends ConfigManager {
	String configFileName;

	public SavedConfig(IConfigurable configurable) {
		this(configurable, UserConfig.DEFAULT_PROPERTY_FILE_NAME);
		init();
	}

	private void init() {
		configure();
	}

	/**
	 * @param configFileName
	 *            name of the file containing a set of properties to be used in
	 *            constructing the model
	 */
	public SavedConfig(IConfigurable configurable, String configFileName) {
		super(configurable);
		this.configFileName = configFileName;
	}

	public void configure() {
		loadPropertiesFromFile(configFileName);
		getConfigurable().completeConfiguration(
				new PropertySet(getProperties()));
	}

}
