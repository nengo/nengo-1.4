.. java:import:: java.awt Component

.. java:import:: java.awt.event MouseEvent

.. java:import:: java.util EventObject

.. java:import:: javax.swing DefaultCellEditor

.. java:import:: javax.swing JTextField

.. java:import:: javax.swing JTree

.. java:import:: javax.swing.tree TreePath

.. java:import:: ca.nengo.config MainHandler

.. java:import:: ca.nengo.config Property

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel.NullValue

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel.Value

ConfigurationTreeCellEditor
===========================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class ConfigurationTreeCellEditor extends DefaultCellEditor

   TreeCellEditor for configuration trees. Gets editor components for property values from MainHandler.

   :author: Bryan Tripp

Constructors
------------
ConfigurationTreeCellEditor
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigurationTreeCellEditor(JTree tree)
   :outertype: ConfigurationTreeCellEditor

   :param tree: Configuration tree to which this cell editor is to belong

Methods
-------
getTreeCellEditorComponent
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
   :outertype: ConfigurationTreeCellEditor

isCellEditable
^^^^^^^^^^^^^^

.. java:method:: @Override public boolean isCellEditable(EventObject e)
   :outertype: ConfigurationTreeCellEditor

