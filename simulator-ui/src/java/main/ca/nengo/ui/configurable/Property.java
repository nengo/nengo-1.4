package ca.nengo.ui.configurable;

import java.io.Serializable;

import ca.shu.ui.lib.util.Util;

/**
 * Describes a configuration parameter of a IConfigurable object
 */
/**
 * @author Shu
 */
public abstract class Property implements Serializable {

	private Object defaultValue = null;

	private String description;

	private boolean isEditable = true;
	private String name;

	public Property(String name) {
		this(name, null, null);
	}

	public Property(String name, Object defaultValue) {
		this(name, null, defaultValue);
	}

	public Property(String name, String description) {
		this(name, description, null);
	}

	/**
	 * @param name
	 *            Name to be given to the parameter
	 * @param description
	 *            Description of the parameter
	 * @param defaultValue
	 *            Default value of this parameter
	 */
	public Property(String name, String description, Object defaultValue) {
		super();
		this.description = description;
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

	public String getDescription() {
		return description;
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

	public String getTooltip() {
		String nodeDescription = "Type: " + getTypeName();

		if (description != null) {
			return "<html><b>" + nodeDescription + "</b><br>" + description + "</html>";
		} else {
			return nodeDescription;
		}
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

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDescription(String description) {
		this.description = description;
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

}
