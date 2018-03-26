.. java:import:: ca.nengo.ui.configurable.descriptors PLong

LongPanel
=========

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public class LongPanel extends PropertyTextPanel

   Input panel for entering Longs

   :author: Shu Wu

Constructors
------------
LongPanel
^^^^^^^^^

.. java:constructor:: public LongPanel(PLong property)
   :outertype: LongPanel

Methods
-------
checkValue
^^^^^^^^^^

.. java:method:: @Override protected TextError checkValue(String textValue)
   :outertype: LongPanel

getDescriptor
^^^^^^^^^^^^^

.. java:method:: @Override public PLong getDescriptor()
   :outertype: LongPanel

getValue
^^^^^^^^

.. java:method:: @Override public Long getValue()
   :outertype: LongPanel

