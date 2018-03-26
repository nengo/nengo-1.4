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

ConfigurationTreeModel.Value
============================

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public static class Value
   :outertype: ConfigurationTreeModel

   A wrapper for property values: stores index and configuration (if applicable)

Constructors
------------
Value
^^^^^

.. java:constructor:: public Value(int index, Object object)
   :outertype: ConfigurationTreeModel.Value

   :param index: Property location
   :param object: Owner object

Methods
-------
getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: ConfigurationTreeModel.Value

   :return: Configuration object

getIndex
^^^^^^^^

.. java:method:: public int getIndex()
   :outertype: ConfigurationTreeModel.Value

   :return: Location

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: ConfigurationTreeModel.Value

   :return: Name

getObject
^^^^^^^^^

.. java:method:: public Object getObject()
   :outertype: ConfigurationTreeModel.Value

   :return: Owner

setIndex
^^^^^^^^

.. java:method:: public void setIndex(int index)
   :outertype: ConfigurationTreeModel.Value

   :param index: Location

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: ConfigurationTreeModel.Value

   :param name: Name

setObject
^^^^^^^^^

.. java:method:: public void setObject(Object o)
   :outertype: ConfigurationTreeModel.Value

   :param o: Owner

