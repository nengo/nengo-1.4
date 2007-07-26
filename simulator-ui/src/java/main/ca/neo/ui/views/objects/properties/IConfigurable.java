package ca.neo.ui.views.objects.properties;

import javax.swing.text.SimpleAttributeSet;

import ca.shu.ui.lib.world.INamedObject;

public interface IConfigurable extends INamedObject {
	public abstract PropertySchema[] getPropertiesSchema();

	public void setProperty(String name, Object value);

	public Object getProperty(String name);

	public void completeConfiguration();

	public void cancelConfiguration();

	public void savePropertiesToFile(String fileName);

	public void loadPropertiesFromFile(String fileName);

	// public String getTypeName();
}
