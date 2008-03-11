package ca.neo.ui.configurable.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.neo.config.ClassRegistry;
import ca.neo.math.impl.IndicatorPDF;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.neuron.impl.ALIFNeuronFactory;
import ca.neo.model.neuron.impl.LIFNeuronFactory;
import ca.neo.model.neuron.impl.PoissonSpikeGenerator;
import ca.neo.model.neuron.impl.SpikeGeneratorFactory;
import ca.neo.model.neuron.impl.SpikingNeuronFactory;
import ca.neo.model.neuron.impl.SynapticIntegratorFactory;
import ca.neo.model.neuron.impl.PoissonSpikeGenerator.LinearNeuronFactory;
import ca.neo.model.neuron.impl.PoissonSpikeGenerator.SigmoidNeuronFactory;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.PropertyDescriptor;
import ca.neo.ui.configurable.PropertyInputPanel;
import ca.neo.ui.configurable.PropertySet;
import ca.neo.ui.configurable.descriptors.PBoolean;
import ca.neo.ui.configurable.descriptors.PFloat;
import ca.neo.ui.models.constructors.AbstractConstructable;
import ca.neo.ui.models.constructors.ModelFactory;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Input Panel for selecting and configuring a Node Factory
 * 
 * @author Shu Wu
 */
public class NodeFactoryPanel extends PropertyInputPanel {

	private static ConstructableNodeFactory[] NodeFactoryItems = new ConstructableNodeFactory[] {
			new CLinearNeuronFactory(), new CSigmoidNeuronFactory(), new CLIFNeuronFactory(),
			new CALIFNeuronFactory(), new CSpikingNeuronFactory() };

	private JComboBox factorySelector;

	private NodeFactory myNodeFactory;

	private ConstructableNodeFactory selectedItem;

	public NodeFactoryPanel(PropertyDescriptor property) {
		super(property);
		init();
	}

	private void configureNodeFactory() {
		selectedItem = (ConstructableNodeFactory) factorySelector.getSelectedItem();

		try {
			NodeFactory model = (NodeFactory) ModelFactory.constructModel(selectedItem);
			setValue(model);
		} catch (ConfigException e) {
			e.defaultHandleBehavior();
		} catch (Exception e) {
			UserMessages.showError("Could not configure Node Factory: " + e.getMessage());
		}
	}

	private void init() {

		factorySelector = new JComboBox(NodeFactoryItems);
		add(factorySelector);

		/*
		 * Reset value if the combo box selection has changed
		 */
		factorySelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (factorySelector.getSelectedItem() != selectedItem) {
					setValue(null);
				}
			}

		});

		JButton configureBtn = new JButton(new AbstractAction("Set") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				configureNodeFactory();
			}
		});

		add(configureBtn);

	}

	@Override
	public Object getValue() {
		return myNodeFactory;
	}

	@Override
	public boolean isValueSet() {
		if (myNodeFactory != null) {
			return true;
		} else {
			setStatusMsg("Node Factory must be set");
			return false;
		}
	}

	@Override
	public void setValue(Object value) {
		if (value == null) {
			myNodeFactory = null;
			return;
		}

		if (value instanceof NodeFactory) {
			myNodeFactory = (NodeFactory) value;
			setStatusMsg("");

			/*
			 * Update the combo box selector with the selected Node Factory
			 */
			boolean foundComboItem = false;
			for (int i = 0; i < NodeFactoryItems.length; i++) {

				if (NodeFactoryItems[i].getType().isInstance(myNodeFactory)) {
					selectedItem = NodeFactoryItems[i];
					factorySelector.setSelectedItem(selectedItem);
					foundComboItem = true;
					break;
				}
			}
			if (!foundComboItem)
				throw new IllegalArgumentException("Unsupported Node Factory");

		} else {
			throw new IllegalArgumentException("Value is not a Node Factory");
		}
	}
}

