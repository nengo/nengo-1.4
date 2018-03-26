.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.panels FloatPanel

PFloat
======

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PFloat extends Property

   Config Descriptor for Floats

   :author: Shu Wu

Constructors
------------
PFloat
^^^^^^

.. java:constructor:: public PFloat(String name)
   :outertype: PFloat

   :param name: TODO

PFloat
^^^^^^

.. java:constructor:: public PFloat(String name, String description)
   :outertype: PFloat

PFloat
^^^^^^

.. java:constructor:: public PFloat(String name, float defaultValue)
   :outertype: PFloat

   :param name: TODO
   :param defaultValue: TODO

PFloat
^^^^^^

.. java:constructor:: public PFloat(String name, String description, float defaultValue)
   :outertype: PFloat

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected PropertyInputPanel createInputPanel()
   :outertype: PFloat

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<Float> getTypeClass()
   :outertype: PFloat

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PFloat

