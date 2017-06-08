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

PXEdge
======

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class PXEdge extends PXPath implements PropertyChangeListener, Destroyable, PiccoloNodeInWorld

   An edge with direction. An piccolo component.

   :author: Shu Wu

Fields
------
DEFAULT_MIN_ARC_RADIUS
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: static final double DEFAULT_MIN_ARC_RADIUS
   :outertype: PXEdge

   Default radius of the arc shape used by this edge. Only applies when the shape of this edge is set to an arc.

Constructors
------------
PXEdge
^^^^^^

.. java:constructor:: public PXEdge(WorldObjectImpl startNode, WorldObjectImpl endNode)
   :outertype: PXEdge

PXEdge
^^^^^^

.. java:constructor:: public PXEdge(WorldObjectImpl startNode, WorldObjectImpl endNode, boolean isDirected)
   :outertype: PXEdge

   Creates a new directed edge

   :param startNode: Starting node
   :param endNode: Ending node
   :param isDirected: Whether the direction of this edge matters

Methods
-------
destroy
^^^^^^^

.. java:method:: public void destroy()
   :outertype: PXEdge

getDefaultColor
^^^^^^^^^^^^^^^

.. java:method:: public Color getDefaultColor()
   :outertype: PXEdge

getEndNode
^^^^^^^^^^

.. java:method:: public WorldObjectImpl getEndNode()
   :outertype: PXEdge

getHighlightColor
^^^^^^^^^^^^^^^^^

.. java:method:: public Color getHighlightColor()
   :outertype: PXEdge

getStartNode
^^^^^^^^^^^^

.. java:method:: public WorldObjectImpl getStartNode()
   :outertype: PXEdge

getState
^^^^^^^^

.. java:method:: public EdgeState getState()
   :outertype: PXEdge

   :return: Edge state

getWorldObject
^^^^^^^^^^^^^^

.. java:method:: public WorldObject getWorldObject()
   :outertype: PXEdge

isAnimating
^^^^^^^^^^^

.. java:method:: public boolean isAnimating()
   :outertype: PXEdge

isDirected
^^^^^^^^^^

.. java:method:: public boolean isDirected()
   :outertype: PXEdge

isHideByDefault
^^^^^^^^^^^^^^^

.. java:method:: public boolean isHideByDefault()
   :outertype: PXEdge

   :return: Whether this edge is visible when in it's default state

propertyChange
^^^^^^^^^^^^^^

.. java:method:: public void propertyChange(PropertyChangeEvent event)
   :outertype: PXEdge

setDefaultColor
^^^^^^^^^^^^^^^

.. java:method:: public void setDefaultColor(Color defaultColor)
   :outertype: PXEdge

setHideByDefault
^^^^^^^^^^^^^^^^

.. java:method:: public void setHideByDefault(boolean hideByDefault)
   :outertype: PXEdge

   :param hideByDefault: If true, this edge is hidden in it's default state.

setHighlightColor
^^^^^^^^^^^^^^^^^

.. java:method:: public void setHighlightColor(Color highlightColor)
   :outertype: PXEdge

setLineShape
^^^^^^^^^^^^

.. java:method:: public void setLineShape(EdgeShape lineShape)
   :outertype: PXEdge

setMinArcRadius
^^^^^^^^^^^^^^^

.. java:method:: public void setMinArcRadius(double minArcRadius)
   :outertype: PXEdge

setPointerVisible
^^^^^^^^^^^^^^^^^

.. java:method:: public void setPointerVisible(boolean visible)
   :outertype: PXEdge

   :param visible: If true, the pointer will be visible. The pointer shows the direction of this edge.

setState
^^^^^^^^

.. java:method:: public final void setState(EdgeState state)
   :outertype: PXEdge

   :param state: New edge state

setWorldObject
^^^^^^^^^^^^^^

.. java:method:: public void setWorldObject(WorldObject worldObjectParent)
   :outertype: PXEdge

toLocal
^^^^^^^

.. java:method:: protected Point2D toLocal(WorldObject node, double x, double y)
   :outertype: PXEdge

toLocal
^^^^^^^

.. java:method:: protected Point2D toLocal(WorldObject node, Point2D point)
   :outertype: PXEdge

updateEdgeBounds
^^^^^^^^^^^^^^^^

.. java:method:: public void updateEdgeBounds()
   :outertype: PXEdge

   Updates the edge when the start or end node has changed

