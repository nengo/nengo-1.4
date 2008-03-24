package ca.nengo.ui.configurable.panels;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;

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

	public BooleanPanel(Property property) {
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
		add(checkBox);
		add(new JLabel("Enable"));
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
