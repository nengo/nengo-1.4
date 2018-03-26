.. java:import:: java.awt Cursor

.. java:import:: java.awt.event MouseEvent

.. java:import:: java.awt.geom Point2D

.. java:import:: java.util Stack

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects SelectionBorder

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects Window

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.viewers NetworkViewer

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

MouseHandler
============

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public class MouseHandler extends PBasicInputEventHandler

   Handles mouse events. Passes double click and mouse context button events to World Objects. Displays a frame around interactable objects as the mouse moves.

   :author: Shu Wu

Constructors
------------
MouseHandler
^^^^^^^^^^^^

.. java:constructor:: public MouseHandler(WorldImpl world)
   :outertype: MouseHandler

Methods
-------
getActiveMouseHandler
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static MouseHandler getActiveMouseHandler()
   :outertype: MouseHandler

getMouseMovedRelativePosition
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Point2D getMouseMovedRelativePosition()
   :outertype: MouseHandler

getWorld
^^^^^^^^

.. java:method:: public WorldImpl getWorld()
   :outertype: MouseHandler

mouseClicked
^^^^^^^^^^^^

.. java:method:: @Override public void mouseClicked(PInputEvent event)
   :outertype: MouseHandler

mouseMoved
^^^^^^^^^^

.. java:method:: @Override public void mouseMoved(PInputEvent event)
   :outertype: MouseHandler

mousePressed
^^^^^^^^^^^^

.. java:method:: @Override public void mousePressed(PInputEvent event)
   :outertype: MouseHandler

mouseReleased
^^^^^^^^^^^^^

.. java:method:: @Override public void mouseReleased(PInputEvent event)
   :outertype: MouseHandler

