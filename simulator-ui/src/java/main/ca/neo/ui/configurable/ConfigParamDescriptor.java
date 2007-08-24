package ca.neo.ui.configurable;

import java.io.Serializable;

/**
 * Describes a configuration parameter of a IConfigurable object
 */
/**
 * @author Shu
 * 
 */
public abstract class ConfigParamDescriptor implements Serializable {

	private Object defaultValue = null;

	private String name;

	/**
	 * @param name
	 *            Name to be given to the parameter
	 */
	public ConfigParamDescriptor(String name) {
		super();
		this.name = name;

	}

	/**
	 * @return UI Input panel which can be used for User Configuration of this
	 *         property, null if none exists
	 */
	public abstract ConfigParamInputPanel createInputPanel();

	/**
	 * @return Default value of this parameter
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return Name of this parameter
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Class type that this parameter's value must be
	 */
	@SuppressWarnings("unchecked")
	public abstract Class getTypeClass();

	/**
	 * @return A name given to the Class type of this parameter's value
	 */
	public abstract String getTypeName();

	/**
	 * @param defaultValue
	 *            Default value of this parameter
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}
