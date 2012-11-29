/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "LongPanel.java". Description:
"Input panel for entering Longs

  @author Shu Wu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU
Public License license (the GPL License), in which case the provisions of GPL
License are applicable  instead of those above. If you wish to allow use of your
version of this file only under the terms of the GPL License and not to allow
others to use your version of this file under the MPL, indicate your decision
by deleting the provisions above and replace  them with the notice and other
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
 */

package ca.nengo.ui.configurable.panels;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.properties.PNumber;

/**
 * Input panel for entering Longs
 * 
 * @author Shu Wu
 */
public abstract class NumberPanel extends PropertyInputPanel {

    private JTextField tf;

	protected enum TextError {
		NoError (""),
		ValueNotSet ("Value not set"),
		InvalidFormat ("Invalid number format"),
		OutOfRange ("Out of valid range");
		
		private String message;
		
		private TextError(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
	}

    /**
     * @param property Property associated with this NumberPanel
     */
    public NumberPanel(PNumber property) {
        super(property);
        tf = new JTextField(10);
        tf.addFocusListener(new TextFieldFocusListener());
        add(tf);
    }
    
    @Override public PNumber getDescriptor() {
        return (PNumber) super.getDescriptor();
    }
    
    @Override public boolean isValueSet() {
		return (checkValue(getText()) == TextError.NoError);
	}
    
	/**
	 * Check if a string is valid as the value for this property, and set
	 * the appropriate status message.
	 * @param value the current text
	 * @return true if the text is valid, false otherwise
	 */
	protected abstract TextError checkValue(String value);

    @Override public void setValue(Object value) {
    	tf.setText(value.toString());
        checkValue(getText());
    }
    
    protected void valueUpdated() {
		TextError error = checkValue(getText());
		setStatusMsg(error.getMessage());
	}
	
    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tf.setEnabled(enabled);
    }
    
	protected String getText() {
		return tf.getText();
	}

    protected class TextFieldFocusListener implements FocusListener {

		@Override public void focusGained(FocusEvent e) {
		}

		@Override public void focusLost(FocusEvent e) {
			valueUpdated();
		}
	}

}