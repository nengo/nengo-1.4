.. java:import:: java.awt Component

.. java:import:: java.awt Dimension

.. java:import:: java.awt Font

.. java:import:: java.awt.event FocusEvent

.. java:import:: java.awt.event FocusListener

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: javax.swing JComponent

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JTextField

.. java:import:: javax.swing SwingConstants

.. java:import:: ca.nengo.config ConfigurationHandler

.. java:import:: ca.nengo.config IconRegistry

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

BaseHandler
===========

.. java:package:: ca.nengo.config.handlers
   :noindex:

.. java:type:: public abstract class BaseHandler implements ConfigurationHandler

   Base class that provides default behaviour for ConfigurationHandlers.

   :author: Bryan Tripp

Constructors
------------
BaseHandler
^^^^^^^^^^^

.. java:constructor:: public BaseHandler(Class<?> c)
   :outertype: BaseHandler

   :param c: Class of objects handled by this handler

Methods
-------
canHandle
^^^^^^^^^

.. java:method:: public boolean canHandle(Class<?> c)
   :outertype: BaseHandler

   :return: true if arg matches class given in constructor

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.canHandle(java.lang.Class)`

fromString
^^^^^^^^^^

.. java:method:: public Object fromString(String s)
   :outertype: BaseHandler

   :return: myClass.getConstructor(new Class[]{String.class}).newInstance(new Object[]{s})

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.fromString(java.lang.String)`

getEditor
^^^^^^^^^

.. java:method:: public Component getEditor(Object o, ConfigurationChangeListener listener, JComponent parent)
   :outertype: BaseHandler

   Returns a JTextField. An object is built from the text using fromString().

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getEditor(java.lang.Object,ConfigurationChangeListener,JComponent)`

getRenderer
^^^^^^^^^^^

.. java:method:: public Component getRenderer(Object o)
   :outertype: BaseHandler

   :return: null

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getRenderer(java.lang.Object)`

toString
^^^^^^^^

.. java:method:: public String toString(Object o)
   :outertype: BaseHandler

   :return: o.toString()

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.toString(java.lang.Object)`

