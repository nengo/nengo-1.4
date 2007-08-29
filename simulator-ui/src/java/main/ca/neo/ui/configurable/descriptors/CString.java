package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Config Descriptor for Strings
 * 
 * @author Shu Wu
 * 
 */
public class CString extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public CString(String name) {
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
