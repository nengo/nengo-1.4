.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

VisiblyMutable.Event
====================

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public static interface Event
   :outertype: VisiblyMutable

   Encapsulates a change to a VisiblyMutable object. The event doesn't specify which changes occurred, just the object that changed. Therefore all properties of the object that influence its display should be checked.

   :author: Bryan Tripp

Methods
-------
getObject
^^^^^^^^^

.. java:method:: public VisiblyMutable getObject()
   :outertype: VisiblyMutable.Event

   :return: An object that has changed in some way

