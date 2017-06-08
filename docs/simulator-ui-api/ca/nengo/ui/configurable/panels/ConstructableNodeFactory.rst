.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ItemEvent

.. java:import:: java.awt.event ItemListener

.. java:import:: java.lang.reflect Constructor

.. java:import:: javax.swing AbstractAction

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JTextField

.. java:import:: ca.nengo.config ClassRegistry

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron.impl ALIFNeuronFactory

.. java:import:: ca.nengo.model.neuron.impl LIFNeuronFactory

.. java:import:: ca.nengo.model.neuron.impl PoissonSpikeGenerator

.. java:import:: ca.nengo.model.neuron.impl PoissonSpikeGenerator.LinearNeuronFactory

.. java:import:: ca.nengo.model.neuron.impl PoissonSpikeGenerator.SigmoidNeuronFactory

.. java:import:: ca.nengo.model.neuron.impl SpikeGeneratorFactory

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuronFactory

.. java:import:: ca.nengo.model.neuron.impl SynapticIntegratorFactory

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PBoolean

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models.constructors AbstractConstructable

.. java:import:: ca.nengo.ui.models.constructors ModelFactory

ConstructableNodeFactory
========================

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: abstract class ConstructableNodeFactory extends AbstractConstructable

Fields
------
pInterceptDefault
^^^^^^^^^^^^^^^^^

.. java:field:: static final Property pInterceptDefault
   :outertype: ConstructableNodeFactory

pMaxRateDefault
^^^^^^^^^^^^^^^

.. java:field:: static final Property pMaxRateDefault
   :outertype: ConstructableNodeFactory

pTauRCDefault
^^^^^^^^^^^^^

.. java:field:: static final Property pTauRCDefault
   :outertype: ConstructableNodeFactory

pTauRefDefault
^^^^^^^^^^^^^^

.. java:field:: static final Property pTauRefDefault
   :outertype: ConstructableNodeFactory

Constructors
------------
ConstructableNodeFactory
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConstructableNodeFactory(String name, Class<? extends NodeFactory> type)
   :outertype: ConstructableNodeFactory

Methods
-------
configureModel
^^^^^^^^^^^^^^

.. java:method:: protected final Object configureModel(ConfigResult configuredProperties) throws ConfigException
   :outertype: ConstructableNodeFactory

createNodeFactory
^^^^^^^^^^^^^^^^^

.. java:method:: protected abstract NodeFactory createNodeFactory(ConfigResult configuredProperties) throws ConfigException
   :outertype: ConstructableNodeFactory

getType
^^^^^^^

.. java:method:: public Class<? extends NodeFactory> getType()
   :outertype: ConstructableNodeFactory

getTypeName
^^^^^^^^^^^

.. java:method:: public final String getTypeName()
   :outertype: ConstructableNodeFactory

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: ConstructableNodeFactory

