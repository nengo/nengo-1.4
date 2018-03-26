.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

VisiblyMutable.NodeRemovedEvent
===============================

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public static interface NodeRemovedEvent extends Event
   :outertype: VisiblyMutable

   Encapsulates a "node removed" change in the VisiblyMutable object.

Methods
-------
getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: VisiblyMutable.NodeRemovedEvent

   :return: the node that has been removed

