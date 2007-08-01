package ca.neo.ui.models.functions;

import ca.neo.math.impl.ConstantFunction;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;

public class FConstantFunction extends ConfigurableFunction {
	static final PropertyStructure[] propStruct = new PropertyStructure[] {
			new PTInt("Dimension"), new PTFloat("Value") };


	public Class getFunctionClass() {
		return ConstantFunction.class;
	}

	public PropertyStructure[] getPropertiesSchema() {
		return propStruct;
	}

	public String getTypeName() {
		return "Constant Function";
	}

}
