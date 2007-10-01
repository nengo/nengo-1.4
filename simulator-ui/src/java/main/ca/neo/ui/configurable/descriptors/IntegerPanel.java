package ca.neo.ui.configurable.descriptors;

import javax.swing.JTextField;

import ca.neo.ui.configurable.PropertyInputPanel;

/**
 * Input Panel for entering Integers
 * 
 * @author Shu Wu
 */
public class IntegerPanel extends PropertyInputPanel {
	private static final long serialVersionUID = 1L;

	private JTextField tf;

	public IntegerPanel(PInt property) {
		super(property);
		initPanel();
	}

	@Override
	public PInt getDescriptor() {
		return (PInt) super.getDescriptor();
	}

	@Override
	public Integer getValue() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

	}

	private void initPanel() {
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