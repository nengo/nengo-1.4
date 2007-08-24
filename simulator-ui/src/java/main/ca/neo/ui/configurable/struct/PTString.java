package ca.neo.ui.configurable.struct;

import javax.swing.JTextField;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

public class PTString extends ConfigParamDescriptor {

	private static final long serialVersionUID = 1L;

	public PTString(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ConfigParamInputPanel createInputPanel() {
		// TODO Auto-generated method stub
		return new StringInputPanel(this);
	}

	@Override
	public Class<String> getTypeClass() {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Text";
	}

}

class StringInputPanel extends ConfigParamInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextField tf;

	public StringInputPanel(ConfigParamDescriptor property) {
		super(property);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getValue() {
		return tf.getText();

	}

	@Override
	public void initPanel() {
		// TODO Auto-generated method stub
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
