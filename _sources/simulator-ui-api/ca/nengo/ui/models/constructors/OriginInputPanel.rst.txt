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

OriginInputPanel
================

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type::  class OriginInputPanel extends PropertyInputPanel

   Swing component for selecting a origin

   :author: Shu Wu

Fields
------
origins
^^^^^^^

.. java:field::  String[] origins
   :outertype: OriginInputPanel

Constructors
------------
OriginInputPanel
^^^^^^^^^^^^^^^^

.. java:constructor:: public OriginInputPanel(OriginSelector property, String[] originNames)
   :outertype: OriginInputPanel

Methods
-------
getValue
^^^^^^^^

.. java:method:: @Override public String getValue()
   :outertype: OriginInputPanel

isValueSet
^^^^^^^^^^

.. java:method:: @Override public boolean isValueSet()
   :outertype: OriginInputPanel

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: OriginInputPanel

