.. java:import:: javax.swing JComboBox

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable ConfigSchemaImpl

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.descriptors PFunctionArray

.. java:import:: ca.nengo.ui.configurable.descriptors PString

.. java:import:: ca.nengo.ui.models.nodes.widgets UIDecodedOrigin

OriginSelector
==============

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type::  class OriginSelector extends Property

   Selects an available Node Origin

   :author: Shu Wu

Fields
------
origins
^^^^^^^

.. java:field::  String[] origins
   :outertype: OriginSelector

Constructors
------------
OriginSelector
^^^^^^^^^^^^^^

.. java:constructor:: public OriginSelector(String name, String description, String[] originNames)
   :outertype: OriginSelector

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected OriginInputPanel createInputPanel()
   :outertype: OriginSelector

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<String> getTypeClass()
   :outertype: OriginSelector

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: OriginSelector

