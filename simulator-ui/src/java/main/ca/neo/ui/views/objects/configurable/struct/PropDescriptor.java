package ca.neo.ui.views.objects.configurable.struct;

import java.io.Serializable;

import javax.swing.JLabel;

import ca.neo.ui.views.objects.configurable.PropertyInputPanel;

/**
 * Describes a property of a IConfigurable object
 */
public abstract class PropDescriptor implements Serializable {

	String name;

	public PropDescriptor(String name) {
		super();
		this.name = name;

	}

	public abstract PropertyInputPanel createInputPanel();

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

}
