package ca.neo.ui.views.objects.properties;

import java.io.Serializable;

import javax.swing.JLabel;

/*
 * Enables type checking and adds styling meta data to a Property
 */
public abstract class PropertySchema implements Serializable {

	String name;

	public PropertySchema(String name) {
		super();
		this.name = name;

		
	}

	public String getName() {
		return name;
	}
	
	public abstract Class getTypeClass();

	public abstract String getTypeName();

	public abstract PropertyInputPanel createInputPanel();
	

	protected JLabel getLabel() {
		JLabel label = new JLabel(getName() + " (" + getTypeName() + ")");
		return label;
	}

}