class CALIFNeuronFactory extends ConstructableNodeFactory {
	static final PropertyDescriptor pIncN = new PIndicatorPDF("IncN");
	static final PropertyDescriptor pIntercept = new PIndicatorPDF("Intercept");
	static final PropertyDescriptor pMaxRate = new PIndicatorPDF("Max rate");

	static final PropertyDescriptor pTauN = new PFloat("tauN");
	static final PropertyDescriptor pTauRC = new PFloat("tauRC");
	static final PropertyDescriptor pTauRef = new PFloat("tauRef");

	static final PropertyDescriptor[] zConfig = { pTauRC, pTauN, pTauRef, pMaxRate, pIntercept,
			pIncN };

	public CALIFNeuronFactory() {
		super("Adapting LIF Neuron", ALIFNeuronFactory.class);
	}

	@Override
	protected NodeFactory createNodeFactory(PropertySet configuredProperties) {
		Float tauRC = (Float) configuredProperties.getProperty(pTauRC);
		Float tauRef = (Float) configuredProperties.getProperty(pTauRef);
		Float tauN = (Float) configuredProperties.getProperty(pTauN);
		IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.getProperty(pMaxRate);
		IndicatorPDF intercept = (IndicatorPDF) configuredProperties.getProperty(pIntercept);
		IndicatorPDF incN = (IndicatorPDF) configuredProperties.getProperty(pIncN);

		return new ALIFNeuronFactory(maxRate, intercept, incN, tauRef, tauRC, tauN);
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

}

class CLIFNeuronFactory extends ConstructableNodeFactory {

	static final PropertyDescriptor pIntercept = new PIndicatorPDF("Intercept");
	static final PropertyDescriptor pMaxRate = new PIndicatorPDF("Max rate");

	static final PropertyDescriptor pTauRC = new PFloat("tauRC");
	static final PropertyDescriptor pTauRef = new PFloat("tauRef");
	static final PropertyDescriptor[] zConfig = { pTauRC, pTauRef, pMaxRate, pIntercept };

	public CLIFNeuronFactory() {
		super("LIF Neuron", LIFNeuronFactory.class);
	}

	@Override
	protected NodeFactory createNodeFactory(PropertySet configuredProperties) {
		Float tauRC = (Float) configuredProperties.getProperty(pTauRC);
		Float tauRef = (Float) configuredProperties.getProperty(pTauRef);
		IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.getProperty(pMaxRate);
		IndicatorPDF intercept = (IndicatorPDF) configuredProperties.getProperty(pIntercept);

		return new LIFNeuronFactory(tauRC, tauRef, maxRate, intercept);
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

}

/**
 * Constructable Linear Neuron Factory
 * 
 * @author Shu Wu
 */
class CLinearNeuronFactory extends ConstructableNodeFactory {
	static final PropertyDescriptor pIntercept = new PIndicatorPDF("Intercept");

	static final PropertyDescriptor pMaxRate = new PIndicatorPDF("Max rate");
	static final PropertyDescriptor pRectified = new PBoolean("Rectified");
	static final PropertyDescriptor[] zConfig = { pMaxRate, pIntercept, pRectified };

	public CLinearNeuronFactory() {
		super("Linear Neuron", LinearNeuronFactory.class);
	}

	@Override
	protected NodeFactory createNodeFactory(PropertySet configuredProperties) {
		IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.getProperty(pMaxRate);
		IndicatorPDF intercept = (IndicatorPDF) configuredProperties.getProperty(pIntercept);
		Boolean rectified = (Boolean) configuredProperties.getProperty(pRectified);

		LinearNeuronFactory factory = new PoissonSpikeGenerator.LinearNeuronFactory(maxRate,
				intercept, rectified);

		return factory;
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

}

abstract class ConstructableNodeFactory extends AbstractConstructable {
	private String name;
	private Class<? extends NodeFactory> type;

	public ConstructableNodeFactory(String name, Class<? extends NodeFactory> type) {
		super();
		this.name = name;
		this.type = type;
	}

