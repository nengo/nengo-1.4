package ca.neo.ui.configurable.descriptors;

/**
 * Config Descriptor for Longs
 * 
 * @author Shu Wu
 * 
 */
public class PTLong extends RangedConfigParam {

	private static final long serialVersionUID = 1L;

	public PTLong(String name) {
		super(name);
	}

	@Override
	public LongPanel createInputPanel() {
		return new LongPanel(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getTypeClass() {
		/*
		 * Return the primitive type
		 */
		return long.class;
	}

	@Override
	public String getTypeName() {
		return "Long";
	}

}