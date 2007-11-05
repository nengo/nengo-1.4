package ca.neo.ui.configurable.descriptors;

import java.util.Vector;

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

	private static final long serialVersionUID = 1L;

	private int myInputDimension;
	
	private boolean isInputDimensionEditable;

	public PFunction(String name, int inputDimension) {
		this(name, inputDimension, false);
	}

	public PFunction(String name, int inputDimension,
			boolean isInputDimensionEditable) {
		super(name);
		this.myInputDimension = inputDimension;
		this.isInputDimensionEditable = isInputDimensionEditable;
	}

	// public PFunction(String name, Object defaultValue) {
	// super(name, defaultValue);
	// }

	private AbstractConfigurableFunction[] createConfigurableFunctions() {
		Vector<AbstractConfigurableFunction> functions = new Vector<AbstractConfigurableFunction>();

		PInt pInputDimension = new PInt("Input Dimension", myInputDimension);
		pInputDimension.setEditable(isInputDimensionEditable);

		ReflectiveConfigurableFunction constantFunction = new ReflectiveConfigurableFunction(
				ConstantFunction.class,
				"Constant Function",
				new PropertyDescriptor[] { pInputDimension, new PFloat("Value") });
		functions.add(constantFunction);

		InterpretorFunction interpreterFunction = new InterpretorFunction(
				myInputDimension, false);

		
		functions.add(interpreterFunction);

		/*
		 * These functions can only have a input dimension of 1
		 */
		if (myInputDimension == 1) {
			ReflectiveConfigurableFunction fourierFunction = new ReflectiveConfigurableFunction(
					FourierFunction.class, "Fourier Function",
					new PropertyDescriptor[] { new PFloat("Fundamental"),
							new PFloat("Cutoff"), new PFloat("RMS"),
							new PLong("Seed") });

			ReflectiveConfigurableFunction gaussianPDF = new ReflectiveConfigurableFunction(
					GaussianPDF.class, "Guassian PDF",
					new PropertyDescriptor[] { new PFloat("Mean"),
							new PFloat("Variance"), new PFloat("Peak") });

			functions.add(fourierFunction);
			functions.add(gaussianPDF);
		}

		return functions.toArray(new AbstractConfigurableFunction[0]);
	}

	@Override
	protected FunctionPanel createInputPanel() {
		AbstractConfigurableFunction[] functions = createConfigurableFunctions();
		return new FunctionPanel(this, functions);
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
