package ca.neo.ui.configurable.managers;

import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.IConfigurable;

/**
 * Configuration manager which loads configuration from a saved file
 * 
 * @author Shu Wu
 * 
 */
public class FileConfigurer extends ConfigManager {
	/**
	 * Name of file to be loaded
	 */
	private String configFileName;

	/**
	 * Configures an object using default properties
	 * 
	 * @param configurable
	 *            Object to be configured
	 */
	public FileConfigurer(IConfigurable configurable) {
		this(configurable, UserTemplateConfigurer.DEFAULT_TEMPLATE_NAME);

	}

	/**
	 * @param configurable
	 *            Object to be configured
	 * @param configFileName
	 *            Name of configuration to use
	 */
	public FileConfigurer(IConfigurable configurable, String configFileName) {
		super(configurable);
		this.configFileName = configFileName;
	}

	@Override
	public void configureAndWait() throws ConfigException {
		loadPropertiesFromFile(configFileName);
		getConfigurable().completeConfiguration(
				new PropertySet(getProperties()));
	}

}
