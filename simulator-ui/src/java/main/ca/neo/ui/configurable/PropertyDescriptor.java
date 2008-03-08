package ca.neo.ui.configurable;

import java.io.Serializable;

import ca.shu.ui.lib.util.Util;

/**
 * Describes a configuration parameter of a IConfigurable object
 */
/**
 * @author Shu
 */
public abstract class PropertyDescriptor implements Serializable {

	private Object defaultValue = null;

	private boolean isEditable = true;

	private String name;

	/**
	 * @param name
	 *            Name to be given to the parameter
	 */
	public PropertyDescriptor(String name) {
		super();
		this.name = name;

	}

	/**
	 * @param name
	 *            Name to be given to the parameter
	 * @param defaultValue
	 *            Default value of this parameter
	 */
	public PropertyDescriptor(String name, Object defaultValue) {
		super();
		this.defaultValue = defaultValue;
		this.name = name;

	}

	/**
	 * @return UI Input panel which can be used for User Configuration of this
	 *         property, null if none exists
	 */
	protected abstract PropertyInputPanel createInputPanel();

	/**
	 * @return Default value of this parameter
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Gets the input panel.
	 */
	public PropertyInputPanel getInputPanel() {
		// Instantiate a new input panel for each call, this is ok.
		Util.Assert(!(!isEditable && (getDefaultValue() == null)),
				"An input panel cannot be disabled and have no default value");

		PropertyInputPanel inputPanel = createInputPanel();

		if (getDefaultValue() != null) {
			inputPanel.setValue(getDefaultValue());
		}
		inputPanel.setEnabled(isEditable);

		return inputPanel;
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
	public abstract Class<?> getTypeClass();

	/**
	 * @return A name given to the Class type of this parameter's value
	 */
	public abstract String getTypeName();

	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * Sets whether this property can be changed from its default value
	 * 
	 * @param bool
	 */
	public void setEditable(boolean bool) {
		isEditable = bool;
	}

	@Override
	public String toString() {
		return getTypeName();
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

}
