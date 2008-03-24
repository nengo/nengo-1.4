package ca.nengo.ui.configurable.descriptors;

import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.panels.BooleanPanel;

/**
 * Config Descriptor for Booleans
 * 
 * @author Shu Wu
 */
public class PBoolean extends Property {

	private static final long serialVersionUID = 1L;

	public PBoolean(String name) {
		super(name);
	}

	public PBoolean(String name, boolean defaultValue) {
		super(name, defaultValue);
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new BooleanPanel(this);
	}

	@Override
	public Class<Boolean> getTypeClass() {
		return Boolean.class;
	}

	@Override
	public String getTypeName() {
		return "Boolean";
	}

}
