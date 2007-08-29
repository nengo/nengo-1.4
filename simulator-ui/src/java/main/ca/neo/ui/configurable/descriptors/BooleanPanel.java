package ca.neo.ui.configurable.descriptors;

import javax.swing.JCheckBox;

import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.ConfigParamInputPanel;

/**
 * Input panel for entering Booleans
 * 
 * @author Shu Wu
 * 
 */
public class BooleanPanel extends ConfigParamInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JCheckBox checkBox;

	public BooleanPanel(ConfigParamDescriptor property) {
		super(property);
	}

	@Override
	public Object getValue() {
		Boolean bool = checkBox.isSelected();
		return bool.booleanValue();
	}

	@Override
	public void initPanel() {
		checkBox = new JCheckBox();
		checkBox.setSelected(false);
		addToPanel(checkBox);

	}

	@Override
	public boolean isValueSet() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		checkBox.setText(value.toString());

	}

}
