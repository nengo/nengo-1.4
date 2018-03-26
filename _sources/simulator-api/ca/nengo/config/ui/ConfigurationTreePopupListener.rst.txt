.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.awt.event MouseAdapter

.. java:import:: java.awt.event MouseEvent

.. java:import:: javax.swing JMenuItem

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPopupMenu

.. java:import:: javax.swing JTree

.. java:import:: javax.swing.tree TreePath

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config ListProperty

.. java:import:: ca.nengo.config MainHandler

.. java:import:: ca.nengo.config NamedValueProperty

.. java:import:: ca.nengo.config Property

.. java:import:: ca.nengo.config.ui ConfigurationTreeModel.Value

.. java:import:: ca.nengo.model StructuralException

ConfigurationTreePopupListener
==============================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class ConfigurationTreePopupListener extends MouseAdapter

   Creates a popup menu for configuration tree nodes, to allow refreshing, adding/setting/removing, etc. as appropriate.

   :author: Bryan Tripp

Constructors
------------
ConfigurationTreePopupListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigurationTreePopupListener(JTree tree, ConfigurationTreeModel model)
   :outertype: ConfigurationTreePopupListener

   :param tree: A tree that displays a Configuration
   :param model: TreeModel underlying the above tree

Methods
-------
mousePressed
^^^^^^^^^^^^

.. java:method:: @Override public void mousePressed(MouseEvent e)
   :outertype: ConfigurationTreePopupListener

mouseReleased
^^^^^^^^^^^^^

.. java:method:: @Override public void mouseReleased(MouseEvent e)
   :outertype: ConfigurationTreePopupListener

