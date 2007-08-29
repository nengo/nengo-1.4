package ca.neo.ui.configurable.descriptors;

import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Input Panel for Strings
 * 
 * @author Shu Wu
 * 
 */
public class StringPanel extends ConfigParamInputPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tf;

	public StringPanel(ConfigParamDescriptor property) {
		super(property);
	}

	@Override
	public Object getValue() {
		return tf.getText();

	}

	@Override
	public void initPanel() {
		tf = new JTextField(10);

		addToPanel(tf);

	}

	@Override
	public boolean isValueSet() {

		String text = (String) getValue();

		if (text != null && text.compareTo("") != 0)
			return true;
		else
			return false;

	}

	@Override
	public void setValue(Object value) {
		tf.setText((String) value);

	}

}
