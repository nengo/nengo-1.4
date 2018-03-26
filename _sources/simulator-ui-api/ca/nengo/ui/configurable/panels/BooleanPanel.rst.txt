.. java:import:: javax.swing JCheckBox

.. java:import:: javax.swing JLabel

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

BooleanPanel
============

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public class BooleanPanel extends PropertyInputPanel

   Input panel for entering Booleans

   :author: Shu Wu

Fields
------
checkBox
^^^^^^^^

.. java:field:: public JCheckBox checkBox
   :outertype: BooleanPanel

label
^^^^^

.. java:field:: public JLabel label
   :outertype: BooleanPanel

Constructors
------------
BooleanPanel
^^^^^^^^^^^^

.. java:constructor:: public BooleanPanel(Property property)
   :outertype: BooleanPanel

   :param property: TODO

Methods
-------
getValue
^^^^^^^^

.. java:method:: @Override public Object getValue()
   :outertype: BooleanPanel

initPanel
^^^^^^^^^

.. java:method:: public void initPanel()
   :outertype: BooleanPanel

isValueSet
^^^^^^^^^^

.. java:method:: @Override public boolean isValueSet()
   :outertype: BooleanPanel

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: BooleanPanel

