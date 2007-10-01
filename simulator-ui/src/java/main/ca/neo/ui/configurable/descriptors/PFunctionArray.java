package ca.neo.ui.configurable.descriptors;

import ca.neo.math.Function;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;

/**
 * Config Descriptor for an Array of functions
 * 
 * @author Shu Wu
 * 
 */
public class PFunctionArray extends PropertyDescriptor {

	private static final long serialVersionUID = 1L;

	public PFunctionArray(String name) {
		super(name);
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		return new FunctionArrayPanel(this);
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
