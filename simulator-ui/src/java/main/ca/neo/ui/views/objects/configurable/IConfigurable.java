package ca.neo.ui.views.objects.configurable;

import javax.swing.text.SimpleAttributeSet;

import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.world.INamedObject;

public interface IConfigurable extends INamedObject {
	public abstract PropertyStructure[] getPropertiesSchema();

	public void setProperty(String name, Object value);

	public Object getProperty(String name);

	public void completeConfiguration();

	public void cancelConfiguration();

	public void savePropertiesToFile(String fileName);
	
	public void deletePropretiesFile(String fileName);

	public String[] getPropertyFiles();

	public void loadPropertiesFromFile(String fileName);

	public abstract String getTypeName();

	// public String getTypeName();
}
