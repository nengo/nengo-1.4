package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.panels.IntegerPanel;

/**
 * Config Descriptor for Integers
 * 
 * @author Shu Wu
 * 
 */
public class PInt extends RangedConfigParam {

	private static final long serialVersionUID = 1L;

	public PInt(String name) {
		super(name);
	}

	public PInt(String name, int defaultValue) {
		super(name, defaultValue);
	}

	public PInt(String name, int defaultvalue, int min, int max) {
		super(name, defaultvalue, min, max);
	}

	@Override
	protected IntegerPanel createInputPanel() {
		return new IntegerPanel(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getTypeClass() {
		/*
		 * Return the primitive type... Integer values can always be cast as the
		 * primitive
		 */
		return int.class;
	}

	@Override
	public String getTypeName() {
		return "Integer";
	}

}
