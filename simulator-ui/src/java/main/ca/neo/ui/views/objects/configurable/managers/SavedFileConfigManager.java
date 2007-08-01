package ca.neo.ui.views.objects.configurable.managers;

import javax.swing.text.SimpleAttributeSet;

import ca.neo.ui.views.objects.configurable.ConfigUtil;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.UIConfigManager;
import ca.shu.ui.lib.util.Util;

public class SavedFileConfigManager implements IConfigurationManager {
	String configFileName;

	public SavedFileConfigManager() {
		this(UIConfigManager.DEFAULT_PROPERTY_FILE_NAME);

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
		ConfigUtil.loadPropertiesFromFile(configurable, configFileName);
		configurable.completeConfiguration();
	}

	

}
