.. java:import:: java.util Collection

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

DroppableX
==========

.. java:package:: ca.nengo.ui.lib.world
   :noindex:

.. java:type:: public interface DroppableX

   An object which accepts a list of drop targets. It differs from IDroppable in that the Drag Handler does add any drop behavior.

   :author: Shu Wu

Methods
-------
droppedOnTargets
^^^^^^^^^^^^^^^^

.. java:method:: public void droppedOnTargets(Collection<WorldObject> targets) throws UserCancelledException
   :outertype: DroppableX

