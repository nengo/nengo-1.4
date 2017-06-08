.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.configurable.panels StringPanel

PString
=======

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PString extends Property

   Config Descriptor for Strings

   :author: Shu Wu

Constructors
------------
PString
^^^^^^^

.. java:constructor:: public PString(String name)
   :outertype: PString

PString
^^^^^^^

.. java:constructor:: public PString(String name, String description, String defaultValue)
   :outertype: PString

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected PropertyInputPanel createInputPanel()
   :outertype: PString

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<String> getTypeClass()
   :outertype: PString

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PString

