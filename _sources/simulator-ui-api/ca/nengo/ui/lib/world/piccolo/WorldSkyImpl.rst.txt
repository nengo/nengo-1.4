.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.lang UnsupportedOperationException

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world WorldSky

.. java:import:: ca.nengo.ui.lib.world.handlers KeyboardFocusHandler

.. java:import:: ca.nengo.ui.lib.world.handlers ScrollZoomHandler

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXCamera

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXEdge

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo PLayer

.. java:import:: edu.umd.cs.piccolo.event PZoomEventHandler

WorldSkyImpl
============

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type:: public class WorldSkyImpl extends WorldLayerImpl implements WorldSky

   A layer within a world which looks at the ground layer. This layer can also contain world objects, but their positions are static during panning and zooming.

   :author: Shu Wu

Constructors
------------
WorldSkyImpl
^^^^^^^^^^^^

.. java:constructor:: public WorldSkyImpl()
   :outertype: WorldSkyImpl

   Create a new sky layer

   :param world: World this layer belongs to

Methods
-------
addEdge
^^^^^^^

.. java:method:: public void addEdge(PXEdge edge)
   :outertype: WorldSkyImpl

addLayer
^^^^^^^^

.. java:method:: public void addLayer(PLayer layer)
   :outertype: WorldSkyImpl

animateViewToCenterBounds
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void animateViewToCenterBounds(Rectangle2D centerBounds, boolean shouldScaleToFit, long duration)
   :outertype: WorldSkyImpl

getCamera
^^^^^^^^^

.. java:method:: public PCamera getCamera()
   :outertype: WorldSkyImpl

getViewBounds
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D getViewBounds()
   :outertype: WorldSkyImpl

getViewScale
^^^^^^^^^^^^

.. java:method:: public double getViewScale()
   :outertype: WorldSkyImpl

getWorld
^^^^^^^^

.. java:method:: public WorldImpl getWorld()
   :outertype: WorldSkyImpl

localToView
^^^^^^^^^^^

.. java:method:: public Point2D localToView(Point2D localPoint)
   :outertype: WorldSkyImpl

localToView
^^^^^^^^^^^

.. java:method:: public Rectangle2D localToView(Rectangle2D arg0)
   :outertype: WorldSkyImpl

setViewBounds
^^^^^^^^^^^^^

.. java:method:: public void setViewBounds(Rectangle2D centerBounds)
   :outertype: WorldSkyImpl

setViewScale
^^^^^^^^^^^^

.. java:method:: public void setViewScale(double scale)
   :outertype: WorldSkyImpl

setWorld
^^^^^^^^

.. java:method:: public void setWorld(WorldImpl world)
   :outertype: WorldSkyImpl

viewToLocal
^^^^^^^^^^^

.. java:method:: public Point2D viewToLocal(Point2D arg0)
   :outertype: WorldSkyImpl

viewToLocal
^^^^^^^^^^^

.. java:method:: public Rectangle2D viewToLocal(Rectangle2D arg0)
   :outertype: WorldSkyImpl

