package ca.neo.ui.configurable.struct;

import javax.swing.JCheckBox;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

public class PTBoolean extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public PTBoolean(String name) {
		super(name);
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
		return new BooleanInputPanel(this);
	}

	@Override
	public Class<Boolean> getTypeClass() {
		return Boolean.class;
	}

	@Override
	public String getTypeName() {
		return "Boolean";
	}

}

class BooleanInputPanel extends ConfigParamInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JCheckBox checkBox;

	public BooleanInputPanel(ConfigParamDescriptor property) {
		super(property);
	}

	@Override
	public Object getValue() {
		Boolean bool = checkBox.isSelected();
		return bool.booleanValue();
	}

	@Override
	public void initPanel() {
		checkBox = new JCheckBox();
		checkBox.setSelected(false);
		addToPanel(checkBox);

	}

	@Override
	public boolean isValueSet() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		checkBox.setText(value.toString());

	}

}
