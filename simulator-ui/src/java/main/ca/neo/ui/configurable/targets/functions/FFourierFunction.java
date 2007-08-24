package ca.neo.ui.configurable.targets.functions;

import ca.neo.math.impl.FourierFunction;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.struct.PTFloat;

public class FFourierFunction extends ConfigurableFunction {
	static final ConfigParamDescriptor[] propStruct = new ConfigParamDescriptor[] {
			new PTFloat("Fundamental"), new PTFloat("Cutoff"),
			new PTFloat("RMS") };

	public ConfigParamDescriptor[] getConfigSchema() {
		return propStruct;
	}

	@Override
	public Class<FourierFunction> getFunctionClass() {
		return FourierFunction.class;
	}

	public String getTypeName() {
		return "Fourier Function";
	}

}
