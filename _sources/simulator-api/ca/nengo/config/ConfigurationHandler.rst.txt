.. java:import:: java.awt Component

.. java:import:: javax.swing JComponent

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

ConfigurationHandler
====================

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public interface ConfigurationHandler

   Manages configuration of \ ``Property``\ s of of a certain Class. A ConfigurationHandler provides default property values as well as user interface components that can be used to display and edit a Property.

   Not all classes need a ConfigurationHandler. If a Property value does not have an associated handler, then a property editor must create a Configuration for it based on its accessor methods. This Configuration may in turn include property values for which there is no ConfigurationHandler, so that a tree of Configurations results. The leaves of this tree are the values with ConfigurationHandlers.

   :author: Bryan Tripp

Methods
-------
canHandle
^^^^^^^^^

.. java:method:: public boolean canHandle(Class<?> c)
   :outertype: ConfigurationHandler

   :param c: A Class
   :return: True if this handler can handle values of the given class

fromString
^^^^^^^^^^

.. java:method:: public Object fromString(String s)
   :outertype: ConfigurationHandler

   :param s: A String representation of an object, eg from toString(o) or user input
   :return: An object built from the given string

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue(Class<?> c)
   :outertype: ConfigurationHandler

   :param c: A class for which canHandle(c) == true
   :return: A default value of the given class

getEditor
^^^^^^^^^

.. java:method:: public Component getEditor(Object o, ConfigurationChangeListener listener, JComponent parent)
   :outertype: ConfigurationHandler

   :param o: An object for which canHandle(o.getClass()) == true
   :param listener: An ActionListener. The returned editor component must 1) add this listener to the part of itself that produces an event when editing is complete, and 2) call setProxy() with an EditorProxy through which the listener can retrieve a new object value when editing is complete
   :param parent: Parent component
   :return: A UI component (eg JTextField) that allows the user to change the object's value. If null, the calling property editor will attempt to create a default editor, possibly using fromString(...).

getRenderer
^^^^^^^^^^^

.. java:method:: public Component getRenderer(Object o)
   :outertype: ConfigurationHandler

   :param o: An object for which canHandle(o.getClass()) == true
   :return: A UI component (eg JLabel) that shows the object's value. If null, the calling property editor will attempt to create a default display, possibly using toString(o).

toString
^^^^^^^^

.. java:method:: public String toString(Object o)
   :outertype: ConfigurationHandler

   :param o: An object for which canHandle(o.getClass()) == true
   :return: A String representation of the object, suitable for user display

