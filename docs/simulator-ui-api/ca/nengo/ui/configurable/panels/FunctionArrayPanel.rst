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

FunctionArrayPanel
==================

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public class FunctionArrayPanel extends PropertyInputPanel

   Input panel for entering an Array of Functions

   :author: Shu Wu

Constructors
------------
FunctionArrayPanel
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public FunctionArrayPanel(PFunctionArray property, int inputDimension)
   :outertype: FunctionArrayPanel

   :param property: TODO
   :param inputDimension: TODO

Methods
-------
editFunctionArray
^^^^^^^^^^^^^^^^^

.. java:method:: protected void editFunctionArray()
   :outertype: FunctionArrayPanel

   Edits the Function Array using a child dialog

getDescriptor
^^^^^^^^^^^^^

.. java:method:: @Override public PFunctionArray getDescriptor()
   :outertype: FunctionArrayPanel

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: FunctionArrayPanel

   :return: TODO

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getOutputDimension()
   :outertype: FunctionArrayPanel

   :return: TODO

getValue
^^^^^^^^

.. java:method:: @Override public Function[] getValue()
   :outertype: FunctionArrayPanel

isOutputDimensionsSet
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isOutputDimensionsSet()
   :outertype: FunctionArrayPanel

   :return: True if Function Array dimensions has been set

isValueSet
^^^^^^^^^^

.. java:method:: @Override public boolean isValueSet()
   :outertype: FunctionArrayPanel

setDimensions
^^^^^^^^^^^^^

.. java:method:: public void setDimensions(int dimensions)
   :outertype: FunctionArrayPanel

   :param dimensions: Dimensions of the function array

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: FunctionArrayPanel

