package ca.neo.ui.configurable;

import java.io.Serializable;

import javax.swing.text.MutableAttributeSet;

public class ConfigResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private final MutableAttributeSet properties;

	public ConfigResult(MutableAttributeSet properties) {
		super();
		this.properties = properties;
	}

	public Object getValue(Property obj) {
		return getValue(obj.getName());
	}

	/**
	 * @param name
	 *            of property
	 * @return the value of that property
	 */
	public Object getValue(String name) {
		return properties.getAttribute(name);
	}

}
