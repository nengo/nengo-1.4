.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

WorldSky
========

.. java:package:: ca.nengo.ui.lib.world
   :noindex:

.. java:type:: public interface WorldSky extends WorldLayer

Fields
------
MAX_ZOOM_SCALE
^^^^^^^^^^^^^^

.. java:field:: public static final double MAX_ZOOM_SCALE
   :outertype: WorldSky

MIN_ZOOM_SCALE
^^^^^^^^^^^^^^

.. java:field:: public static final double MIN_ZOOM_SCALE
   :outertype: WorldSky

Methods
-------
animateViewToCenterBounds
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void animateViewToCenterBounds(Rectangle2D centerBounds, boolean shouldScaleToFit, long duration)
   :outertype: WorldSky

   Animate the camera's view from its current transform when the activity starts to a new transform that centers the given bounds in the camera layers coordinate system into the cameras view bounds. If the duration is 0 then the view will be transformed immediately, and null will be returned. Else a new PTransformActivity will get returned that is set to animate the camera's view transform to the new bounds. If shouldScale is true, then the camera will also scale its view so that the given bounds fit fully within the cameras view bounds, else the camera will maintain its original scale.

getViewBounds
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D getViewBounds()
   :outertype: WorldSky

   Return the bounds of this camera in the view coordinate system.

getViewScale
^^^^^^^^^^^^

.. java:method:: public double getViewScale()
   :outertype: WorldSky

   Return the scale applied by the view transform to the layers viewed by this camera.

localToView
^^^^^^^^^^^

.. java:method:: public Point2D localToView(Point2D localPoint)
   :outertype: WorldSky

   Convert the point from the camera's local coordinate system to the camera's view coordinate system. The given point is modified by this method.

localToView
^^^^^^^^^^^

.. java:method:: public Rectangle2D localToView(Rectangle2D localRectangle)
   :outertype: WorldSky

   Convert the rectangle from the camera's local coordinate system to the camera's view coordinate system. The given rectangle is modified by this method.

setViewBounds
^^^^^^^^^^^^^

.. java:method:: public void setViewBounds(Rectangle2D centerBounds)
   :outertype: WorldSky

   Translates and scales the camera's view transform so that the given bounds (in camera layer's coordinate system)are centered withing the cameras view bounds. Use this method to point the camera at a given location.

setViewScale
^^^^^^^^^^^^

.. java:method:: public void setViewScale(double scale)
   :outertype: WorldSky

   Set the scale of the view transform that is applied to the layers viewed by this camera.

viewToLocal
^^^^^^^^^^^

.. java:method:: public Point2D viewToLocal(Point2D viewPoint)
   :outertype: WorldSky

   Convert the point from the camera's view coordinate system to the camera's local coordinate system. The given point is modified by this.

viewToLocal
^^^^^^^^^^^

.. java:method:: public Rectangle2D viewToLocal(Rectangle2D viewRectangle)
   :outertype: WorldSky

   Convert the rectangle from the camera's view coordinate system to the camera's local coordinate system. The given rectangle is modified by this method.