	protected final Object configureModel(PropertySet configuredProperties) throws ConfigException {
		NodeFactory nodeFactory = createNodeFactory(configuredProperties);

		if (!getType().isInstance(nodeFactory)) {
			throw new ConfigException("Expected type: " + getType().getSimpleName() + " Got: "
					+ nodeFactory.getClass().getSimpleName());
		} else {
			return nodeFactory;
		}
	}

	abstract protected NodeFactory createNodeFactory(PropertySet configuredProperties)
			throws ConfigException;

	public Class<? extends NodeFactory> getType() {
		return type;
	}

	public final String getTypeName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}

class CSigmoidNeuronFactory extends ConstructableNodeFactory {

	static final PropertyDescriptor pInflection = new PIndicatorPDF("Inflection");

	static final PropertyDescriptor pMaxRate = new PIndicatorPDF("Max rate");
	static final PropertyDescriptor pSlope = new PIndicatorPDF("Slope");
	static final PropertyDescriptor[] zConfig = { pSlope, pInflection, pMaxRate };

	public CSigmoidNeuronFactory() {
		super("Sigmoid Neuron", SigmoidNeuronFactory.class);
	}

	@Override
	protected NodeFactory createNodeFactory(PropertySet configuredProperties) {
		IndicatorPDF slope = (IndicatorPDF) configuredProperties.getProperty(pSlope);
		IndicatorPDF inflection = (IndicatorPDF) configuredProperties.getProperty(pInflection);
		IndicatorPDF maxRate = (IndicatorPDF) configuredProperties.getProperty(pMaxRate);

		return new PoissonSpikeGenerator.SigmoidNeuronFactory(slope, inflection, maxRate);
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		return zConfig;
	}

}

/**
 * Customizable Neuron Factory Description Schema
 * 
 * @author Shu Wu
 */
class CSpikingNeuronFactory extends ConstructableNodeFactory {
	private static final PropertyDescriptor pBias = new PIndicatorPDF("bias");
	private static final PropertyDescriptor pScale = new PIndicatorPDF("scale");

	private static PListSelector getClassSelector(String selectorName, Class<?>[] classes) {
		ClassWrapper[] classWrappers = new ClassWrapper[classes.length];

		for (int i = 0; i < classes.length; i++) {
			classWrappers[i] = new ClassWrapper(classes[i]);
		}

		return new PListSelector(selectorName, classWrappers);
	}

	private PropertyDescriptor pSpikeGenerator;

	private PropertyDescriptor pSynapticIntegrator;

	public CSpikingNeuronFactory() {
		super("Customizable Neuron", SpikingNeuronFactory.class);
	}

