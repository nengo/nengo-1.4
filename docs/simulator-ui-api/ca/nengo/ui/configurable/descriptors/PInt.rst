.. java:import:: ca.nengo.ui.configurable.panels IntegerPanel

PInt
====

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PInt extends RangedConfigParam

   Config Descriptor for Integers

   :author: Shu Wu

Constructors
------------
PInt
^^^^

.. java:constructor:: public PInt(String name)
   :outertype: PInt

   :param name: TODO

PInt
^^^^

.. java:constructor:: public PInt(String name, String description)
   :outertype: PInt

PInt
^^^^

.. java:constructor:: public PInt(String name, String description, int defaultValue)
   :outertype: PInt

PInt
^^^^

.. java:constructor:: public PInt(String name, int defaultValue)
   :outertype: PInt

PInt
^^^^

.. java:constructor:: public PInt(String name, int defaultvalue, int min, int max)
   :outertype: PInt

   :param name: TODO
   :param defaultvalue: TODO
   :param min: TODO
   :param max: TODO

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected IntegerPanel createInputPanel()
   :outertype: PInt

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<Integer> getTypeClass()
   :outertype: PInt

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PInt

