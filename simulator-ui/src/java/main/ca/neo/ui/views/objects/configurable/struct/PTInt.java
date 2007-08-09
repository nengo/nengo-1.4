package ca.neo.ui.views.objects.configurable.struct;

import javax.swing.JPanel;
import javax.swing.JTextField;

import ca.neo.ui.views.objects.configurable.PropertyInputPanel;

public class PTInt extends PropDescriptor {

	private static final long serialVersionUID = 1L;

	public PTInt(String name) {
		super(name);
	}

	int min, max;
	boolean rangeEnabled = false;

	public PTInt(String name, int min, int max) {
		super(name + " min:" + min + " max:" + max);
		this.min = min;
		this.max = max;
		rangeEnabled = true;
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		return new IntegerInputPanel(this);
	}

	@Override
	public Class<Integer> getTypeClass() {
		return int.class;
	}

	@Override
	public String getTypeName() {
		return "Integer";
	}

}

class IntegerInputPanel extends PropertyInputPanel {
	JTextField tf;

	public IntegerInputPanel(PTInt property) {
		super(property);
	}

	@Override
	public PTInt getType() {
		return (PTInt) super.getType();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(JPanel panel) {
		tf = new JTextField(10);

		panel.add(tf);

	}

	@Override
	public Integer getValue() {

		Integer integerValue = new Integer(tf.getText());
		return integerValue.intValue();

	}

	@Override
	public void setValue(Object value) {
		tf.setText(value.toString());

	}

	@Override
	public boolean isValueSet() {
		String textValue = tf.getText();

		if (textValue == null || textValue.compareTo("") == 0)
			return false;

		try {
			Integer value = getValue();

			if (getType().rangeEnabled) {
				if (value > getType().max || value < getType().min) {
					return false;
				}
			}

		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

}
