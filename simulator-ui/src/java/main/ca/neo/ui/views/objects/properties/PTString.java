package ca.neo.ui.views.objects.properties;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class PTString extends PropertySchema {

	public PTString(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Text";
	}

	@Override
	public Class getTypeClass() {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public PropertyInputPanel createInputPanel() {
		// TODO Auto-generated method stub
		return new StringInputPanel(this);
	}

}

class StringInputPanel extends PropertyInputPanel {
	JTextField tf;

	public StringInputPanel(PropertySchema property) {
		super(property);
		// TODO Auto-generated constructor stub
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
	public Object getValue() {
		return tf.getText();

	}

	@Override
	public void setValue(Object value) {
		tf.setText((String) value);

	}

	@Override
	public boolean isValueSet() {

		String text = (String) getValue();

		if (text != null && text.compareTo("") != 0)
			return true;
		else
			return false;

	}

}
