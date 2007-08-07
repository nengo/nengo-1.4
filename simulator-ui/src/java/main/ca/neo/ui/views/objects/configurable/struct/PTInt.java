package ca.neo.ui.views.objects.configurable.struct;

import javax.swing.JPanel;
import javax.swing.JTextField;

import ca.neo.ui.views.objects.configurable.PropertyInputPanel;

public class PTInt extends PropDescriptor {

	public PTInt(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		return new IntegerInputPanel(this);
	}

	@Override
	public Class getTypeClass() {
		// TODO Auto-generated method stub
		return int.class;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Integer";
	}

}

class IntegerInputPanel extends PropertyInputPanel {
	JTextField tf;

	public IntegerInputPanel(PTInt property) {
		super(property);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PTInt getType() {
		// TODO Auto-generated method stub
		return (PTInt) super.getType();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(JPanel panel) {
		// TODO Auto-generated method stub
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
