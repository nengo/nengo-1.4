.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Dimension

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.security InvalidParameterException

.. java:import:: java.text DecimalFormat

.. java:import:: javax.swing AbstractAction

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JSlider

.. java:import:: javax.swing SwingConstants

.. java:import:: javax.swing.event ChangeEvent

.. java:import:: javax.swing.event ChangeListener

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math.impl GradientDescentApproximator

.. java:import:: ca.nengo.math.impl WeightedCostApproximator

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PFloat

.. java:import:: ca.nengo.ui.configurable.descriptors PInt

.. java:import:: ca.nengo.ui.configurable.descriptors PNodeFactory

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager.ConfigMode

.. java:import:: ca.nengo.ui.configurable.managers UserConfigurer

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.models.nodes UINEFEnsemble

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

.. java:import:: ca.nengo.util.impl Rectifier

CNEFEnsemble
============

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public class CNEFEnsemble extends ConstructableNode

Fields
------
pApproximator
^^^^^^^^^^^^^

.. java:field:: static final Property pApproximator
   :outertype: CNEFEnsemble

pDim
^^^^

.. java:field:: static final Property pDim
   :outertype: CNEFEnsemble

pEncodingDistribution
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: static final Property pEncodingDistribution
   :outertype: CNEFEnsemble

pEncodingSign
^^^^^^^^^^^^^

.. java:field:: static final Property pEncodingSign
   :outertype: CNEFEnsemble

pNodeFactory
^^^^^^^^^^^^

.. java:field:: static final Property pNodeFactory
   :outertype: CNEFEnsemble

pNoise
^^^^^^

.. java:field:: static final Property pNoise
   :outertype: CNEFEnsemble

pNumOfNodes
^^^^^^^^^^^

.. java:field:: static final Property pNumOfNodes
   :outertype: CNEFEnsemble

pRadius
^^^^^^^

.. java:field:: static final Property pRadius
   :outertype: CNEFEnsemble

zConfig
^^^^^^^

.. java:field:: static final ConfigSchemaImpl zConfig
   :outertype: CNEFEnsemble

   Config descriptors

Constructors
------------
CNEFEnsemble
^^^^^^^^^^^^

.. java:constructor:: public CNEFEnsemble()
   :outertype: CNEFEnsemble

Methods
-------
createNode
^^^^^^^^^^

.. java:method:: protected Node createNode(ConfigResult prop, String name)
   :outertype: CNEFEnsemble

getNodeConfigSchema
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public ConfigSchemaImpl getNodeConfigSchema()
   :outertype: CNEFEnsemble

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: CNEFEnsemble

