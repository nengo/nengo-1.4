package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;

/**
 * Config Descriptor for Strings
 * 
 * @author Shu Wu
 * 
 */
public class PString extends PropertyDescriptor {

	private static final long serialVersionUID = 1L;

	public PString(String name) {
		super(name);
	}

	public PString(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		return new StringPanel(this);
	}

	@Override
	public Class<String> getTypeClass() {
		return String.class;
	}

	@Override
	public String getTypeName() {
		return "Text";
	}

}
