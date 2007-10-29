package ca.neo.ui.configurable.descriptors;

import ca.neo.math.Function;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.FourierFunction;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.descriptors.functions.AbstractConfigurableFunction;
import ca.neo.ui.configurable.descriptors.functions.InterpretorFunction;
import ca.neo.ui.configurable.descriptors.functions.ReflectiveConfigurableFunction;

/**
 * Config Descriptor for Functions
 * 
 * @author Shu Wu
 */
public class PFunction extends PropertyDescriptor {

	private static final ReflectiveConfigurableFunction constantFunction = new ReflectiveConfigurableFunction(
			ConstantFunction.class, "Constant Function",
			new PropertyDescriptor[] { new PInt("Dimension"),
					new PFloat("Value") });

	private static final ReflectiveConfigurableFunction fourierFunction = new ReflectiveConfigurableFunction(
			FourierFunction.class,
			"Fourier Function",
			new PropertyDescriptor[] { new PFloat("Fundamental"),
					new PFloat("Cutoff"), new PFloat("RMS"), new PLong("Seed") });

	private static final ReflectiveConfigurableFunction gaussianPDF = new ReflectiveConfigurableFunction(
			GaussianPDF.class, "Guassiand PDF", new PropertyDescriptor[] {
					new PFloat("Mean"), new PFloat("Variance"),
					new PFloat("Peak") });

	private static final InterpretorFunction interpreterFunction = new InterpretorFunction();

	private static final long serialVersionUID = 1L;

	/**
	 * A list of configurable function wrappers
	 */
	public static final AbstractConfigurableFunction[] functions = new AbstractConfigurableFunction[] {
			constantFunction, fourierFunction, gaussianPDF, interpreterFunction };

	public PFunction(String name) {
		super(name);
	}

	public PFunction(String name, Object defaultValue) {
		super(name, defaultValue);
	}

	@Override
	public FunctionPanel createInputPanel() {
		return new FunctionPanel(this);
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
