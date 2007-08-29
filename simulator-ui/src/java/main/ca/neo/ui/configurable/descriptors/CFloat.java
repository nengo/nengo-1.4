package ca.neo.ui.configurable.descriptors;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;


/**
 * Config Descriptor for Floats
 * 
 * @author Shu Wu
 * 
 */
public class CFloat extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public CFloat(String name) {
		super(name);
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
		return new FloatPanel(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getTypeClass() {
		/*
		 * Return the primitive type
		 */
		return float.class;
	}

	@Override
	public String getTypeName() {
		return "Float";
	}

}
