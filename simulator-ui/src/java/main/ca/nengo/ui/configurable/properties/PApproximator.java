package ca.nengo.ui.configurable.properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;

import ca.nengo.math.ApproximatorFactory;
import ca.nengo.math.impl.GradientDescentApproximator;
import ca.nengo.math.impl.WeightedCostApproximator;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.Sign;
import ca.nengo.ui.configurable.managers.UserConfigurer;
import ca.nengo.ui.configurable.managers.ConfigManager.ConfigMode;
import ca.nengo.ui.lib.util.Util;

public class PApproximator extends Property {

    private static final long serialVersionUID = 1L;

    public PApproximator(String name, String description) {
        super(name, description, null);
    }

    @Override protected PropertyInputPanel createInputPanel() {
        return new Panel(this);
    }

    @Override public Class<?> getTypeClass() {
        return ApproximatorFactory.class;
    }

    private static class Panel extends PropertyInputPanel {
        private JComboBox comboBox;
        private JButton setButton;
        private float noiseLevel = 0.1f;
        private int singularvalues = -1;
        
        public Panel(Property property) {
            super(property);
            comboBox = new JComboBox(Sign.values());
            setButton = new JButton(new AbstractAction("Set") {
                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    configure();
                }
            });

            add(comboBox);
            add(setButton);

            comboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateApproximator();
                }
            });
            updateApproximator();
        }

        private void updateApproximator() {
            Sign sign = (Sign) comboBox.getSelectedItem();

            if (sign != null) {
                if (sign == Sign.Positive || sign == Sign.Negative) {
                    setButton.setEnabled(false);
                    boolean positive = (sign == Sign.Positive);
                    approximator = new GradientDescentApproximator.Factory(
                            new GradientDescentApproximator.CoefficientsSameSign(positive), false);
                } else if (sign == Sign.Unconstrained) {
                    setButton.setEnabled(true);
                    approximator = new WeightedCostApproximator.Factory(noiseLevel, singularvalues);
                } else {
                    Util.Assert(false, "Unsupported item");
                }
            }
        }

        private void configure() {
            try {
                Property pNoiseLevel = new PFloat("Noise level",
                		"Ratio of the noise amplitude to the signal amplitude",
                		noiseLevel);
                Property pNSV = new PInt("Number of Singular Values",
                		"The Weighted Cost Approximator uses singular value " +
                		"decomposition to solve the optimization problem. " +
                		"This sets the number of singular values to use.",
                		singularvalues);
                Map<Property, Object> result = UserConfigurer.configure(
                        new Property[] { pNoiseLevel, pNSV }, "Approximator", this.getDialogParent(),
                        ConfigMode.STANDARD);

                noiseLevel = (Float) result.get(pNoiseLevel);
                singularvalues = (Integer) result.get(pNSV);
                updateApproximator();

            } catch (ConfigException e) {
                e.defaultHandleBehavior();
            }

        }

        @Override
        public ApproximatorFactory getValue() {
            return approximator;
        }

        @Override
        public boolean isValueSet() {
            return (getValue() != null);
        }

        private ApproximatorFactory approximator;

        @Override
        public void setValue(Object value) {
            // do nothing, values can't be set on this property
        }

    }

}