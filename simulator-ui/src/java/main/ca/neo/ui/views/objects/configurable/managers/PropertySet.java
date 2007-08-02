package ca.neo.ui.views.objects.configurable.managers;

import javax.swing.text.MutableAttributeSet;

import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

public class PropertySet {
	MutableAttributeSet properties;

	public PropertySet(MutableAttributeSet properties) {
		super();
		this.properties = properties;
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

	public Object getProperty(PropDescriptor obj) {
		return getProperty(obj.getName());
	}

	
}
