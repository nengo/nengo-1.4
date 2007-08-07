package ca.neo.ui.views.objects.configurable.struct;

import javax.swing.JPanel;
import javax.swing.JTextField;

import ca.neo.ui.views.objects.configurable.PropertyInputPanel;

public class PTFloat extends PropDescriptor {

	private static final long serialVersionUID = 1L;

	public PTFloat(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		// TODO Auto-generated method stub
		return new FloatInputPanel(this);
	}

	@Override
	public Class<Float> getTypeClass() {
		return Float.class;
	}

	@Override
	public String getTypeName() {
		return "Float";
	}

}

class FloatInputPanel extends PropertyInputPanel {
	JTextField tf;

	public FloatInputPanel(PropDescriptor property) {
		super(property);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(JPanel panel) {
		tf = new JTextField(10);

		panel.add(tf);

	}

	@Override
	public Object getValue() {

		Float floatValue = new Float(tf.getText());
		return floatValue.floatValue();

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
