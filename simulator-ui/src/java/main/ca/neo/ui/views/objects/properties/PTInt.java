package ca.neo.ui.views.objects.properties;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class PTInt extends PropertySchema {

	public PTInt(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		// TODO Auto-generated method stub
		return new IntegerInputPanel(this);
	}

	@Override
	public Class getTypeClass() {
		// TODO Auto-generated method stub
		return int.class;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Integer";
	}

}

class IntegerInputPanel extends PropertyInputPanel {
	JTextField tf;

	public IntegerInputPanel(PropertySchema property) {
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
		tf = new JTextField(10);

		panel.add(tf);

	}

	@Override
	public Object getValue() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

	}

	@Override
	public void setValue(Object value) {
		tf.setText(value.toString());

	}

	@Override
	public boolean isValueSet() {
		String textValue = tf.getText();

		if (textValue == null || textValue.compareTo("") == 0)
			return false;

		try {
			getValue();

		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

}
