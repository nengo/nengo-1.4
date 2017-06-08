.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

VisiblyMutableUtils
===================

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public class VisiblyMutableUtils

   Utility methods for VisiblyMutable objects.

   :author: Bryan Tripp

Methods
-------
changed
^^^^^^^

.. java:method:: public static void changed(VisiblyMutable vm, List<VisiblyMutable.Listener> listeners)
   :outertype: VisiblyMutableUtils

   Notifies listeners of a change to the given VisiblyMutable object.

   :param vm: The changed VisiblyMutable object
   :param listeners: List of things listening for changes

isValidName
^^^^^^^^^^^

.. java:method:: public static boolean isValidName(String name)
   :outertype: VisiblyMutableUtils

nameChanged
^^^^^^^^^^^

.. java:method:: public static void nameChanged(VisiblyMutable vm, String oldName, String newName, List<VisiblyMutable.Listener> listeners) throws StructuralException
   :outertype: VisiblyMutableUtils

   :param vm: The changed VisiblyMutable object
   :param oldName: The old (existing) name of the VisiblyMutable
   :param newName: The new (replacement) name of the VisiblyMutable
   :param listeners: List of things listening for changes
   :throws StructuralException: if the new name is invalid

nodeRemoved
^^^^^^^^^^^

.. java:method:: public static void nodeRemoved(VisiblyMutable vm, Node n, List<VisiblyMutable.Listener> listeners)
   :outertype: VisiblyMutableUtils

   Notifies listeners that a node has been removed within the given object.

   :param vm: The changed VisiblyMutable object
   :param n: The node that was removed
   :param listeners: List of things listening for changes

