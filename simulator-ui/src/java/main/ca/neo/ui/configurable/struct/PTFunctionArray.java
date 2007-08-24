package ca.neo.ui.configurable.struct;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

public class PTFunctionArray extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public PTFunctionArray(String name) {
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
