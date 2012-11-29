package ca.nengo.ui.configurable.panels;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;

public abstract class PropertyTextPanel extends PropertyInputPanel {
	
	protected enum TextError {
		NoError, ValueNotSet, InvalidFormat
	}
	
	protected String valueNotSetMessage = "Value not set";
	protected String invalidFormatMessage = "Invalid number format";
	
    /**
     * Text field component
     */
	protected JTextField textField;

	public PropertyTextPanel(Property property, int columns) {
		super(property);
		
		textField = new JTextField(columns);
		textField.addFocusListener(new TextFieldFocusListener());
		add(textField);
	}
	
	protected String getText() {
		return textField.getText();
	}
	
	/**
	 * Check if a string is valid as the value for this property, and set
	 * the appropriate status message.
	 * @param value the current text
	 * @return true if the text is valid, false otherwise
	 */
	protected abstract TextError checkValue(String value);
	
	protected void valueUpdated() {
		TextError error = checkValue(getText());
		switch (error) {
		case ValueNotSet:
			setStatusMsg(valueNotSetMessage);
			break;
		case InvalidFormat:
			setStatusMsg(invalidFormatMessage);
			break;
		default:
			setStatusMsg("");
		}
	}
	
	public boolean isValueSet() {
		return (checkValue(getText()) == TextError.NoError);
	}
	
    @Override
    public void setValue(Object value) {
        textField.setText(value.toString());
        checkValue(getText());
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }
	
	protected class TextFieldFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			valueUpdated();
		}
	}
	

}
