package ca.neo.ui.views.objects.configurable.struct;

import java.io.Serializable;

import javax.swing.JLabel;

import ca.neo.ui.views.objects.configurable.PropertyInputPanel;

/**
 * Statically describes a property 
 */
public abstract class PropertyStructure implements Serializable {

	String name;

	public PropertyStructure(String name) {
		super();
		this.name = name;

		
	}

	public String getName() {
		return name;
	}
	
	public abstract Class getTypeClass();

	public abstract String getTypeName();

	public abstract PropertyInputPanel createInputPanel();
	

	public JLabel getLabel() {
		JLabel label = new JLabel(getName() + " (" + getTypeName() + ")");
		return label;
	}

}
