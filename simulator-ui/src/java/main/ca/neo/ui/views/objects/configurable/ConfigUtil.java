package ca.neo.ui.views.objects.configurable;

import javax.swing.text.SimpleAttributeSet;

import ca.shu.ui.lib.util.Util;

public class ConfigUtil {

	public static void setProperty(IConfigurable configurable, String name,
			Object value) {
		configurable.getPropertiesReference().addAttribute(name, value);
	}

	public static Object getProperty(IConfigurable configurable, String name) {
		return configurable.getPropertiesReference().getAttribute(name);
	}

	public static void loadPropertiesFromFile(IConfigurable configurable,
			String fileName) {
		SimpleAttributeSet loadedProperties = (SimpleAttributeSet) Util
				.loadProperty(configurable, fileName);

		if (loadedProperties != null) {

			configurable.setProperties(loadedProperties);
		} else {
			Util.Error("Could not load file: " + fileName);
		}
	}

	public static String[] getPropertyFiles(IConfigurable configurable) {
		// TODO Auto-generated method stub
		return Util.getPropertyFiles(configurable);
	}

	public static void savePropertiesToFile(IConfigurable configurable,
			String fileName) {
		Util.saveProperty(configurable, configurable.getPropertiesReference(),
				fileName);

		// saveStatic(properties, fileName);
	}

	public static void deletePropretiesFile(IConfigurable configurable,
			String fileName) {
		Util.deleteProperty(configurable, fileName);
	}

}
