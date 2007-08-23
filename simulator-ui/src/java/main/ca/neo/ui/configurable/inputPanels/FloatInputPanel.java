package ca.neo.ui.configurable.inputPanels;

import javax.swing.JPanel;
import javax.swing.JTextField;

import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.struct.PropDescriptor;

public class FloatInputPanel extends PropertyInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextField tf;

	public FloatInputPanel(PropDescriptor property) {
		super(property);
	}

	@Override
	public Object getValue() {

		Float floatValue = new Float(tf.getText());
		return floatValue.floatValue();

	}

	@Override
	public void init(JPanel panel) {
		tf = new JTextField(10);

		panel.add(tf);

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

	@Override
	public void setValue(Object value) {
		tf.setText(value.toString());

	}

}
