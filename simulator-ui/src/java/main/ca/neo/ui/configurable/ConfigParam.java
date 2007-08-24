package ca.neo.ui.configurable;

import javax.swing.text.MutableAttributeSet;


public class ConfigParam {
	MutableAttributeSet properties;

	public ConfigParam(MutableAttributeSet properties) {
		super();
		this.properties = properties;
	}

	public Object getProperty(ConfigParamDescriptor obj) {
		return getProperty(obj.getName());
	}

	/**
	 * 
	 * @param name
	 *            of property
	 * @return the value of that property
	 */
	public Object getProperty(String name) {
		return properties.getAttribute(name);
	}

}
