package ca.neo.ui.views.objects.properties;

import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Enables type checking and adds styling meta data to a Property
 */
public abstract class PropertySchema {

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
