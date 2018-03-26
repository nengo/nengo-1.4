.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Dialog

.. java:import:: java.awt Dimension

.. java:import:: java.awt FlowLayout

.. java:import:: java.awt Frame

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.awt.event KeyAdapter

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.lang.reflect Constructor

.. java:import:: java.lang.reflect InvocationTargetException

.. java:import:: java.lang.reflect Method

.. java:import:: java.lang.reflect ParameterizedType

.. java:import:: java.lang.reflect Type

.. java:import:: java.util Collection

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: javax.swing BorderFactory

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JEditorPane

.. java:import:: javax.swing JFrame

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JScrollPane

.. java:import:: javax.swing JTree

.. java:import:: javax.swing ToolTipManager

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl ListPropertyImpl

.. java:import:: ca.nengo.config.impl NamedValuePropertyImpl

.. java:import:: ca.nengo.config.impl SingleValuedPropertyImpl

.. java:import:: ca.nengo.config.ui AquaTreeUI

.. java:import:: ca.nengo.config.ui ConfigurationTreeCellEditor

.. java:import:: ca.nengo.config.ui ConfigurationTreeCellRenderer

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel

.. java:import:: ca.nengo.config.ui ConfigurationTreePopupListener

ConfigUtil
==========

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public class ConfigUtil

   Configuration-related utility methods.

   :author: Bryan Tripp

Methods
-------
configure
^^^^^^^^^

.. java:method:: public static void configure(Dialog owner, Object o)
   :outertype: ConfigUtil

   Shows a tree in which object properties can be edited.

   :param owner: Parent dialog in which to put GUI elements
   :param o: Object to investigate

configure
^^^^^^^^^

.. java:method:: public static void configure(Frame owner, Object o)
   :outertype: ConfigUtil

   Shows a tree in which object properties can be edited.

   :param owner: Parent frame in which to put GUI elements
   :param o: Object to investigate

createConfigurationPane
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static ConfigurationPane createConfigurationPane(Object o)
   :outertype: ConfigUtil

   Shows a tree in which object properties can be edited.

   :param o: The Object to configure
   :return: A Scroll Pane containing the configuration properties

defaultConfiguration
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static ConfigurationImpl defaultConfiguration(Object configurable)
   :outertype: ConfigUtil

   :param configurable: An Object
   :return: A default Configuration with properties of the object, based on reflection of the object's getters and setters.

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public static Configuration getConfiguration(Object configurable)
   :outertype: ConfigUtil

   :param configurable: An object
   :return: configurable.getConfiguration() : Configuration if such a method is defined for configurable, otherwise ConfigUtil.defaultConfiguration(configurable).

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public static Object getDefaultValue(Class<?> type)
   :outertype: ConfigUtil

   :param type: A class
   :return: If there is a ConfigurationHandler for the class, then getDefaultValue() from that handler, otherwise if there is a zero-arg constructor then the result of that constructor, otherwise null.

getPrimitiveWrapperClass
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static Class<?> getPrimitiveWrapperClass(Class<?> c)
   :outertype: ConfigUtil

   :param c: Any class
   :return: Either c or if c is a primitive class (eg Integer.TYPE), the corresponding wrapper class

showHelp
^^^^^^^^

.. java:method:: public static void showHelp(String text)
   :outertype: ConfigUtil

   Displays given text in a help window.

   :param text: Help text (html body)

stripSuffix
^^^^^^^^^^^

.. java:method:: public static String stripSuffix(String s, String suffix)
   :outertype: ConfigUtil

   :param s: A String
   :param suffix: Something that the string might end with
   :return: The string with the given suffix removed (if it was there)

