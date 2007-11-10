package ca.neo.ui.configurable.panels;

import javax.swing.JTextField;

import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;

/**
 * Input Panel for Strings
 * 
 * @author Shu Wu
 */
public class StringPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tf;

	public StringPanel(PropertyDescriptor property) {
		super(property);
		initPanel();
	}

	@Override
	public Object getValue() {
		return tf.getText();

	}

	private void initPanel() {
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
