package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.panels.LongPanel;

/**
 * Config Descriptor for Longs
 * 
 * @author Shu Wu
 * 
 */
public class PLong extends RangedConfigParam {

	private static final long serialVersionUID = 1L;

	public PLong(String name) {
		super(name);
	}

	@Override
	protected LongPanel createInputPanel() {
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
