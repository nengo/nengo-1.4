package ca.neo.ui.models.functions;

import ca.neo.math.impl.ConstantFunction;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;

public class FConstantFunction extends ConfigurableFunction {
	static final PropDescriptor[] propStruct = new PropDescriptor[] {
			new PTInt("Dimension"), new PTFloat("Value") };


	public Class<ConstantFunction> getFunctionClass() {
		return ConstantFunction.class;
	}

	public PropDescriptor[] getConfigSchema() {
		return propStruct;
	}

	public String getTypeName() {
		return "Constant Function";
	}

}
