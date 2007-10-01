package ca.neo.ui.configurable.descriptors;

import javax.swing.JCheckBox;

import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;

/**
 * Input panel for entering Booleans
 * 
 * @author Shu Wu
 */
public class BooleanPanel extends PropertyInputPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JCheckBox checkBox;

	public BooleanPanel(PropertyDescriptor property) {
		super(property);
		initPanel();
	}

	@Override
	public Object getValue() {
		Boolean bool = checkBox.isSelected();
		return bool.booleanValue();
	}

	private void initPanel() {
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
		checkBox.setSelected((Boolean) value);
	}

}
