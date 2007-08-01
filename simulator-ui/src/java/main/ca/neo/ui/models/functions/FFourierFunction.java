package ca.neo.ui.models.functions;

import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.FourierFunction;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;

public class FFourierFunction extends ConfigurableFunction {
	static final PropertyStructure[] propStruct = new PropertyStructure[] {
			new PTFloat("Fundamental"), new PTFloat("Cutoff"),
			new PTFloat("RMS") };


	public Class getFunctionClass() {
		return FourierFunction.class;
	}

	public PropertyStructure[] getPropertiesSchema() {
		return propStruct;
	}

	public String getTypeName() {
		return "Fourier Function";
	}

}
