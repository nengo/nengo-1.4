.. java:import:: java.awt Cursor

.. java:import:: java.awt.geom Point2D

.. java:import:: java.util ArrayList

.. java:import:: java.util Iterator

.. java:import:: javax.swing SwingConstants

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PBounds

.. java:import:: edu.umd.cs.piccolo.util PDimension

.. java:import:: edu.umd.cs.piccolo.util PPickPath

.. java:import:: edu.umd.cs.piccolox.handles PHandle

.. java:import:: edu.umd.cs.piccolox.util PBoundsLocator

BoundsHandle
============

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type:: public class BoundsHandle extends PHandle

   \ **PBoundsHandle**\  a handle for resizing the bounds of another node. If a bounds handle is dragged such that the other node's width or height becomes negative then the each drag handle's locator assciated with that other node is "flipped" so that they are attached to and dragging a different corner of the nodes bounds.

   :author: Jesse Grosjean

Constructors
------------
BoundsHandle
^^^^^^^^^^^^

.. java:constructor:: public BoundsHandle(PBoundsLocator aLocator)
   :outertype: BoundsHandle

Methods
-------
addBoundsHandlesTo
^^^^^^^^^^^^^^^^^^

.. java:method:: public static void addBoundsHandlesTo(WorldObjectImpl wo)
   :outertype: BoundsHandle

addStickyBoundsHandlesTo
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void addStickyBoundsHandlesTo(PNode aNode, PCamera camera)
   :outertype: BoundsHandle

dragHandle
^^^^^^^^^^

.. java:method:: @Override public void dragHandle(PDimension aLocalDimension, PInputEvent aEvent)
   :outertype: BoundsHandle

endHandleDrag
^^^^^^^^^^^^^

.. java:method:: @Override public void endHandleDrag(Point2D aLocalPoint, PInputEvent aEvent)
   :outertype: BoundsHandle

flipHandleIfNeeded
^^^^^^^^^^^^^^^^^^

.. java:method:: public void flipHandleIfNeeded(boolean flipX, boolean flipY)
   :outertype: BoundsHandle

flipSiblingBoundsHandles
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @SuppressWarnings public void flipSiblingBoundsHandles(boolean flipX, boolean flipY)
   :outertype: BoundsHandle

getCursorFor
^^^^^^^^^^^^

.. java:method:: public Cursor getCursorFor(int side)
   :outertype: BoundsHandle

getHandleCursorEventHandler
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public PBasicInputEventHandler getHandleCursorEventHandler()
   :outertype: BoundsHandle

   Return the event handler that is responsible for setting the mouse cursor when it enters/exits this handle.

installHandleEventHandlers
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void installHandleEventHandlers()
   :outertype: BoundsHandle

removeBoundsHandlesFrom
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @SuppressWarnings public static void removeBoundsHandlesFrom(WorldObjectImpl wo)
   :outertype: BoundsHandle

startHandleDrag
^^^^^^^^^^^^^^^

.. java:method:: @Override public void startHandleDrag(Point2D aLocalPoint, PInputEvent aEvent)
   :outertype: BoundsHandle

