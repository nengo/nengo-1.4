.. java:import:: java.awt Component

.. java:import:: javax.swing Icon

.. java:import:: javax.swing JLabel

.. java:import:: javax.swing JTree

.. java:import:: javax.swing.tree DefaultTreeCellRenderer

.. java:import:: ca.nengo.config IconRegistry

.. java:import:: ca.nengo.config MainHandler

.. java:import:: ca.nengo.config Property

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel.Value

ConfigurationTreeCellRenderer
=============================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class ConfigurationTreeCellRenderer extends DefaultTreeCellRenderer

   Renderer for cells in a configuration tree.

   :author: Bryan Tripp

Methods
-------
getTreeCellRendererComponent
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   :outertype: ConfigurationTreeCellRenderer

