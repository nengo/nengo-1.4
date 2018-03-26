.. java:import:: java.awt Paint

.. java:import:: java.awt.geom Dimension2D

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.beans PropertyChangeEvent

.. java:import:: java.beans PropertyChangeListener

.. java:import:: java.security InvalidParameterException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util Enumeration

.. java:import:: java.util HashSet

.. java:import:: java.util Hashtable

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: ca.nengo.ui.lib.objects.activities TransientMessage

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world Destroyable

.. java:import:: ca.nengo.ui.lib.world PaintContext

.. java:import:: ca.nengo.ui.lib.world WorldLayer

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world WorldObject.Listener

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXNode

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PiccoloNodeInWorld

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.activities PInterpolatingActivity

.. java:import:: edu.umd.cs.piccolo.event PInputEventListener

.. java:import:: edu.umd.cs.piccolo.util PBounds

WorldObjectImpl
===============

.. java:package:: ca.nengo.ui.lib.world.piccolo
   :noindex:

.. java:type:: public class WorldObjectImpl implements WorldObject

   World objects are visible UI objects which exist in a World layer (Ground or Sky).

   :author: Shu Wu

Fields
------
CONVERSION_MAP
^^^^^^^^^^^^^^

.. java:field:: public static final Object[][] CONVERSION_MAP
   :outertype: WorldObjectImpl

TIME_BETWEEN_POPUPS
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final long TIME_BETWEEN_POPUPS
   :outertype: WorldObjectImpl

Constructors
------------
WorldObjectImpl
^^^^^^^^^^^^^^^

.. java:constructor:: protected WorldObjectImpl(String name, PiccoloNodeInWorld pNode)
   :outertype: WorldObjectImpl

WorldObjectImpl
^^^^^^^^^^^^^^^

.. java:constructor:: public WorldObjectImpl()
   :outertype: WorldObjectImpl

   Creates an unnamed WorldObject

WorldObjectImpl
^^^^^^^^^^^^^^^

.. java:constructor:: public WorldObjectImpl(PiccoloNodeInWorld node)
   :outertype: WorldObjectImpl

WorldObjectImpl
^^^^^^^^^^^^^^^

.. java:constructor:: public WorldObjectImpl(String name)
   :outertype: WorldObjectImpl

   Creates a named WorldObject

   :param name: Name of this object

Methods
-------
addChild
^^^^^^^^

.. java:method:: public void addChild(WorldObject wo)
   :outertype: WorldObjectImpl

addChild
^^^^^^^^

.. java:method:: public void addChild(WorldObject wo, int index)
   :outertype: WorldObjectImpl

addChildrenListener
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addChildrenListener(ChildListener listener)
   :outertype: WorldObjectImpl

addInputEventListener
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addInputEventListener(PInputEventListener arg0)
   :outertype: WorldObjectImpl

addPropertyChangeListener
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addPropertyChangeListener(Property eventType, Listener worldListener)
   :outertype: WorldObjectImpl

altClicked
^^^^^^^^^^

.. java:method:: public void altClicked()
   :outertype: WorldObjectImpl

animateToBounds
^^^^^^^^^^^^^^^

.. java:method:: public PInterpolatingActivity animateToBounds(double x, double y, double width, double height, long duration)
   :outertype: WorldObjectImpl

animateToPosition
^^^^^^^^^^^^^^^^^

.. java:method:: public void animateToPosition(double x, double y, long duration)
   :outertype: WorldObjectImpl

animateToPositionScaleRotation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void animateToPositionScaleRotation(double x, double y, double scale, double theta, long duration)
   :outertype: WorldObjectImpl

animateToScale
^^^^^^^^^^^^^^

.. java:method:: public void animateToScale(double scale, long duration)
   :outertype: WorldObjectImpl

animateToTransparency
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void animateToTransparency(float transparency, long duration)
   :outertype: WorldObjectImpl

childAdded
^^^^^^^^^^

.. java:method:: public void childAdded(WorldObject wo)
   :outertype: WorldObjectImpl

childRemoved
^^^^^^^^^^^^

.. java:method:: public void childRemoved(WorldObject wo)
   :outertype: WorldObjectImpl

destroy
^^^^^^^

.. java:method:: public final void destroy()
   :outertype: WorldObjectImpl

destroyChildren
^^^^^^^^^^^^^^^

.. java:method:: public final void destroyChildren()
   :outertype: WorldObjectImpl

doubleClicked
^^^^^^^^^^^^^

.. java:method:: public void doubleClicked()
   :outertype: WorldObjectImpl

dragOffset
^^^^^^^^^^

.. java:method:: public void dragOffset(double dx, double dy)
   :outertype: WorldObjectImpl

findIntersectingNodes
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Collection<WorldObject> findIntersectingNodes(Rectangle2D fullBounds)
   :outertype: WorldObjectImpl

