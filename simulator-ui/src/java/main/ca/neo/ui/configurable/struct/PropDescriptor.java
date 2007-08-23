package ca.neo.ui.configurable.struct;

import java.io.Serializable;

import javax.swing.JLabel;

import ca.neo.ui.configurable.PropertyInputPanel;

/**
 * Describes a property of a IConfigurable object
 */
public abstract class PropDescriptor implements Serializable {

	Object defaultValue = null;

	String name;

	public PropDescriptor(String name) {
		super();
		this.name = name;

	}

	public abstract PropertyInputPanel createInputPanel();

	public Object getDefaultValue() {
		return defaultValue;
	}

	public JLabel getLabel() {
		JLabel label = new JLabel(getName() + " (" + getTypeName() + ")");
		return label;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public abstract Class getTypeClass();

	public abstract String getTypeName();

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return getTypeName();
	}

}
