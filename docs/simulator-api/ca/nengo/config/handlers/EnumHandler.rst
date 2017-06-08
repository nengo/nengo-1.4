.. java:import:: java.awt Component

.. java:import:: java.util ArrayList

.. java:import:: java.util EnumSet

.. java:import:: java.util List

.. java:import:: javax.swing JComboBox

.. java:import:: javax.swing JComponent

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

EnumHandler
===========

.. java:package:: ca.nengo.config.handlers
   :noindex:

.. java:type:: public class EnumHandler extends BaseHandler

   ConfigurationHandler for SimulationMode values.

   :author: Bryan Tripp

Constructors
------------
EnumHandler
^^^^^^^^^^^

.. java:constructor:: public EnumHandler()
   :outertype: EnumHandler

   Defaults to type Enum with null default value.

EnumHandler
^^^^^^^^^^^

.. java:constructor:: public EnumHandler(Class<?> type, Enum<?> defaultValue)
   :outertype: EnumHandler

   :param type: Type handled by this handler
   :param defaultValue: Default value for this handler

Methods
-------
fromString
^^^^^^^^^^

.. java:method:: @Override public Object fromString(String s)
   :outertype: EnumHandler

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue(Class<?> c)
   :outertype: EnumHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getDefaultValue(java.lang.Class)`

getEditor
^^^^^^^^^

.. java:method:: @SuppressWarnings @Override public Component getEditor(Object o, ConfigurationChangeListener listener, JComponent parent)
   :outertype: EnumHandler

