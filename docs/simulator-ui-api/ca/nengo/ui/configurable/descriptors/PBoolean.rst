.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.panels BooleanPanel

PBoolean
========

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PBoolean extends Property

   Config Descriptor for Booleans

   :author: Shu Wu

Constructors
------------
PBoolean
^^^^^^^^

.. java:constructor:: public PBoolean(String name)
   :outertype: PBoolean

   :param name: TODO

PBoolean
^^^^^^^^

.. java:constructor:: public PBoolean(String name, String description)
   :outertype: PBoolean

PBoolean
^^^^^^^^

.. java:constructor:: public PBoolean(String name, boolean defaultValue)
   :outertype: PBoolean

   :param name: TODO
   :param defaultValue: TODO

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected PropertyInputPanel createInputPanel()
   :outertype: PBoolean

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<Boolean> getTypeClass()
   :outertype: PBoolean

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PBoolean

setDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public void setDefaultValue(Boolean val)
   :outertype: PBoolean

