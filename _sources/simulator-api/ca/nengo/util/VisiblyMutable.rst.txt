.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

VisiblyMutable
==============

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface VisiblyMutable

   An object that fires an event when its properties change in such a way that it expects the user interface to display it differently. This allows the user interface to update when the object is changed through another means, such as scripting.

   :author: Bryan Tripp

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: VisiblyMutable

   :param listener: Listener to add

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: VisiblyMutable

   :param listener: Listener to remove

