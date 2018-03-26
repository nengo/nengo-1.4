.. java:import:: java.awt.geom Point2D

.. java:import:: java.lang.ref WeakReference

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: ca.nengo.ui.lib.world Droppable

.. java:import:: ca.nengo.ui.lib.world DroppableX

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

DragAction
==========

.. java:package:: ca.nengo.ui.lib.actions
   :noindex:

.. java:type:: public class DragAction extends ReversableAction

   Action which allows the dragging of objects by the selection handler to be done and undone. NOTE: Special care is taken of Window objects. These objects maintain their own Window state, and are thus immune to this action handler's undo action.

   :author: Shu Wu

Constructors
------------
DragAction
^^^^^^^^^^

.. java:constructor:: public DragAction(Collection<WorldObject> selectedObjects)
   :outertype: DragAction

   :param selectedObjects: Nodes before they are dragged. Their offset positions will be used as initial positions.

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: DragAction

dropNode
^^^^^^^^

.. java:method:: public static void dropNode(WorldObject node) throws UserCancelledException
   :outertype: DragAction

isObjectDragUndoable
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static boolean isObjectDragUndoable(WorldObject obj)
   :outertype: DragAction

   :param obj: Object whose drag is being undone
   :return: True, if Object's drag can be undone

isReversable
^^^^^^^^^^^^

.. java:method:: @Override protected boolean isReversable()
   :outertype: DragAction

setFinalPositions
^^^^^^^^^^^^^^^^^

.. java:method:: public void setFinalPositions()
   :outertype: DragAction

   Stores the final positions based on the node offsets... called from selection handler after dragging has ended

undo
^^^^

.. java:method:: @Override protected void undo() throws ActionException
   :outertype: DragAction

