package ca.neo.ui.configurable.struct;

import ca.neo.math.Function;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;
import ca.neo.ui.configurable.targets.functions.FunctionInputPanel;

public class PTFunction extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public PTFunction(String name) {
		super(name);
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
		return new FunctionInputPanel(this);
	}

	@Override
	public Class<Function> getTypeClass() {
		return Function.class;
	}

	@Override
	public String getTypeName() {
		return "Function";
	}

}
