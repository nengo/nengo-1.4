package ca.neo.ui.configurable.targets.functions;

import ca.neo.math.impl.GaussianPDF;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.struct.PTFloat;

public class FGaussianPDF extends ConfigurableFunction {
	static final ConfigParamDescriptor[] propStruct = new ConfigParamDescriptor[] {
			new PTFloat("Mean"), new PTFloat("Variance"), new PTFloat("Peak") };

	public ConfigParamDescriptor[] getConfigSchema() {
		return propStruct;
	}

	@Override
	public Class<GaussianPDF> getFunctionClass() {
		return GaussianPDF.class;
	}

	public String getTypeName() {
		return "Gaussian PDF";
	}

}