firePropertyChange
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void firePropertyChange(Property event)
   :outertype: WorldObjectImpl

getBounds
^^^^^^^^^

.. java:method:: public PBounds getBounds()
   :outertype: WorldObjectImpl

getChildren
^^^^^^^^^^^

.. java:method:: public Iterable<WorldObject> getChildren()
   :outertype: WorldObjectImpl

getChildrenCount
^^^^^^^^^^^^^^^^

.. java:method:: public int getChildrenCount()
   :outertype: WorldObjectImpl

getFullBounds
^^^^^^^^^^^^^

.. java:method:: public PBounds getFullBounds()
   :outertype: WorldObjectImpl

getHeight
^^^^^^^^^

.. java:method:: public double getHeight()
   :outertype: WorldObjectImpl

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: WorldObjectImpl

getOffset
^^^^^^^^^

.. java:method:: public Point2D getOffset()
   :outertype: WorldObjectImpl

getParent
^^^^^^^^^

.. java:method:: public WorldObject getParent()
   :outertype: WorldObjectImpl

getPiccolo
^^^^^^^^^^

.. java:method:: public PNode getPiccolo()
   :outertype: WorldObjectImpl

getRotation
^^^^^^^^^^^

.. java:method:: public double getRotation()
   :outertype: WorldObjectImpl

getScale
^^^^^^^^

.. java:method:: public double getScale()
   :outertype: WorldObjectImpl

getTooltip
^^^^^^^^^^

.. java:method:: public WorldObject getTooltip()
   :outertype: WorldObjectImpl

getTransparency
^^^^^^^^^^^^^^^

.. java:method:: public float getTransparency()
   :outertype: WorldObjectImpl

getVisible
^^^^^^^^^^

.. java:method:: public boolean getVisible()
   :outertype: WorldObjectImpl

getWidth
^^^^^^^^

.. java:method:: public double getWidth()
   :outertype: WorldObjectImpl

getWorld
^^^^^^^^

.. java:method:: public WorldImpl getWorld()
   :outertype: WorldObjectImpl

getWorldLayer
^^^^^^^^^^^^^

.. java:method:: public WorldLayer getWorldLayer()
   :outertype: WorldObjectImpl

getX
^^^^

.. java:method:: public double getX()
   :outertype: WorldObjectImpl

getY
^^^^

.. java:method:: public double getY()
   :outertype: WorldObjectImpl

globalToLocal
^^^^^^^^^^^^^

.. java:method:: public Dimension2D globalToLocal(Dimension2D globalDimension)
   :outertype: WorldObjectImpl

globalToLocal
^^^^^^^^^^^^^

.. java:method:: public Point2D globalToLocal(Point2D arg0)
   :outertype: WorldObjectImpl

globalToLocal
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D globalToLocal(Rectangle2D globalPoint)
   :outertype: WorldObjectImpl

isAncestorOf
^^^^^^^^^^^^

.. java:method:: public boolean isAncestorOf(WorldObject wo)
   :outertype: WorldObjectImpl

isAnimating
^^^^^^^^^^^

.. java:method:: public boolean isAnimating()
   :outertype: WorldObjectImpl

isDestroyed
^^^^^^^^^^^

.. java:method:: public boolean isDestroyed()
   :outertype: WorldObjectImpl

isDraggable
^^^^^^^^^^^

.. java:method:: public boolean isDraggable()
   :outertype: WorldObjectImpl

isSelectable
^^^^^^^^^^^^

.. java:method:: public boolean isSelectable()
   :outertype: WorldObjectImpl

isSelected
^^^^^^^^^^

.. java:method:: public boolean isSelected()
   :outertype: WorldObjectImpl

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: public void layoutChildren()
   :outertype: WorldObjectImpl

localToGlobal
^^^^^^^^^^^^^

.. java:method:: public Point2D localToGlobal(Point2D arg0)
   :outertype: WorldObjectImpl

localToGlobal
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D localToGlobal(Rectangle2D arg0)
   :outertype: WorldObjectImpl

localToParent
^^^^^^^^^^^^^

.. java:method:: public Dimension2D localToParent(Dimension2D localRectangle)
   :outertype: WorldObjectImpl

localToParent
^^^^^^^^^^^^^

.. java:method:: public Point2D localToParent(Point2D localPoint)
   :outertype: WorldObjectImpl

localToParent
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D localToParent(Rectangle2D localRectangle)
   :outertype: WorldObjectImpl

moveToBack
^^^^^^^^^^

.. java:method:: public void moveToBack()
   :outertype: WorldObjectImpl

moveToFront
^^^^^^^^^^^

.. java:method:: public void moveToFront()
   :outertype: WorldObjectImpl

objectToGround
^^^^^^^^^^^^^^

.. java:method:: public Point2D objectToGround(Point2D position)
   :outertype: WorldObjectImpl

objectToGround
^^^^^^^^^^^^^^

