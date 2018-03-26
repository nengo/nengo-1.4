.. java:import:: java.awt BasicStroke

.. java:import:: java.awt Color

.. java:import:: java.awt Graphics2D

.. java:import:: java.awt Paint

.. java:import:: java.awt Shape

.. java:import:: java.awt Stroke

.. java:import:: java.awt.geom Ellipse2D

.. java:import:: java.awt.geom GeneralPath

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.io IOException

.. java:import:: java.io ObjectInputStream

.. java:import:: java.io ObjectOutputStream

.. java:import:: edu.umd.cs.piccolo.util PAffineTransform

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

.. java:import:: edu.umd.cs.piccolo.util PUtil

PXPath
======

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class PXPath extends PXNode

   \ **PXPath**\  is a wrapper around a java.awt.geom.GeneralPath. The setBounds method works by scaling the path to fit into the specified bounds. This normally works well, but if the specified base bounds get too small then it is impossible to expand the path shape again since all its numbers have tended to zero, so application code may need to take this into consideration.

   One option that applications have is to call \ ``startResizeBounds``\  before starting an interaction that may make the bounds very small, and calling \ ``endResizeBounds``\  when this interaction is finished. When this is done PXPath will use a copy of the original path to do the resizing so the numbers in the path wont loose resolution.

   This class also provides methods for constructing common shapes using a general path.

   :author: Jesse Grosjean

Fields
------
PROPERTY_CODE_PATH
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int PROPERTY_CODE_PATH
   :outertype: PXPath

PROPERTY_CODE_STROKE
^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int PROPERTY_CODE_STROKE
   :outertype: PXPath

PROPERTY_CODE_STROKE_PAINT
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int PROPERTY_CODE_STROKE_PAINT
   :outertype: PXPath

PROPERTY_PATH
^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_PATH
   :outertype: PXPath

   The property name that identifies a change of this node's path (see \ :java:ref:`getPathReference <getPathReference>`\ ). In any property change event the new value will be a reference to this node's path, but old value will always be null.

PROPERTY_STROKE
^^^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_STROKE
   :outertype: PXPath

   The property name that identifies a change of this node's stroke (see \ :java:ref:`getStroke <getStroke>`\ ). Both old and new value will be set correctly to Stroke objects in any property change event.

PROPERTY_STROKE_PAINT
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_STROKE_PAINT
   :outertype: PXPath

   The property name that identifies a change of this node's stroke paint (see \ :java:ref:`getStrokePaint <getStrokePaint>`\ ). Both old and new value will be set correctly to Paint objects in any property change event.

Constructors
------------
PXPath
^^^^^^

.. java:constructor:: public PXPath()
   :outertype: PXPath

PXPath
^^^^^^

.. java:constructor:: public PXPath(Shape aShape)
   :outertype: PXPath

PXPath
^^^^^^

.. java:constructor:: public PXPath(Shape aShape, Stroke aStroke)
   :outertype: PXPath

   Construct this path with the given shape and stroke. This method may be used to optimize the creation of a large number of PPaths. Normally PPaths have a default stroke of width one, but when a path has a non null stroke it takes significantly longer to compute its bounds. This method allows you to override that default stroke before the bounds are ever calculated, so if you pass in a null stroke here you won't ever have to pay that bounds calculation price if you don't need to.

Methods
-------
append
^^^^^^

.. java:method:: public void append(Shape aShape, boolean connect)
   :outertype: PXPath

closePath
^^^^^^^^^

.. java:method:: public void closePath()
   :outertype: PXPath

createEllipse
^^^^^^^^^^^^^

.. java:method:: public static PXPath createEllipse(float x, float y, float width, float height)
   :outertype: PXPath

createLine
^^^^^^^^^^

.. java:method:: public static PXPath createLine(float x1, float y1, float x2, float y2)
   :outertype: PXPath

createPolyline
^^^^^^^^^^^^^^

.. java:method:: public static PXPath createPolyline(Point2D[] points)
   :outertype: PXPath

createPolyline
^^^^^^^^^^^^^^

.. java:method:: public static PXPath createPolyline(float[] xp, float[] yp)
   :outertype: PXPath

createRectangle
^^^^^^^^^^^^^^^

.. java:method:: public static PXPath createRectangle(float x, float y, float width, float height)
   :outertype: PXPath

curveTo
^^^^^^^

.. java:method:: public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3)
   :outertype: PXPath

endResizeBounds
^^^^^^^^^^^^^^^

.. java:method:: public void endResizeBounds()
   :outertype: PXPath

getPathBoundsWithStroke
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Rectangle2D getPathBoundsWithStroke()
   :outertype: PXPath

getPathReference
^^^^^^^^^^^^^^^^

.. java:method:: public GeneralPath getPathReference()
   :outertype: PXPath

getStroke
^^^^^^^^^

.. java:method:: public Stroke getStroke()
   :outertype: PXPath

getStrokePaint
^^^^^^^^^^^^^^

.. java:method:: public Paint getStrokePaint()
   :outertype: PXPath

internalUpdateBounds
^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void internalUpdateBounds(double x, double y, double width, double height)
   :outertype: PXPath

   Set the bounds of this path. This method works by scaling the path to fit into the specified bounds. This normally works well, but if the specified base bounds get too small then it is impossible to expand the path shape again since all its numbers have tended to zero, so application code may need to take this into consideration.

intersects
^^^^^^^^^^

.. java:method:: public boolean intersects(Rectangle2D aBounds)
   :outertype: PXPath

lineTo
^^^^^^

.. java:method:: public void lineTo(float x, float y)
   :outertype: PXPath

moveTo
^^^^^^

.. java:method:: public void moveTo(float x, float y)
   :outertype: PXPath

paint
^^^^^

.. java:method:: protected void paint(PPaintContext paintContext)
   :outertype: PXPath

paramString
^^^^^^^^^^^

.. java:method:: protected String paramString()
   :outertype: PXPath

   Returns a string representing the state of this node. This method is intended to be used only for debugging purposes, and the content and format of the returned string may vary between implementations. The returned string may be empty but may not be \ ``null``\ .

   :return: a string representation of this node's state

quadTo
^^^^^^

.. java:method:: public void quadTo(float x1, float y1, float x2, float y2)
   :outertype: PXPath

reset
^^^^^

.. java:method:: public void reset()
   :outertype: PXPath

setPathTo
^^^^^^^^^

.. java:method:: public void setPathTo(Shape aShape)
   :outertype: PXPath

setPathToEllipse
^^^^^^^^^^^^^^^^

.. java:method:: public void setPathToEllipse(float x, float y, float width, float height)
   :outertype: PXPath

setPathToPolyline
^^^^^^^^^^^^^^^^^

.. java:method:: public void setPathToPolyline(Point2D[] points)
   :outertype: PXPath

setPathToPolyline
^^^^^^^^^^^^^^^^^

.. java:method:: public void setPathToPolyline(float[] xp, float[] yp)
   :outertype: PXPath

setPathToRectangle
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setPathToRectangle(float x, float y, float width, float height)
   :outertype: PXPath

setStroke
^^^^^^^^^

.. java:method:: public void setStroke(Stroke aStroke)
   :outertype: PXPath

setStrokePaint
^^^^^^^^^^^^^^

.. java:method:: public void setStrokePaint(Paint aPaint)
   :outertype: PXPath

startResizeBounds
^^^^^^^^^^^^^^^^^

.. java:method:: public void startResizeBounds()
   :outertype: PXPath

updateBoundsFromPath
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void updateBoundsFromPath()
   :outertype: PXPath

