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

ConfigUtil.ConfigurationPane
============================

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public static class ConfigurationPane extends JScrollPane
   :outertype: ConfigUtil

Constructors
------------
ConfigurationPane
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigurationPane(Object o)
   :outertype: ConfigUtil.ConfigurationPane

   :param o: Object to configure

Methods
-------
getCellRenderer
^^^^^^^^^^^^^^^

.. java:method:: public ConfigurationTreeCellRenderer getCellRenderer()
   :outertype: ConfigUtil.ConfigurationPane

   :return: Cell Renderer object

getTree
^^^^^^^

.. java:method:: public JTree getTree()
   :outertype: ConfigUtil.ConfigurationPane

   :return: Tree object

