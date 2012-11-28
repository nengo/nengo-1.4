/*
The contents of this file are subject to the Mozilla Public License Version 1.1
(the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific
language governing rights and limitations under the License.

The Original Code is "PBoolean.java". Description:
"Config Descriptor for Booleans

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

package ca.nengo.ui.configurable.properties;

import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.nengo.math.impl.IndicatorPDF;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;

public class PIndicatorPDF extends Property {

    private static final long serialVersionUID = 1L;

    public PIndicatorPDF(String name, String description, float defMin, float defMax) {
        super(name, description, new IndicatorPDF(defMin, defMax));
    }
    
    @Override public IndicatorPDF getDefaultValue() {
    	return (IndicatorPDF) super.getDefaultValue();
    }

    @Override protected PropertyInputPanel createInputPanel() {
        return new Panel(this, getDefaultValue());
    }

    @Override public Class<IndicatorPDF> getTypeClass() {
        return IndicatorPDF.class;
    }

    private static class Panel extends PropertyInputPanel {
        JTextField highValue;
        JTextField lowValue;

        public Panel(Property property, IndicatorPDF defValue) {
            super(property);

            add(new JLabel("Low: "));
            lowValue = new JTextField(10);
            add(lowValue);

            add(new JLabel("High: "));
            highValue = new JTextField(10);
            add(highValue);
            
            if (defValue != null) {
            	setValue(defValue);
            }
        }

        @Override public Object getValue() {
            String minStr = lowValue.getText();
            String maxStr = highValue.getText();

            if (minStr == null || maxStr == null) {
                return null;
            }

            try {
                Float min = new Float(minStr);
                Float max = new Float(maxStr);

                return new IndicatorPDF(min, max);
            } catch (NumberFormatException e) {
            	setStatusMsg("invalid number format");
                return null;
            } catch (IllegalArgumentException e) {
            	setStatusMsg("low must be less than or equal to high");
                return null;
            }

        }

        @Override public boolean isValueSet() {
            if (getValue() != null) {
                return true;
            } else {
                return false;
            }

        }

        @Override public void setValue(Object value) {
            if (value instanceof IndicatorPDF) {
                IndicatorPDF pdf = (IndicatorPDF) value;
                lowValue.setText((new Float(pdf.getLow())).toString());
                highValue.setText((new Float(pdf.getHigh())).toString());
            }
        }
    }
}
