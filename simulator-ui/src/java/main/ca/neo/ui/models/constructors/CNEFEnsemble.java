package ca.neo.ui.models.constructors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.neo.math.ApproximatorFactory;
import ca.neo.math.impl.GradientDescentApproximator;
import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.ui.configurable.ConfigResult;
import ca.neo.ui.configurable.ConfigSchemaImpl;
import ca.neo.ui.configurable.Property;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.descriptors.PInt;
import ca.neo.ui.configurable.descriptors.PNodeFactory;
import ca.neo.ui.models.constructors.PSign.SignType;
import ca.neo.ui.models.nodes.UINEFEnsemble;
import ca.neo.util.VectorGenerator;
import ca.neo.util.impl.RandomHypersphereVG;
import ca.neo.util.impl.Rectifier;

public class CNEFEnsemble extends ConstructableNode {
	static final Property pDecodingSign = new PSign("Decoding Sign");

	static final Property pDim = new PInt("Dimensions");

	static final Property pEncodingDistribution = new PEncodingDistribution("Encoding Distribution");
	static final Property pEncodingSign = new PSign("Encoding Sign");
	static final Property pNumOfNeurons = new PInt("Number of Neurons");
	static final Property pNodeFactory = new PNodeFactory("Node Factory");

	/**
	 * Config descriptors
	 */
	static final ConfigSchemaImpl zConfig = new ConfigSchemaImpl(new Property[] { pNumOfNeurons,
			pDim }, new Property[] { pDecodingSign, pEncodingDistribution, pEncodingSign,
			pNodeFactory });

	public CNEFEnsemble() {
		super();
	}

	protected Node createNode(ConfigResult prop, String name) {
		try {

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
			Integer numOfNeurons = (Integer) prop.getValue(pNumOfNeurons);
			Integer dimensions = (Integer) prop.getValue(pDim);

			/*
			 * Advanced properties, these may not necessarily be configued, so
			 */
			SignType decodingSign = (SignType) prop.getValue(pDecodingSign);
			NodeFactory nodeFactory = (NodeFactory) prop.getValue(pNodeFactory);
			SignType encodingSign = (SignType) prop.getValue(pEncodingSign);
			Float encodingDistribution = (Float) prop.getValue(pEncodingDistribution);

			if (decodingSign != null) {
				if (decodingSign == SignType.Positive || decodingSign == SignType.Negative) {
					boolean positive = true;
					if (decodingSign == SignType.Negative) {
						positive = false;
					}
					ApproximatorFactory approxFactory = new GradientDescentApproximator.Factory(
							new GradientDescentApproximator.CoefficientsSameSign(positive), false);
					ef.setApproximatorFactory(approxFactory);
				}
			}

			if (nodeFactory != null) {
				ef.setNodeFactory(nodeFactory);
			}

			if (encodingSign != null) {
				if (encodingDistribution == null) {
					encodingDistribution = 0f;
				}
				VectorGenerator vectorGen = new RandomHypersphereVG(true, 1, encodingDistribution);
				if (encodingSign == SignType.Positive) {
					vectorGen = new Rectifier(vectorGen, true);
				} else if (encodingSign == SignType.Negative) {
					vectorGen = new Rectifier(vectorGen, false);
				}
				ef.setEncoderFactory(vectorGen);
			}

			NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions);

			return ensemble;
		} catch (StructuralException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ConfigSchemaImpl getNodeConfigSchema() {
		return zConfig;
	}

	public String getTypeName() {
		return UINEFEnsemble.typeName;
	}

}

class PEncodingDistribution extends Property {

	private static final long serialVersionUID = 1L;

	public PEncodingDistribution(String name) {
		super(name, (Float) 0f);
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new Slider(this);
	}

	@Override
	public Class<Float> getTypeClass() {
		return Float.class;
	}

	@Override
	public String getTypeName() {
		return "Slider";
	}

	static class Slider extends PropertyInputPanel {
		private static final int NUMBER_OF_TICKS = 1000;
		private static final long serialVersionUID = 1L;

		private JSlider sliderSwing;
		private JLabel sliderValueLabel;

		public Slider(Property property) {
			super(property);

			JPanel sliderPanel = new JPanel();
			sliderPanel.setLayout(new BorderLayout());
			add(sliderPanel);

			sliderSwing = new JSlider(0, NUMBER_OF_TICKS);

			/*
			 * Add labels
			 */
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

		@Override
		public Float getValue() {
			return ((float) sliderSwing.getValue() / (float) NUMBER_OF_TICKS);
		}

		@Override
		public boolean isValueSet() {
			return true;
		}

		@Override
		public void setValue(Object value) {
			if (value instanceof Float) {
				sliderSwing.setValue((int) (((Float) value) * NUMBER_OF_TICKS));
			} else {
				throw new InvalidParameterException();
			}
		}

	}

}

class PSign extends Property {

	private static final long serialVersionUID = 1L;

	public PSign(String name) {
		super(name);
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new Panel(this);
	}

	@Override
	public Class<?> getTypeClass() {
		return SignType.class;
	}

	@Override
	public String getTypeName() {
		return "Sign";
	}

	private static class Panel extends PropertyInputPanel {

		private static SignItem[] items = { new SignItem("Unconstrained", SignType.Unconstrained),
				new SignItem("Positive", SignType.Positive),
				new SignItem("Negative", SignType.Negative) };

		private static final long serialVersionUID = 1L;

		private JComboBox comboBox;

		public Panel(Property property) {
			super(property);
			comboBox = new JComboBox(items);
			add(comboBox);
		}

		@Override
		public SignType getValue() {
			return ((SignItem) comboBox.getSelectedItem()).getType();
		}

		@Override
		public boolean isValueSet() {
			return true;
		}

		@Override
		public void setValue(Object value) {
			for (SignItem item : items) {
				if (value == item.getType()) {
					comboBox.setSelectedItem(item);
				}
			}
		}

		private static class SignItem {
			String name;
			SignType type;

			public SignItem(String name, SignType type) {
				super();
				this.name = name;
				this.type = type;
			}

			public SignType getType() {
				return type;
			}

			@Override
			public String toString() {
				return name;
			}

		}

	}

	public enum SignType {
		Negative, Positive, Unconstrained
	}

}