	private Object constructFromClass(Class<?> type) throws ConfigException {
		try {
			Constructor<?> ct = type.getConstructor();
			try {
				return ct.newInstance();
			} catch (Exception e) {
				throw new ConfigException("Error constructing " + type.getSimpleName() + ": "
						+ e.getMessage());
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
			throw new ConfigException("Security Exception");
		} catch (NoSuchMethodException e1) {
			throw new ConfigException("Cannot find zero-arg constructor for: "
					+ type.getSimpleName());
		}
	}

	@Override
	protected NodeFactory createNodeFactory(PropertySet configuredProperties)
			throws ConfigException {
		Class<?> synapticIntegratorClass = ((ClassWrapper) configuredProperties
				.getProperty(pSynapticIntegrator)).getWrapped();
		Class<?> spikeGeneratorClass = ((ClassWrapper) configuredProperties
				.getProperty(pSpikeGenerator)).getWrapped();

		IndicatorPDF scale = (IndicatorPDF) configuredProperties.getProperty(pScale);
		IndicatorPDF bias = (IndicatorPDF) configuredProperties.getProperty(pBias);

		/*
		 * Construct Objects from Classes
		 */
		SynapticIntegratorFactory synapticIntegratorFactory = (SynapticIntegratorFactory) constructFromClass(synapticIntegratorClass);
		SpikeGeneratorFactory spikeGeneratorFactory = (SpikeGeneratorFactory) constructFromClass(spikeGeneratorClass);

		return new SpikingNeuronFactory(synapticIntegratorFactory, spikeGeneratorFactory, scale,
				bias);
	}

	@Override
	public PropertyDescriptor[] getConfigSchema() {
		/*
		 * Generate these descriptors Just-In-Time, to show all possible
		 * implementations in ClassRegistry
		 */
		pSynapticIntegrator = getClassSelector("Synaptic Integrator", ClassRegistry.getInstance()
				.getImplementations(SynapticIntegratorFactory.class).toArray(new Class<?>[] {}));
		pSpikeGenerator = getClassSelector("Spike Generator", ClassRegistry.getInstance()
				.getImplementations(SpikeGeneratorFactory.class).toArray(new Class<?>[] {}));

		return new PropertyDescriptor[] { pSynapticIntegrator, pSpikeGenerator, pScale, pBias };
	}

	/**
	 * Wraps a Class as a list item
	 */
	private static class ClassWrapper {
		Class<?> type;

		public ClassWrapper(Class<?> type) {
			super();
			this.type = type;
		}

		public Class<?> getWrapped() {
			return type;
		}

		@Override
		public String toString() {
			/*
			 * Return a name string that is at most two atoms long
			 */
			String canonicalName = type.getCanonicalName();
			String[] nameAtoms = canonicalName.split("\\.");
			if (nameAtoms.length > 2) {
				return nameAtoms[nameAtoms.length - 2] + "." + nameAtoms[nameAtoms.length - 1];

			} else {
				return canonicalName;
			}

		}
	}
}

class PIndicatorPDF extends PropertyDescriptor {

	private static final long serialVersionUID = 1L;

	public PIndicatorPDF(String name) {
		super(name);
	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new Panel(this);
	}

	@Override
	public Class<IndicatorPDF> getTypeClass() {
		return IndicatorPDF.class;
	}

	@Override
	public String getTypeName() {
		return "Indicator PDF";
	}

	private static class Panel extends PropertyInputPanel {
		JTextField highValue;
		JTextField lowValue;

		public Panel(PropertyDescriptor property) {
			super(property);

			add(new JLabel("Low: "));
			lowValue = new JTextField(10);
			add(lowValue);

			add(new JLabel("High: "));
			highValue = new JTextField(10);
			add(highValue);

		}

		@Override
		public Object getValue() {
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
				return null;
			}

		}

		@Override
		public boolean isValueSet() {
			if (getValue() != null) {
				return true;
			} else {
				return false;
			}

		}

		@Override
		public void setValue(Object value) {
			if (value instanceof IndicatorPDF) {
				IndicatorPDF pdf = (IndicatorPDF) value;
				lowValue.setText((new Float(pdf.getLow())).toString());
				highValue.setText((new Float(pdf.getHigh())).toString());
			}
		}
	}

}

class PListSelector extends PropertyDescriptor {
	private static final long serialVersionUID = 1L;

	private Object[] items;

	public PListSelector(String name, Object[] items) {
		super(name);
		this.items = items;

	}

	@Override
	protected PropertyInputPanel createInputPanel() {
		return new Panel(this, items);
	}

	@Override
	public Class<Object> getTypeClass() {
		return Object.class;
	}

	@Override
	public String getTypeName() {
		return "List";
	}

	private static class Panel extends PropertyInputPanel {
		private JComboBox comboBox;

		public Panel(PropertyDescriptor property, Object[] items) {
			super(property);
			comboBox = new JComboBox(items);
			add(comboBox);
		}

		@Override
		public Object getValue() {
			return comboBox.getSelectedItem();
		}

		@Override
		public boolean isValueSet() {
			return true;
		}

		@Override
		public void setValue(Object value) {
			comboBox.setSelectedItem(value);
		}

	}

}