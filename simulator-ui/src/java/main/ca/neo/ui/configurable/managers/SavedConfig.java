package ca.neo.ui.configurable.managers;

import ca.neo.ui.configurable.IConfigurable;

public class SavedConfig extends ConfigManager {
	String configFileName;

	public SavedConfig(IConfigurable configurable) {
		this(configurable, UserTemplateConfig.DEFAULT_PROPERTY_FILE_NAME);
		init();
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

	@Override
	public void configureAndWait() {
		loadPropertiesFromFile(configFileName);
		getConfigurable().completeConfiguration(
				new PropertySet(getProperties()));
	}

	private void init() {
		configureAndWait();
	}

}
