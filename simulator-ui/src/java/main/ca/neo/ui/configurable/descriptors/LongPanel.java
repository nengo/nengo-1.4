package ca.neo.ui.configurable.descriptors;

import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Input panel for entering Longs
 * 
 * @author Shu Wu
 */
public class LongPanel extends ConfigParamInputPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tf;

	public LongPanel(CLong property) {
		super(property);
		initPanel();
	}

	@Override
	public CLong getDescriptor() {
		return (CLong) super.getDescriptor();
	}

	@Override
	public Long getValue() {

		Long longValue = new Long(tf.getText());
		return longValue;

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