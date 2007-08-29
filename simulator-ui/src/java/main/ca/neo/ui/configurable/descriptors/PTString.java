package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Config Descriptor for Strings
 * 
 * @author Shu Wu
 * 
 */
public class PTString extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public PTString(String name) {
		super(name);
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
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
