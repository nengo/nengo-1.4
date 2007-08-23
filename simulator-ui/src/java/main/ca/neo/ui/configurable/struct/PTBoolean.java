package ca.neo.ui.configurable.struct;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ca.neo.ui.configurable.PropertyInputPanel;

public class PTBoolean extends PropDescriptor {


	private static final long serialVersionUID = 1L;

	public PTBoolean(String name) {
		super(name);
	}

	@Override
	public PropertyInputPanel createInputPanel() {
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

class BooleanInputPanel extends PropertyInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JCheckBox checkBox;

	public BooleanInputPanel(PropDescriptor property) {
		super(property);
	}

	@Override
	public Object getValue() {
		Boolean bool = checkBox.isSelected();
		return bool.booleanValue();
	}

	@Override
	public void init(JPanel panel) {
		checkBox = new JCheckBox();
		checkBox.setSelected(false);
		panel.add(checkBox);

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
