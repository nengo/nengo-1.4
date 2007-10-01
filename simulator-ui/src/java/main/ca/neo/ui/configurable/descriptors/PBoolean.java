package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;

/**
 * Config Descriptor for Booleans
 * 
 * @author Shu Wu
 * 
 */
public class PBoolean extends PropertyDescriptor {

	private static final long serialVersionUID = 1L;

	public PBoolean(String name) {
		super(name);
	}

	public PBoolean(String name, boolean defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public PropertyInputPanel createInputPanel() {
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
