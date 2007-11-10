package ca.neo.ui.configurable.descriptors;

import java.util.Vector;

import ca.neo.math.Function;
import ca.neo.math.impl.FourierFunction;
import ca.neo.math.impl.GaussianPDF;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.descriptors.functions.AbstractFn;
import ca.neo.ui.configurable.descriptors.functions.FnConstant;
import ca.neo.ui.configurable.descriptors.functions.FnCustom;
import ca.neo.ui.configurable.descriptors.functions.FnReflective;
import ca.neo.ui.configurable.panels.FunctionPanel;

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
		this(name, inputDimension, false, null);
	}

	public PFunction(String name, int inputDimension,
			boolean isInputDimensionEditable, Function defaultValue) {
		super(name, defaultValue);
		this.myInputDimension = inputDimension;
		this.isInputDimensionEditable = isInputDimensionEditable;
	}

	// public PFunction(String name, Object defaultValue) {
	// super(name, defaultValue);
	// }

	private AbstractFn[] createConfigurableFunctions() {
		Vector<AbstractFn> functions = new Vector<AbstractFn>();

		functions
				.add(new FnConstant(myInputDimension, isInputDimensionEditable));

		FnCustom interpreterFunction = new FnCustom(myInputDimension, false);

		functions.add(interpreterFunction);

		/*
		 * These functions can only have a input dimension of 1
		 */
		if (myInputDimension == 1) {
			FnReflective fourierFunction = new FnReflective(
					FourierFunction.class, "Fourier Function",
					new PropertyDescriptor[] { new PFloat("Fundamental"),
							new PFloat("Cutoff"), new PFloat("RMS"),
							new PLong("Seed") });

			FnReflective gaussianPDF = new FnReflective(
					GaussianPDF.class, "Guassian PDF",
					new PropertyDescriptor[] { new PFloat("Mean"),
							new PFloat("Variance"), new PFloat("Peak") });

			functions.add(fourierFunction);
			functions.add(gaussianPDF);
		}

		return functions.toArray(new AbstractFn[0]);
	}

	@Override
	protected FunctionPanel createInputPanel() {
		AbstractFn[] functions = createConfigurableFunctions();
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

	public int getInputDimension() {
		return myInputDimension;
	}

}
