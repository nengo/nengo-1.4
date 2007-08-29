package ca.neo.ui.configurable.descriptors;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Config Descriptor for an Array of functions
 * 
 * @author Shu Wu
 * 
 */
public class CFunctionArray extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public CFunctionArray(String name) {
		super(name);
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
		return new FunctionArrayPanel(this);
	}

	@Override
	public Class<Function[]> getTypeClass() {
		return Function[].class;
	}

	@Override
	public String getTypeName() {
		return "Functions";
	}

}
