package ca.neo.ui.configurable.targets.functions;

import ca.neo.math.impl.FourierFunction;
import ca.neo.ui.configurable.struct.PTFloat;
import ca.neo.ui.configurable.struct.PropDescriptor;

public class FFourierFunction extends ConfigurableFunction {
	static final PropDescriptor[] propStruct = new PropDescriptor[] {
			new PTFloat("Fundamental"), new PTFloat("Cutoff"),
			new PTFloat("RMS") };


	public PropDescriptor[] getConfigSchema() {
		return propStruct;
	}

	public Class<FourierFunction> getFunctionClass() {
		return FourierFunction.class;
	}

	public String getTypeName() {
		return "Fourier Function";
	}

}
