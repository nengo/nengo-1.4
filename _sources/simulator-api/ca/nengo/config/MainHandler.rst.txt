.. java:import:: java.awt Component

.. java:import:: java.io BufferedReader

.. java:import:: java.io File

.. java:import:: java.io FileReader

.. java:import:: java.io IOException

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: javax.swing JComponent

.. java:import:: javax.swing JLabel

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.config.handlers BooleanHandler

.. java:import:: ca.nengo.config.handlers EnumHandler

.. java:import:: ca.nengo.config.handlers FloatHandler

.. java:import:: ca.nengo.config.handlers IntegerHandler

.. java:import:: ca.nengo.config.handlers LongHandler

.. java:import:: ca.nengo.config.handlers MatrixHandler

.. java:import:: ca.nengo.config.handlers StringHandler

.. java:import:: ca.nengo.config.handlers VectorHandler

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel.NullValue

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.neuron.impl IzhikevichSpikeGenerator

MainHandler
===========

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public final class MainHandler implements ConfigurationHandler

   A composite ConfigurationHandler which delegates to other underlying ConfigurationHandlers that can handle specific classes.

   :author: Bryan Tripp

Fields
------
HANDLERS_FILE_PROPERTY
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static String HANDLERS_FILE_PROPERTY
   :outertype: MainHandler

   Java package with handlers

Methods
-------
addHandler
^^^^^^^^^^

.. java:method:: public void addHandler(ConfigurationHandler handler)
   :outertype: MainHandler

   :param handler: New handler to which the MainHandler can delegate

canHandle
^^^^^^^^^

.. java:method:: public boolean canHandle(Class<?> c)
   :outertype: MainHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.canHandle(java.lang.Class)`

fromString
^^^^^^^^^^

.. java:method:: public Object fromString(Class<?> c, String s)
   :outertype: MainHandler

   :param c: The class of the object represented by s
   :param s: A String representation of an object
   :return: x.fromString(s), where x is a handler appropriate for the class c

fromString
^^^^^^^^^^

.. java:method:: public Object fromString(String s)
   :outertype: MainHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.fromString(java.lang.String)`

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue(Class<?> c)
   :outertype: MainHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getDefaultValue(java.lang.Class)`

getEditor
^^^^^^^^^

.. java:method:: public Component getEditor(Object o, ConfigurationChangeListener listener, JComponent parent)
   :outertype: MainHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getEditor(Object,ConfigurationChangeListener,JComponent)`

getInstance
^^^^^^^^^^^

.. java:method:: public static synchronized MainHandler getInstance()
   :outertype: MainHandler

   :return: Singleton instance

getRenderer
^^^^^^^^^^^

.. java:method:: public Component getRenderer(Object o)
   :outertype: MainHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.getRenderer(java.lang.Object)`

toString
^^^^^^^^

.. java:method:: public String toString(Object o)
   :outertype: MainHandler

   **See also:** :java:ref:`ca.nengo.config.ConfigurationHandler.toString(java.lang.Object)`

