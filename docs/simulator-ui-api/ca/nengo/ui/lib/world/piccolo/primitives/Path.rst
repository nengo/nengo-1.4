.. java:import:: java.awt Paint

.. java:import:: java.awt Stroke

.. java:import:: java.awt.geom Point2D

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

Path
====

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class Path extends WorldObjectImpl

Constructors
------------
Path
^^^^

.. java:constructor:: public Path()
   :outertype: Path

Methods
-------
createEllipse
^^^^^^^^^^^^^

.. java:method:: public static Path createEllipse(float x, float y, float width, float height)
   :outertype: Path

createLine
^^^^^^^^^^

.. java:method:: public static Path createLine(float x1, float y1, float x2, float y2)
   :outertype: Path

createPolyline
^^^^^^^^^^^^^^

.. java:method:: public static Path createPolyline(float[] xp, float[] yp)
   :outertype: Path

createPolyline
^^^^^^^^^^^^^^

.. java:method:: public static Path createPolyline(Point2D[] points)
   :outertype: Path

createRectangle
^^^^^^^^^^^^^^^

.. java:method:: public static Path createRectangle(float x, float y, float width, float height)
   :outertype: Path

getStrokePaint
^^^^^^^^^^^^^^

.. java:method:: public Paint getStrokePaint()
   :outertype: Path

setPathToPolyline
^^^^^^^^^^^^^^^^^

.. java:method:: public void setPathToPolyline(Point2D[] points)
   :outertype: Path

setStroke
^^^^^^^^^

.. java:method:: public void setStroke(Stroke stroke)
   :outertype: Path

setStrokePaint
^^^^^^^^^^^^^^

.. java:method:: public void setStrokePaint(Paint paint)
   :outertype: Path

