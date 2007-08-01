package ca.neo.ui.views.objects.configurable;

import javax.swing.text.SimpleAttributeSet;

import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;

public abstract class AbstractConfigurable implements IConfigurable {
	SimpleAttributeSet properties;

	boolean isConfigured = false;
	
	public boolean isConfigured() {
		return isConfigured;
	}

	public void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}

	public AbstractConfigurable() {
		super();

		properties = new SimpleAttributeSet();
	}

	public SimpleAttributeSet getPropertiesReference() {
		return properties;
	}

	public Object getProperty(String name) {
		return properties.getAttribute(name);
	}

	public Object getProperty(PropertyStructure prop) {
		return properties.getAttribute(prop.getName());
	}

	
	public void setProperties(SimpleAttributeSet properties) {
		this.properties = properties;
	}

	public void setProperty(String name, Object value) {

		getPropertiesReference().addAttribute(name, value);

	}
	
	

	public  void cancelConfiguration() {
		
	}

	public void completeConfiguration() {
		setConfigured(true);
	}

	public abstract PropertyStructure[] getPropertiesSchema();

	public abstract String getTypeName();
}
