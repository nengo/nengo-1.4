package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Config Descriptor for Booleans
 * 
 * @author Shu Wu
 * 
 */
public class PTBoolean extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public PTBoolean(String name) {
		super(name);
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
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
