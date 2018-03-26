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

ObjectState
===========

.. java:package:: ca.nengo.ui.lib.actions
   :noindex:

.. java:type::  class ObjectState

   Stores UI state variables required to do and undo drag operations.

   :author: Shu Wu

Constructors
------------
ObjectState
^^^^^^^^^^^

.. java:constructor:: protected ObjectState(WorldObject initialParent, Point2D initialOffset)
   :outertype: ObjectState

Methods
-------
getFinalOffset
^^^^^^^^^^^^^^

.. java:method:: protected Point2D getFinalOffset()
   :outertype: ObjectState

getFinalParentRef
^^^^^^^^^^^^^^^^^

.. java:method:: protected WeakReference<WorldObject> getFinalParentRef()
   :outertype: ObjectState

getInitialOffset
^^^^^^^^^^^^^^^^

.. java:method:: protected Point2D getInitialOffset()
   :outertype: ObjectState

getInitialParentRef
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected WeakReference<WorldObject> getInitialParentRef()
   :outertype: ObjectState

setFinalState
^^^^^^^^^^^^^

.. java:method:: protected void setFinalState(WorldObject finalParent, Point2D finalOffset)
   :outertype: ObjectState

