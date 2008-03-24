package ca.nengo.ui.configurable.descriptors;

import ca.nengo.math.Function;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.panels.FunctionArrayPanel;

/**
 * Config Descriptor for an Array of functions
 * 
 * @author Shu Wu
 */
public class PFunctionArray extends Property {

	private static final long serialVersionUID = 1L;

	private int inputDimension;

	public PFunctionArray(String name, int inputDimension) {
		super(name);
		this.inputDimension = inputDimension;
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new FunctionArrayPanel(this, inputDimension);
	}

	@Override
	public Class<Function[]> getTypeClass() {
		return Function[].class;
	}

	@Override
	public String getTypeName() {
		return "Functions";
	}

}
