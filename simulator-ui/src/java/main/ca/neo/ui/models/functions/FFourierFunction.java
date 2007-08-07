package ca.neo.ui.models.functions;

import ca.neo.math.impl.FourierFunction;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

public class FFourierFunction extends ConfigurableFunction {
	static final PropDescriptor[] propStruct = new PropDescriptor[] {
			new PTFloat("Fundamental"), new PTFloat("Cutoff"),
			new PTFloat("RMS") };


	public Class<FourierFunction> getFunctionClass() {
		return FourierFunction.class;
	}

	public PropDescriptor[] getConfigSchema() {
		return propStruct;
	}

	public String getTypeName() {
		return "Fourier Function";
	}

}
