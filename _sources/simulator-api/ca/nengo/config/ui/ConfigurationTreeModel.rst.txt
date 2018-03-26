.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util List

.. java:import:: javax.swing.event TreeModelEvent

.. java:import:: javax.swing.event TreeModelListener

.. java:import:: javax.swing.tree TreeModel

.. java:import:: javax.swing.tree TreePath

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configurable

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config ListProperty

.. java:import:: ca.nengo.config MainHandler

.. java:import:: ca.nengo.config NamedValueProperty

.. java:import:: ca.nengo.config Property

.. java:import:: ca.nengo.config SingleValuedProperty

.. java:import:: ca.nengo.model StructuralException

ConfigurationTreeModel
======================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class ConfigurationTreeModel implements TreeModel

   Data model underlying JTree user interface for a Configurable.

   :author: Bryan Tripp

Constructors
------------
ConfigurationTreeModel
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigurationTreeModel(Object configurable)
   :outertype: ConfigurationTreeModel

   :param configurable: Root of the configuration tree

Methods
-------
addTreeModelListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addTreeModelListener(TreeModelListener listener)
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.addTreeModelListener(javax.swing.event.TreeModelListener)`

addValue
^^^^^^^^

.. java:method:: public void addValue(TreePath parentPath, Object value, String name)
   :outertype: ConfigurationTreeModel

   :param parentPath: Path in configuration tree of a property to which a value is to be added
   :param value: New value to add
   :param name: Name of new value (only used if parent is a NamedValueProperty; can be null otherwise)

getChild
^^^^^^^^

.. java:method:: public Object getChild(Object parent, int index)
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.getChild(java.lang.Object,int)`

getChildCount
^^^^^^^^^^^^^

.. java:method:: public int getChildCount(Object parent)
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.getChildCount(java.lang.Object)`

getIndexOfChild
^^^^^^^^^^^^^^^

.. java:method:: public int getIndexOfChild(Object parent, Object child)
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.getIndexOfChild(java.lang.Object,java.lang.Object)`

getRoot
^^^^^^^

.. java:method:: public Object getRoot()
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.getRoot()`

insertValue
^^^^^^^^^^^

.. java:method:: public void insertValue(TreePath path, Object value)
   :outertype: ConfigurationTreeModel

   :param path: Path to the tree node to insert before
   :param value: Value to insert

isLeaf
^^^^^^

.. java:method:: public boolean isLeaf(Object o)
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.isLeaf(java.lang.Object)`

refresh
^^^^^^^

.. java:method:: public void refresh(TreePath path)
   :outertype: ConfigurationTreeModel

   :param path: Path to root of subtree to refresh

removeTreeModelListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeTreeModelListener(TreeModelListener listener)
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.removeTreeModelListener(javax.swing.event.TreeModelListener)`

removeValue
^^^^^^^^^^^

.. java:method:: public void removeValue(TreePath path)
   :outertype: ConfigurationTreeModel

   :param path: Tree path to property value to remove

setValue
^^^^^^^^

.. java:method:: public void setValue(TreePath path, Object value) throws StructuralException
   :outertype: ConfigurationTreeModel

   :param path: Path to object to be replaced with new value
   :param value: New value
   :throws StructuralException: if the setValue functions fail

valueForPathChanged
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void valueForPathChanged(TreePath path, Object newValue)
   :outertype: ConfigurationTreeModel

   **See also:** :java:ref:`javax.swing.tree.TreeModel.valueForPathChanged(javax.swing.tree.TreePath,java.lang.Object)`