.. java:method:: public Rectangle2D objectToGround(Rectangle2D rectangle)
   :outertype: WorldObjectImpl

objectToSky
^^^^^^^^^^^

.. java:method:: public Point2D objectToSky(Point2D position)
   :outertype: WorldObjectImpl

objectToSky
^^^^^^^^^^^

.. java:method:: public Rectangle2D objectToSky(Rectangle2D rectangle)
   :outertype: WorldObjectImpl

paint
^^^^^

.. java:method:: public void paint(PaintContext paintContext)
   :outertype: WorldObjectImpl

parentToLocal
^^^^^^^^^^^^^

.. java:method:: public Point2D parentToLocal(Point2D parentPoint)
   :outertype: WorldObjectImpl

parentToLocal
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D parentToLocal(Rectangle2D parentRectangle)
   :outertype: WorldObjectImpl

piccoloEventToWorldEvent
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected static Property piccoloEventToWorldEvent(String propertyName)
   :outertype: WorldObjectImpl

prepareForDestroy
^^^^^^^^^^^^^^^^^

.. java:method:: protected void prepareForDestroy()
   :outertype: WorldObjectImpl

   Perform any operations before being destroyed

removeChild
^^^^^^^^^^^

.. java:method:: public void removeChild(WorldObject wo)
   :outertype: WorldObjectImpl

removeChildrenListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChildrenListener(ChildListener listener)
   :outertype: WorldObjectImpl

removeFromParent
^^^^^^^^^^^^^^^^

.. java:method:: public void removeFromParent()
   :outertype: WorldObjectImpl

removeFromWorld
^^^^^^^^^^^^^^^

.. java:method:: public void removeFromWorld()
   :outertype: WorldObjectImpl

removeInputEventListener
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeInputEventListener(PInputEventListener arg0)
   :outertype: WorldObjectImpl

removePropertyChangeListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removePropertyChangeListener(Property event, Listener listener)
   :outertype: WorldObjectImpl

repaint
^^^^^^^

.. java:method:: public void repaint()
   :outertype: WorldObjectImpl

setBounds
^^^^^^^^^

.. java:method:: public boolean setBounds(double arg0, double arg1, double arg2, double arg3)
   :outertype: WorldObjectImpl

setBounds
^^^^^^^^^

.. java:method:: public boolean setBounds(Rectangle2D arg0)
   :outertype: WorldObjectImpl

setChildrenPickable
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setChildrenPickable(boolean areChildrenPickable)
   :outertype: WorldObjectImpl

setDraggable
^^^^^^^^^^^^

.. java:method:: public void setDraggable(boolean draggable)
   :outertype: WorldObjectImpl

setHeight
^^^^^^^^^

.. java:method:: public boolean setHeight(double height)
   :outertype: WorldObjectImpl

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: WorldObjectImpl

setOffset
^^^^^^^^^

.. java:method:: public void setOffset(double arg0, double arg1)
   :outertype: WorldObjectImpl

setOffset
^^^^^^^^^

.. java:method:: public void setOffset(Point2D arg0)
   :outertype: WorldObjectImpl

setPaint
^^^^^^^^

.. java:method:: public void setPaint(Paint arg0)
   :outertype: WorldObjectImpl

setPickable
^^^^^^^^^^^

.. java:method:: public void setPickable(boolean isPickable)
   :outertype: WorldObjectImpl

setRotation
^^^^^^^^^^^

.. java:method:: public void setRotation(double theta)
   :outertype: WorldObjectImpl

setScale
^^^^^^^^

.. java:method:: public void setScale(double arg0)
   :outertype: WorldObjectImpl

setSelectable
^^^^^^^^^^^^^

.. java:method:: public void setSelectable(boolean isSelectable)
   :outertype: WorldObjectImpl

setSelected
^^^^^^^^^^^

.. java:method:: public void setSelected(boolean isSelected)
   :outertype: WorldObjectImpl

setTransparency
^^^^^^^^^^^^^^^

.. java:method:: public void setTransparency(float zeroToOne)
   :outertype: WorldObjectImpl

setVisible
^^^^^^^^^^

.. java:method:: public void setVisible(boolean isVisible)
   :outertype: WorldObjectImpl

setWidth
^^^^^^^^

.. java:method:: public boolean setWidth(double width)
   :outertype: WorldObjectImpl

showPopupMessage
^^^^^^^^^^^^^^^^

.. java:method:: public synchronized void showPopupMessage(String msg)
   :outertype: WorldObjectImpl

toString
^^^^^^^^

.. java:method:: public String toString()
   :outertype: WorldObjectImpl

translate
^^^^^^^^^

.. java:method:: public void translate(double dx, double dy)
   :outertype: WorldObjectImpl

worldEventToPiccoloEvent
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected static String worldEventToPiccoloEvent(Property type)
   :outertype: WorldObjectImpl

