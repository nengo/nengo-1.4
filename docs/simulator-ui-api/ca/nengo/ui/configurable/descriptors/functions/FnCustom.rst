.. java:import:: java.awt Dialog

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.util Map

.. java:import:: javax.swing BorderFactory

.. java:import:: javax.swing Box

.. java:import:: javax.swing BoxLayout

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing.border EtchedBorder

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math FunctionInterpreter

.. java:import:: ca.nengo.math.impl DefaultFunctionInterpreter

.. java:import:: ca.nengo.math.impl PostfixFunction

.. java:import:: ca.nengo.ui.actions PlotFunctionAction

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable IConfigurable

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PFunction

.. java:import:: ca.nengo.ui.configurable.descriptors PInt

.. java:import:: ca.nengo.ui.configurable.descriptors PString

.. java:import:: ca.nengo.ui.configurable.managers ConfigDialog

.. java:import:: ca.nengo.ui.configurable.managers ConfigManager

.. java:import:: ca.nengo.ui.configurable.managers UserConfigurer

.. java:import:: ca.nengo.ui.configurable.panels StringPanel

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UserMessages

FnCustom
========

.. java:package:: ca.nengo.ui.configurable.descriptors.functions
   :noindex:

.. java:type:: public class FnCustom extends AbstractFn

   TODO

   :author: TODO

Fields
------
configurer
^^^^^^^^^^

.. java:field::  InterpreterFunctionConfigurer configurer
   :outertype: FnCustom

isInputDimEditable
^^^^^^^^^^^^^^^^^^

.. java:field::  boolean isInputDimEditable
   :outertype: FnCustom

Constructors
------------
FnCustom
^^^^^^^^

.. java:constructor:: public FnCustom(int inputDimensions, boolean isInputDimEditable)
   :outertype: FnCustom

   :param inputDimensions: TODO
   :param isInputDimEditable: TODO

Methods
-------
configureFunction
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Function configureFunction(Dialog parent)
   :outertype: FnCustom

createFunction
^^^^^^^^^^^^^^

.. java:method:: @Override protected Function createFunction(ConfigResult props) throws ConfigException
   :outertype: FnCustom

getFunction
^^^^^^^^^^^

.. java:method:: @Override public PostfixFunction getFunction()
   :outertype: FnCustom

getSchema
^^^^^^^^^

.. java:method:: public ConfigSchema getSchema()
   :outertype: FnCustom

preConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void preConfiguration(ConfigResult props) throws ConfigException
   :outertype: FnCustom

