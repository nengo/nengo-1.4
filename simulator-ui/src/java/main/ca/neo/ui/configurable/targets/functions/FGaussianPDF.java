package ca.neo.ui.configurable.targets.functions;

import ca.neo.math.impl.GaussianPDF;
import ca.neo.ui.configurable.struct.PTFloat;
import ca.neo.ui.configurable.struct.PropDescriptor;

public class FGaussianPDF extends ConfigurableFunction {
	static final PropDescriptor[] propStruct = new PropDescriptor[] {
			new PTFloat("Mean"), new PTFloat("Variance"), new PTFloat("Peak") };


	public PropDescriptor[] getConfigSchema() {
		return propStruct;
	}

	public Class<GaussianPDF> getFunctionClass() {
		return GaussianPDF.class;
	}

	public String getTypeName() {
		return "Gaussian PDF";
	}

}
