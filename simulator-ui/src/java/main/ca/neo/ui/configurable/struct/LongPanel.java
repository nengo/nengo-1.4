package ca.neo.ui.configurable.struct;

import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigParamInputPanel;

public class LongPanel extends ConfigParamInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextField tf;

	public LongPanel(PTLong property) {
		super(property);
	}

	@Override
	public PTLong getDescriptor() {
		return (PTLong) super.getDescriptor();
	}

	@Override
	public Long getValue() {

		Long longValue = new Long(tf.getText());
		return longValue;

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
			Long value = getValue();

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