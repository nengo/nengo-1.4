package ca.neo.ui.configurable.struct;

import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

public class FloatPanel extends ConfigParamInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextField tf;

	public FloatPanel(ConfigParamDescriptor property) {
		super(property);
	}

	@Override
	public Object getValue() {

		Float floatValue = new Float(tf.getText());
		return floatValue.floatValue();

	}

	@Override
	public void initPanel() {
		tf = new JTextField(10);
		addToPanel(tf);

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
