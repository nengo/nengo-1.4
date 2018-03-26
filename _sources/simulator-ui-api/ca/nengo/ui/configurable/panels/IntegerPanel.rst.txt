.. java:import:: ca.nengo.ui.configurable.descriptors PInt

IntegerPanel
============

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public class IntegerPanel extends PropertyTextPanel

   Input Panel for entering Integers

   :author: Shu Wu

Constructors
------------
IntegerPanel
^^^^^^^^^^^^

.. java:constructor:: public IntegerPanel(PInt property)
   :outertype: IntegerPanel

Methods
-------
checkValue
^^^^^^^^^^

.. java:method:: @Override protected TextError checkValue(String textValue)
   :outertype: IntegerPanel

getDescriptor
^^^^^^^^^^^^^

.. java:method:: @Override public PInt getDescriptor()
   :outertype: IntegerPanel

getValue
^^^^^^^^

.. java:method:: @Override public Integer getValue()
   :outertype: IntegerPanel

