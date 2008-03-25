package ca.nengo.ui.models.constructors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.nengo.math.ApproximatorFactory;
import ca.nengo.math.impl.GradientDescentApproximator;
import ca.nengo.math.impl.WeightedCostApproximator;
import ca.nengo.model.Node;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.NodeFactory;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.model.nef.NEFEnsembleFactory;
import ca.nengo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.ConfigSchemaImpl;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.PropertyInputPanel;
import ca.nengo.ui.configurable.descriptors.PFloat;
import ca.nengo.ui.configurable.descriptors.PInt;
import ca.nengo.ui.configurable.descriptors.PNodeFactory;
import ca.nengo.ui.configurable.managers.UserConfigurer;
import ca.nengo.ui.configurable.managers.ConfigManager.ConfigMode;
import ca.nengo.ui.models.nodes.UINEFEnsemble;
import ca.nengo.util.VectorGenerator;
import ca.nengo.util.impl.RandomHypersphereVG;
import ca.nengo.util.impl.Rectifier;
import ca.shu.ui.lib.util.Util;

public class CNEFEnsemble extends ConstructableNode {
	static final Property pApproximator = new PApproximator("Decoding Sign");

	static final Property pDim = new PInt("Dimensions");

	static final Property pEncodingDistribution = new PEncodingDistribution("Encoding Distribution");
	static final Property pEncodingSign = new PSign("Encoding Sign");
	static final Property pNodeFactory = new PNodeFactory("Node Factory");
	static final Property pNumOfNodes = new PInt("Number of Nodes");

	/**
	 * Config descriptors
	 */
	static final ConfigSchemaImpl zConfig = new ConfigSchemaImpl(new Property[] { pNumOfNodes,
			pDim, pNodeFactory }, new Property[] { pApproximator, pEncodingDistribution,
			pEncodingSign });

	public CNEFEnsemble() {
		super();
	}

	protected Node createNode(ConfigResult prop, String name) {
		try {

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();
			Integer numOfNeurons = (Integer) prop.getValue(pNumOfNodes);
			Integer dimensions = (Integer) prop.getValue(pDim);

			/*
			 * Advanced properties, these may not necessarily be configued, so
			 */
			ApproximatorFactory approxFactory = (ApproximatorFactory) prop.getValue(pApproximator);
			NodeFactory nodeFactory = (NodeFactory) prop.getValue(pNodeFactory);
			Sign encodingSign = (Sign) prop.getValue(pEncodingSign);
			Float encodingDistribution = (Float) prop.getValue(pEncodingDistribution);

			if (nodeFactory != null) {
				ef.setNodeFactory(nodeFactory);
			}

			if (approxFactory != null) {
				ef.setApproximatorFactory(approxFactory);
			}

			if (encodingSign != null) {
				if (encodingDistribution == null) {
					encodingDistribution = 0f;
				}
				VectorGenerator vectorGen = new RandomHypersphereVG(true, 1, encodingDistribution);
				if (encodingSign == Sign.Positive) {
					vectorGen = new Rectifier(vectorGen, true);
				} else if (encodingSign == Sign.Negative) {
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

class PApproximator extends Property {

	private static final long serialVersionUID = 1L;

	private static final String TYPE_NAME = "Approximator Factory";

	public PApproximator(String name) {
		super(name);
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new Panel(this);
	}

	@Override
	public Class<?> getTypeClass() {
		return ApproximatorFactory.class;
	}

	@Override
	public String getTypeName() {
		return TYPE_NAME;
	}

	private static class Panel extends PropertyInputPanel {
		private static SignItem unconstrained = new SignItem("Unconstrained", Sign.Unconstrained);
		private static SignItem positive = new SignItem("Positive", Sign.Positive);
		private static SignItem negative = new SignItem("Negative", Sign.Negative);

		private static SignItem[] items = { unconstrained, positive, negative };

		private static final long serialVersionUID = 1L;

		private JComboBox comboBox;
		private JButton setButton;

		public Panel(Property property) {
			super(property);
			comboBox = new JComboBox(items);
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
			Sign sign = ((SignItem) comboBox.getSelectedItem()).getSign();

			if (sign != null) {
				if (sign == Sign.Positive || sign == Sign.Negative) {
					setButton.setEnabled(false);
					boolean positive = true;
					if (sign == Sign.Negative) {
						positive = false;
					}
					approximator = new GradientDescentApproximator.Factory(
							new GradientDescentApproximator.CoefficientsSameSign(positive), false);

				} else if (sign == Sign.Unconstrained) {
					setButton.setEnabled(true);
					approximator = new WeightedCostApproximator.Factory(noiseLevel, NSV);
				} else {
					Util.Assert(false, "Unsupported item");
				}

			}
		}

		/*
		 * These values are used to configure a WeightedCostApproximator
		 */
		private float noiseLevel = 0.1f;
		private int NSV = -1;

		private void configure() {
			try {
				Property pNoiseLevel = new PFloat("Noise level", noiseLevel);
				Property pNSV = new PInt("Number of Singular Values", NSV);
				ConfigResult result = UserConfigurer.configure(
						new Property[] { pNoiseLevel, pNSV }, TYPE_NAME, this.getDialogParent(),
						ConfigMode.STANDARD);

				noiseLevel = (Float) result.getValue(pNoiseLevel);
				NSV = (Integer) result.getValue(pNSV);
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

enum Sign {
	Negative, Positive, Unconstrained
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
		return Sign.class;
	}

	@Override
	public String getTypeName() {
		return "Sign";
	}

	private static class Panel extends PropertyInputPanel {

		private static SignItem[] items = { new SignItem("Unconstrained", Sign.Unconstrained),
				new SignItem("Positive", Sign.Positive), new SignItem("Negative", Sign.Negative) };

		private static final long serialVersionUID = 1L;

		private JComboBox comboBox;

		public Panel(Property property) {
			super(property);
			comboBox = new JComboBox(items);
			add(comboBox);
		}

		@Override
		public Sign getValue() {
			return ((SignItem) comboBox.getSelectedItem()).getSign();
		}

		@Override
		public boolean isValueSet() {
			return true;
		}

		@Override
		public void setValue(Object value) {
			for (SignItem item : items) {
				if (value == item.getSign()) {
					comboBox.setSelectedItem(item);
				}
			}
		}
	}

}

class SignItem {
	String name;
	Sign type;

	public SignItem(String name, Sign type) {
		super();
		this.name = name;
		this.type = type;
	}

	public Sign getSign() {
		return type;
	}

	@Override
	public String toString() {
		return name;
	}

}