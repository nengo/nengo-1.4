package ca.neo.ui.configurable.descriptors;

import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Input panel for entering floating point numbers
 * 
 * @author Shu Wu
 */
public class FloatPanel extends ConfigParamInputPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Text field component
	 */
	private JTextField tf;

	public FloatPanel(ConfigParamDescriptor property) {
		super(property);

		tf = new JTextField(10);
		addToPanel(tf);
	}

	@Override
	public Object getValue() {

		Float floatValue = new Float(tf.getText());
		return floatValue.floatValue();

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
