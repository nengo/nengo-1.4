.. java:import:: java.awt.event FocusEvent

.. java:import:: java.awt.event FocusListener

.. java:import:: javax.swing JTextField

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

PropertyTextPanel
=================

.. java:package:: ca.nengo.ui.configurable.panels
   :noindex:

.. java:type:: public abstract class PropertyTextPanel extends PropertyInputPanel

Fields
------
invalidFormatMessage
^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected String invalidFormatMessage
   :outertype: PropertyTextPanel

textField
^^^^^^^^^

.. java:field:: protected JTextField textField
   :outertype: PropertyTextPanel

   Text field component

valueNotSetMessage
^^^^^^^^^^^^^^^^^^

.. java:field:: protected String valueNotSetMessage
   :outertype: PropertyTextPanel

Constructors
------------
PropertyTextPanel
^^^^^^^^^^^^^^^^^

.. java:constructor:: public PropertyTextPanel(Property property, int columns)
   :outertype: PropertyTextPanel

Methods
-------
checkValue
^^^^^^^^^^

.. java:method:: protected abstract TextError checkValue(String value)
   :outertype: PropertyTextPanel

   Check if a string is valid as the value for this property, and set the appropriate status message.

   :param value: the current text
   :return: true if the text is valid, false otherwise

getText
^^^^^^^

.. java:method:: protected String getText()
   :outertype: PropertyTextPanel

isValueSet
^^^^^^^^^^

.. java:method:: public boolean isValueSet()
   :outertype: PropertyTextPanel

setEnabled
^^^^^^^^^^

.. java:method:: @Override public void setEnabled(boolean enabled)
   :outertype: PropertyTextPanel

setValue
^^^^^^^^

.. java:method:: @Override public void setValue(Object value)
   :outertype: PropertyTextPanel

valueUpdated
^^^^^^^^^^^^

.. java:method:: protected void valueUpdated()
   :outertype: PropertyTextPanel

