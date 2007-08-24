package ca.neo.ui.configurable.struct;


public class PTInt extends RangedConfigParam {

	private static final long serialVersionUID = 1L;

	public PTInt(String name, int min, int max) {
		super(name, min, max);
	}

	public PTInt(String name) {
		super(name);
	}

	@Override
	public IntegerPanel createInputPanel() {
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
