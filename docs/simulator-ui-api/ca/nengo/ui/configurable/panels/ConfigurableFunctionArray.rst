.. java:import:: java.awt Container

.. java:import:: java.awt.event ActionEvent

.. java:import:: javax.swing AbstractAction

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JTextField

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable IConfigurable

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PFunction

.. java:import:: ca.nengo.ui.configurable.descriptors PFunctionArray

.. java:import:: ca.nengo.ui.configurable.managers UserConfigurer

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

ConfigurableFunctionArray
=========================

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type::  class ConfigurableFunctionArray implements IConfigurable

   :author: Shu

Constructors
------------
ConfigurableFunctionArray
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigurableFunctionArray(int inputDimension, int outputDimension, Function[] defaultValues)
   :outertype: ConfigurableFunctionArray

   :param inputDimension: TODO
   :param outputDimension: Number of functions to create
   :param defaultValues: TODO

Methods
-------
completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void completeConfiguration(ConfigResult properties)
   :outertype: ConfigurableFunctionArray

getDescription
^^^^^^^^^^^^^^

.. java:method:: public String getDescription()
   :outertype: ConfigurableFunctionArray

getExtendedDescription
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExtendedDescription()
   :outertype: ConfigurableFunctionArray

getFunctions
^^^^^^^^^^^^

.. java:method:: public Function[] getFunctions()
   :outertype: ConfigurableFunctionArray

   :return: Functions created

getSchema
^^^^^^^^^

.. java:method:: public ConfigSchema getSchema()
   :outertype: ConfigurableFunctionArray

getTypeName
^^^^^^^^^^^

.. java:method:: public String getTypeName()
   :outertype: ConfigurableFunctionArray

preConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public void preConfiguration(ConfigResult props) throws ConfigException
   :outertype: ConfigurableFunctionArray

