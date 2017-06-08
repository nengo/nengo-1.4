.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ItemEvent

.. java:import:: java.awt.event ItemListener

.. java:import:: javax.swing AbstractAction

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JDialog

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.ui.actions PlotFunctionAction

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PFunction

.. java:import:: ca.nengo.ui.configurable.descriptors.functions AbstractFn

.. java:import:: ca.nengo.ui.configurable.descriptors.functions ConfigurableFunction

.. java:import:: ca.nengo.ui.configurable.descriptors.functions FnAdvanced

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

FunctionPanel
=============

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public class FunctionPanel extends PropertyInputPanel

   Input Panel for editing an individual Function

   :author: Shu Wu

Constructors
------------
FunctionPanel
^^^^^^^^^^^^^

.. java:constructor:: public FunctionPanel(PFunction property, ConfigurableFunction[] functions)
   :outertype: FunctionPanel

   :param property: TODO
   :param functions: TODO

Methods
-------
getDescriptor
^^^^^^^^^^^^^

.. java:method:: @Override public PFunction getDescriptor()
   :outertype: FunctionPanel

getValue
^^^^^^^^

.. java:method:: @Override public Function getValue()
   :outertype: FunctionPanel

isValueSet
^^^^^^^^^^

.. java:method:: @Override public boolean isValueSet()
   :outertype: FunctionPanel

previewFunction
^^^^^^^^^^^^^^^

.. java:method:: protected void previewFunction()
   :outertype: FunctionPanel

   Previews the function

setParameters
^^^^^^^^^^^^^

.. java:method:: protected void setParameters(boolean resetValue)
   :outertype: FunctionPanel

   Sets up the function using the configurable Function wrapper

   :param resetValue: Whether to reset the ConfigurableFunction's value before editing

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: FunctionPanel

