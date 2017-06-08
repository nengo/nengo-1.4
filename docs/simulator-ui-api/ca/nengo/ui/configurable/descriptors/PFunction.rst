.. java:import:: java.util Vector

.. java:import:: ca.nengo.config ClassRegistry

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl FourierFunction

.. java:import:: ca.nengo.math.impl GaussianPDF

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors.functions ConfigurableFunction

.. java:import:: ca.nengo.ui.configurable.descriptors.functions FnAdvanced

.. java:import:: ca.nengo.ui.configurable.descriptors.functions FnConstant

.. java:import:: ca.nengo.ui.configurable.descriptors.functions FnCustom

.. java:import:: ca.nengo.ui.configurable.descriptors.functions FnReflective

.. java:import:: ca.nengo.ui.configurable.panels FunctionPanel

PFunction
=========

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PFunction extends Property

   Config Descriptor for Functions

   :author: Shu Wu

Constructors
------------
PFunction
^^^^^^^^^

.. java:constructor:: public PFunction(String name, int inputDimension)
   :outertype: PFunction

   :param name: TODO
   :param inputDimension: TODO

PFunction
^^^^^^^^^

.. java:constructor:: public PFunction(String name, int inputDimension, boolean isInputDimensionEditable, Function defaultValue)
   :outertype: PFunction

   :param name: TODO
   :param inputDimension: TODO
   :param isInputDimensionEditable: TODO
   :param defaultValue: TODO

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected FunctionPanel createInputPanel()
   :outertype: PFunction

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: PFunction

   :return: TODO

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<Function> getTypeClass()
   :outertype: PFunction

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PFunction

