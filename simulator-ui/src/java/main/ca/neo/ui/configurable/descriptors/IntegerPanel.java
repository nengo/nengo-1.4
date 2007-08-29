package ca.neo.ui.configurable.descriptors;

import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Input Panel for entering Integers
 * 
 * @author Shu Wu
 * 
 */
public class IntegerPanel extends ConfigParamInputPanel {
	private static final long serialVersionUID = 1L;

	private JTextField tf;

	public IntegerPanel(PTInt property) {
		super(property);
	}

	@Override
	public PTInt getDescriptor() {
		return (PTInt) super.getDescriptor();
	}

	@Override
	public Integer getValue() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

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
			Integer value = getValue();

			if (getDescriptor().isCheckRange()) {
				if (value > getDescriptor().getMax()
						|| value < getDescriptor().getMin()) {
					return false;
				}
			}

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