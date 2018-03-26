.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.panels FunctionArrayPanel

PFunctionArray
==============

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PFunctionArray extends Property

   Config Descriptor for an Array of functions

   :author: Shu Wu

Constructors
------------
PFunctionArray
^^^^^^^^^^^^^^

.. java:constructor:: public PFunctionArray(String name, int inputDimension)
   :outertype: PFunctionArray

   :param name: TODO
   :param inputDimension: TODO

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected PropertyInputPanel createInputPanel()
   :outertype: PFunctionArray

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<Function[]> getTypeClass()
   :outertype: PFunctionArray

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PFunctionArray

