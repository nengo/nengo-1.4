package ca.neo.ui.views.objects.configurable.struct;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ca.neo.ui.views.objects.configurable.PropertyInputPanel;

public class PTBoolean extends PropDescriptor {

	public PTBoolean(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		// TODO Auto-generated method stub
		return new BooleanInputPanel(this);
	}

	@Override
	public Class getTypeClass() {
		// TODO Auto-generated method stub
		return boolean.class;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Boolean";
	}

}

class BooleanInputPanel extends PropertyInputPanel {
	JCheckBox checkBox;

	public BooleanInputPanel(PropDescriptor property) {
		super(property);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(JPanel panel) {
		// TODO Auto-generated method stub
		checkBox = new JCheckBox();
		checkBox.setSelected(false);
		panel.add(checkBox);

	}

	@Override
	public Object getValue() {
		Boolean bool = checkBox.isSelected();
		return bool.booleanValue();
	}

	@Override
	public void setValue(Object value) {
		checkBox.setText(value.toString());

	}

	@Override
	public boolean isValueSet() {
		return true;
	}

}
