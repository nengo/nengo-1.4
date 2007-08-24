package ca.neo.ui.configurable.targets.functions;

import ca.neo.math.impl.ConstantFunction;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.struct.PTFloat;
import ca.neo.ui.configurable.struct.PTInt;

public class FConstantFunction extends ConfigurableFunction {
	static final ConfigParamDescriptor[] propStruct = new ConfigParamDescriptor[] {
			new PTInt("Dimension"), new PTFloat("Value") };

	public ConfigParamDescriptor[] getConfigSchema() {
		return propStruct;
	}

	@Override
	public Class<ConstantFunction> getFunctionClass() {
		return ConstantFunction.class;
	}

	public String getTypeName() {
		return "Constant Function";
	}

}
