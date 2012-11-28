package ca.nengo.ui.configurable.properties;

import javax.swing.JComboBox;

import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.Sign;

public class PSign extends Property {

    private static final long serialVersionUID = 1L;

    public PSign(String name, String description) {
        super(name, description, null);
    }

    @Override protected PropertyInputPanel createInputPanel() {
        return new Panel(this);
    }

    @Override public Class<?> getTypeClass() {
        return Sign.class;
    }

    private static class Panel extends PropertyInputPanel {
        private JComboBox comboBox;

        public Panel(Property property) {
            super(property);
            comboBox = new JComboBox(Sign.values());
            add(comboBox);
        }

        @Override public Sign getValue() {
            return (Sign) comboBox.getSelectedItem();
        }

        @Override public boolean isValueSet() {
            return true;
        }

        @Override public void setValue(Object value) {
            for (Sign item : Sign.values()) {
                if (value == item) {
                    comboBox.setSelectedItem(item);
                }
            }
        }
    }
}