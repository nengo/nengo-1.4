.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

VisiblyMutable.NameChangeEvent
==============================

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public static interface NameChangeEvent extends Event
   :outertype: VisiblyMutable

   Encapsulates a change in the name of a VisiblyMutable object. This event means that no other changes to the object have occurred except for the name change.

   :author: Bryan Tripp

Methods
-------
getNewName
^^^^^^^^^^

.. java:method:: public String getNewName()
   :outertype: VisiblyMutable.NameChangeEvent

   :return: The new name of the object

getOldName
^^^^^^^^^^

.. java:method:: public String getOldName()
   :outertype: VisiblyMutable.NameChangeEvent

   :return: The previous name of the object

