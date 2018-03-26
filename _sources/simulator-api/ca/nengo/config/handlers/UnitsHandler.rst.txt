.. java:import:: java.awt Component

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JComponent

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

.. java:import:: ca.nengo.model Units

UnitsHandler
============

.. java:package:: ca.nengo.config.handlers
   :noindex:

.. java:type:: public class UnitsHandler extends BaseHandler

   ConfigurationHandler for Units values.

   :author: Bryan Tripp

Constructors
------------
UnitsHandler
^^^^^^^^^^^^

.. java:constructor:: public UnitsHandler()
   :outertype: UnitsHandler

   ConfigurationHandler for Units values.

Methods
-------
fromString
^^^^^^^^^^

.. java:method:: @Override public Object fromString(String s)
   :outertype: UnitsHandler

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue(Class<?> c)
   :outertype: UnitsHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getDefaultValue(java.lang.Class)`

getEditor
^^^^^^^^^

.. java:method:: @Override public Component getEditor(Object o, ConfigurationChangeListener listener, JComponent parent)
   :outertype: UnitsHandler

