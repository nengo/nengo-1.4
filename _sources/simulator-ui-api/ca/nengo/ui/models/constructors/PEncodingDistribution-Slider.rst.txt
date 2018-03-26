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

PEncodingDistribution.Slider
============================

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: static class Slider extends PropertyInputPanel
   :outertype: PEncodingDistribution

Constructors
------------
Slider
^^^^^^

.. java:constructor:: public Slider(Property property)
   :outertype: PEncodingDistribution.Slider

Methods
-------
getValue
^^^^^^^^

.. java:method:: @Override public Float getValue()
   :outertype: PEncodingDistribution.Slider

isValueSet
^^^^^^^^^^

.. java:method:: @Override public boolean isValueSet()
   :outertype: PEncodingDistribution.Slider

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: PEncodingDistribution.Slider

