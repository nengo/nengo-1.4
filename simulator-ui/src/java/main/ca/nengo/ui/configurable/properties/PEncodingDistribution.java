package ca.nengo.ui.configurable.properties;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;

public class PEncodingDistribution extends Property {

    private static final long serialVersionUID = 1L;

    public PEncodingDistribution(String name, String description) {
        super(name, description, 0f);
    }

    @Override protected PropertyInputPanel createInputPanel() {
        return new Slider(this);
    }

    @Override public Class<Float> getTypeClass() {
        return Float.class;
    }

    static class Slider extends PropertyInputPanel {
        private static final int NUMBER_OF_TICKS = 1000;

        private JSlider sliderSwing;
        private JLabel sliderValueLabel;

        public Slider(Property property) {
            super(property);

            JPanel sliderPanel = new JPanel();
            sliderPanel.setLayout(new BorderLayout());
            add(sliderPanel);

            sliderSwing = new JSlider(0, NUMBER_OF_TICKS);
            sliderSwing.setPreferredSize(new Dimension(400, (int) sliderSwing.getPreferredSize()
                    .getHeight()));
            sliderPanel.add(sliderSwing, BorderLayout.NORTH);

            JPanel labelsPanel = new JPanel();
            sliderPanel.add(labelsPanel, BorderLayout.SOUTH);

            labelsPanel.setLayout(new BorderLayout());
            labelsPanel.add(new JLabel("0.0 - Evenly Distributed"), BorderLayout.WEST);
            sliderValueLabel = new JLabel();
            sliderValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            sliderValueLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            labelsPanel.add(sliderValueLabel, BorderLayout.CENTER);
            labelsPanel.add(new JLabel("Clustered on Axis - 1.0"), BorderLayout.EAST);
            updateSliderLabel();

            sliderSwing.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    updateSliderLabel();
                }
            });
        }

        private void updateSliderLabel() {
            DecimalFormat df = new DecimalFormat("0.000");

            sliderValueLabel.setText(" -" + df.format(getValue()) + "- ");
        }

        @Override public Float getValue() {
            return ((float) sliderSwing.getValue() / (float) NUMBER_OF_TICKS);
        }

        @Override public boolean isValueSet() {
            return true;
        }

        @Override public void setValue(Object value) {
            if (value instanceof Float) {
                sliderSwing.setValue((int) (((Float) value) * NUMBER_OF_TICKS));
            } else {
                throw new InvalidParameterException();
            }
        }

    }

}