package ca.neo.ui.configurable.panels;

import javax.swing.JTextField;

import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.descriptors.PLong;

/**
 * Input panel for entering Longs
 * 
 * @author Shu Wu
 */
public class LongPanel extends PropertyInputPanel {

	private static final long serialVersionUID = 1L;

	private JTextField tf;

	public LongPanel(PLong property) {
		super(property);
		initPanel();
	}

	@Override
	public PLong getDescriptor() {
		return (PLong) super.getDescriptor();
	}

	@Override
	public Long getValue() {

		Long longValue = new Long(tf.getText());
		return longValue;

	}

	private void initPanel() {
		tf = new JTextField(10);
		add(tf);
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