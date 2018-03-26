.. java:import:: java.awt Color

.. java:import:: java.awt.geom Arc2D

.. java:import:: java.awt.geom Point2D

.. java:import:: java.beans PropertyChangeEvent

.. java:import:: java.beans PropertyChangeListener

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world Destroyable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

PointerTriangle
===============

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type::  class PointerTriangle extends PXEdge

   A triangle which points in the direction of a directed edge

   :author: Shu Wu

Fields
------
POINTER_DISTANCE_FROM_END_NODE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: static final double POINTER_DISTANCE_FROM_END_NODE
   :outertype: PointerTriangle

TRIANGLE_EDGE_LENGTH
^^^^^^^^^^^^^^^^^^^^

.. java:field:: static final double TRIANGLE_EDGE_LENGTH
   :outertype: PointerTriangle

Constructors
------------
PointerTriangle
^^^^^^^^^^^^^^^

.. java:constructor:: public PointerTriangle(PXEdge edge)
   :outertype: PointerTriangle

   Creates a new pointer triangle

   :param edge: Directed edge which dictates where and how to point

Methods
-------
updateEdgeBounds
^^^^^^^^^^^^^^^^

.. java:method:: @Override public void updateEdgeBounds()
   :outertype: PointerTriangle

