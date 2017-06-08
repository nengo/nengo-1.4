.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

VisiblyMutable.Listener
=======================

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public static interface Listener
   :outertype: VisiblyMutable

   A listener for changes to a VisiblyMutable object.

   :author: Bryan Tripp

Methods
-------
changed
^^^^^^^

.. java:method:: public void changed(Event e) throws StructuralException
   :outertype: VisiblyMutable.Listener

   :param e: An object that has changed in some way (all properties that influence the display of the object should be checked)

