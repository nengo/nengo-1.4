.. java:import:: java.awt Paint

.. java:import:: java.awt.geom Dimension2D

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.util Collection

.. java:import:: edu.umd.cs.piccolo.activities PInterpolatingActivity

WorldObject
===========

.. java:package:: ca.nengo.ui.lib.world
   :noindex:

.. java:type:: public interface WorldObject extends NamedObject, Destroyable

Methods
-------
addChild
^^^^^^^^

.. java:method:: public void addChild(WorldObject wo)
   :outertype: WorldObject

addChildrenListener
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addChildrenListener(ChildListener listener)
   :outertype: WorldObject

addPropertyChangeListener
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addPropertyChangeListener(Property event, Listener listener)
   :outertype: WorldObject

altClicked
^^^^^^^^^^

.. java:method:: public void altClicked()
   :outertype: WorldObject

   Called if this object is clicked on with the 'alt' key held

animateToBounds
^^^^^^^^^^^^^^^

.. java:method:: public PInterpolatingActivity animateToBounds(double x, double y, double width, double height, long duration)
   :outertype: WorldObject

   Animate this node's bounds from their current location when the activity starts to the specified bounds. If this node descends from the root then the activity will be scheduled, else the returned activity should be scheduled manually. If two different transform activities are scheduled for the same node at the same time, they will both be applied to the node, but the last one scheduled will be applied last on each frame, so it will appear to have replaced the original. Generally you will not want to do that. Note this method animates the node's bounds, but does not change the node's transform. Use animateTransformToBounds() to animate the node's transform instead.

   :param duration: amount of time that the animation should take
   :return: the newly scheduled activity

animateToPosition
^^^^^^^^^^^^^^^^^

.. java:method:: public void animateToPosition(double x, double y, long duration)
   :outertype: WorldObject

animateToPositionScaleRotation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void animateToPositionScaleRotation(double x, double y, double scale, double theta, long duration)
   :outertype: WorldObject

   Animate this node's transform from its current location when the activity starts to the specified location, scale, and rotation. If this node descends from the root then the activity will be scheduled, else the returned activity should be scheduled manually. If two different transform activities are scheduled for the same node at the same time, they will both be applied to the node, but the last one scheduled will be applied last on each frame, so it will appear to have replaced the original. Generally you will not want to do that.

   :param duration: amount of time that the animation should take
   :param theta: final theta value (in radians) for the animation
   :return: the newly scheduled activity

animateToScale
^^^^^^^^^^^^^^

.. java:method:: public void animateToScale(double scale, long duration)
   :outertype: WorldObject

   :param scale: Scale to animate to
   :param duration: Duration of animation

animateToTransparency
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void animateToTransparency(float transparency, long duration)
   :outertype: WorldObject

   :param transparency: Transparency to animate to
   :param duration: Duration of animation

childAdded
^^^^^^^^^^

.. java:method:: public void childAdded(WorldObject wo)
   :outertype: WorldObject

   :param wo: Child which has just been added

childRemoved
^^^^^^^^^^^^

.. java:method:: public void childRemoved(WorldObject wo)
   :outertype: WorldObject

   :param wo: Child which has just been removed

destroyChildren
^^^^^^^^^^^^^^^

.. java:method:: public void destroyChildren()
   :outertype: WorldObject

doubleClicked
^^^^^^^^^^^^^

.. java:method:: public void doubleClicked()
   :outertype: WorldObject

   Called if this object is double clicked on

dragOffset
^^^^^^^^^^

.. java:method:: public void dragOffset(double dx, double dy)
   :outertype: WorldObject

   Offset this node relative to the parents coordinate system, and is NOT effected by this nodes current scale or rotation. This is implemented by directly adding dx to the m02 position and dy to the m12 position in the affine transform.

findIntersectingNodes
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Collection<WorldObject> findIntersectingNodes(Rectangle2D fullBounds)
   :outertype: WorldObject

getBounds
^^^^^^^^^

.. java:method:: public Rectangle2D getBounds()
   :outertype: WorldObject

   Return a copy of this node's bounds. These bounds are stored in the local coordinate system of this node and do not include the bounds of any of this node's children.

getChildren
^^^^^^^^^^^

.. java:method:: public Iterable<WorldObject> getChildren()
   :outertype: WorldObject

   Return the list used to manage this node's children. This list should not be modified.

   :return: reference to the children list

getChildrenCount
^^^^^^^^^^^^^^^^

.. java:method:: public int getChildrenCount()
   :outertype: WorldObject

   :return: Number of children

getFullBounds
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D getFullBounds()
   :outertype: WorldObject

   Return a copy of this node's full bounds. These bounds are stored in the parent coordinate system of this node and they include the union of this node's bounds and all the bounds of it's descendents.

   :return: a copy of this node's full bounds.

getHeight
^^^^^^^^^

.. java:method:: public double getHeight()
   :outertype: WorldObject

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: WorldObject

getOffset
^^^^^^^^^

.. java:method:: public Point2D getOffset()
   :outertype: WorldObject

   Return the offset that is being applied to this node by its transform. This offset effects this node and all of its descendents and is specified in the parent coordinate system. This returns the values that are in the m02 and m12 positions in the affine transform.

   :return: a point representing the x and y offset

getParent
^^^^^^^^^

.. java:method:: public WorldObject getParent()
   :outertype: WorldObject

getRotation
^^^^^^^^^^^

.. java:method:: public double getRotation()
   :outertype: WorldObject

   Returns the rotation applied by this node's transform in radians. This rotation affects this node and all its descendents. The value returned will be between 0 and 2pi radians.

   :return: rotation in radians.

getScale
^^^^^^^^

.. java:method:: public double getScale()
   :outertype: WorldObject

   Return the scale applied by this node's transform. The scale is effecting this node and all its descendents.

   :return: scale applied by this nodes transform.

getTooltip
^^^^^^^^^^

.. java:method:: public WorldObject getTooltip()
   :outertype: WorldObject

   :return: Tooltip object, null if there is none

getTransparency
^^^^^^^^^^^^^^^

.. java:method:: public float getTransparency()
   :outertype: WorldObject

   Return the transparency used when painting this node. Note that this transparency is also applied to all of the node's descendents.

getVisible
^^^^^^^^^^

.. java:method:: public boolean getVisible()
   :outertype: WorldObject

   Return true if this node is visible, that is if it will paint itself and descendents.

   :return: true if this node and its descendents are visible.

getWidth
^^^^^^^^

.. java:method:: public double getWidth()
   :outertype: WorldObject

getWorld
^^^^^^^^

.. java:method:: public World getWorld()
   :outertype: WorldObject

   :return: World which is an ancestor

getWorldLayer
^^^^^^^^^^^^^

.. java:method:: public WorldLayer getWorldLayer()
   :outertype: WorldObject

   :return: World layer which is an ancestor

getX
^^^^

.. java:method:: public double getX()
   :outertype: WorldObject

   Return the x position (in local coords) of this node's bounds.

getY
^^^^

.. java:method:: public double getY()
   :outertype: WorldObject

   Return the y position (in local coords) of this node's bounds.

globalToLocal
^^^^^^^^^^^^^

.. java:method:: public Dimension2D globalToLocal(Dimension2D globalDimension)
   :outertype: WorldObject

   Transform the given dimension from global coordinates to this node's local coordinate system. Note that this will modify the dimension parameter.

   :param globalDimension: dimension in global coordinates to be transformed.
   :return: dimension in this node's local coordinate system.

globalToLocal
^^^^^^^^^^^^^

.. java:method:: public Point2D globalToLocal(Point2D globalPoint)
   :outertype: WorldObject

   Converts a global coordinate to a local coordinate. This method modifies the parameter.

   :param globalPoint: Global coordinate
   :return: Local coordinate

globalToLocal
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D globalToLocal(Rectangle2D globalPoint)
   :outertype: WorldObject

   Converts a local bound to a global bound. This method modifies the parameter.

   :param globalPoint: Global bound
   :return: local bound

isAncestorOf
^^^^^^^^^^^^

.. java:method:: public boolean isAncestorOf(WorldObject wo)
   :outertype: WorldObject

isAnimating
^^^^^^^^^^^

.. java:method:: public boolean isAnimating()
   :outertype: WorldObject

   :return: Whether this node is animating

isDestroyed
^^^^^^^^^^^

.. java:method:: public boolean isDestroyed()
   :outertype: WorldObject

   :return: Whether this Object has been destroyed

isDraggable
^^^^^^^^^^^

.. java:method:: public boolean isDraggable()
   :outertype: WorldObject

isSelectable
^^^^^^^^^^^^

.. java:method:: public boolean isSelectable()
   :outertype: WorldObject

   :return: Whether this object is selectable by a Selection Handler

isSelected
^^^^^^^^^^

.. java:method:: public boolean isSelected()
   :outertype: WorldObject

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: public void layoutChildren()
   :outertype: WorldObject

   Nodes that apply layout constraints to their children should override this method and do the layout there.

localToGlobal
^^^^^^^^^^^^^

.. java:method:: public Point2D localToGlobal(Point2D localPoint)
   :outertype: WorldObject

   Transform the given point from this node's local coordinate system to the global coordinate system. Note that this will modify the point parameter.

   :param localPoint: point in local coordinate system to be transformed.
   :return: point in global coordinates

localToGlobal
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D localToGlobal(Rectangle2D localRectangle)
   :outertype: WorldObject

   Transform the given rectangle from this node's local coordinate system to the global coordinate system. Note that this will modify the rectangle parameter.

   :param localRectangle: rectangle in local coordinate system to be transformed.
   :return: rectangle in global coordinates

localToParent
^^^^^^^^^^^^^

.. java:method:: public Point2D localToParent(Point2D localPoint)
   :outertype: WorldObject

   Transform the given point from this node's local coordinate system to its parent's local coordinate system. Note that this will modify the point parameter.

   :param localPoint: point in local coordinate system to be transformed.
   :return: point in parent's local coordinate system

localToParent
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D localToParent(Rectangle2D localRectangle)
   :outertype: WorldObject

   Transform the given rectangle from this node's local coordinate system to its parent's local coordinate system. Note that this will modify the rectangle parameter.

   :param localRectangle: rectangle in local coordinate system to be transformed.
   :return: rectangle in parent's local coordinate system

localToParent
^^^^^^^^^^^^^

.. java:method:: public Dimension2D localToParent(Dimension2D localRectangle)
   :outertype: WorldObject

moveToBack
^^^^^^^^^^

.. java:method:: public void moveToBack()
   :outertype: WorldObject

   Change the order of this node in its parent's children list so that it will draw in back of all of its other sibling nodes.

moveToFront
^^^^^^^^^^^

.. java:method:: public void moveToFront()
   :outertype: WorldObject

   Change the order of this node in its parent's children list so that it will draw after the given sibling node.

objectToGround
^^^^^^^^^^^^^^

.. java:method:: public Point2D objectToGround(Point2D position)
   :outertype: WorldObject

   :param position: Position relative to object
   :return: Position relative to World's ground layer

objectToGround
^^^^^^^^^^^^^^

.. java:method:: public Rectangle2D objectToGround(Rectangle2D rectangle)
   :outertype: WorldObject

   :param rectangle: relative to object
   :return: Relative to World's ground layer

objectToSky
^^^^^^^^^^^

.. java:method:: public Point2D objectToSky(Point2D position)
   :outertype: WorldObject

   :param position: relative to object
   :return: relative to World's sky layer

objectToSky
^^^^^^^^^^^

.. java:method:: public Rectangle2D objectToSky(Rectangle2D rectangle)
   :outertype: WorldObject

   :param rectangle: relative to object
   :return: relative to World's sky layer

paint
^^^^^

.. java:method:: public void paint(PaintContext paintContext)
   :outertype: WorldObject

   Paint this node behind any of its children nodes. Subclasses that define a different appearance should override this method and paint themselves there.

   :param paintContext: the paint context to use for painting the node

parentToLocal
^^^^^^^^^^^^^

.. java:method:: public Point2D parentToLocal(Point2D parentPoint)
   :outertype: WorldObject

   Transform the given point from this node's parent's local coordinate system to the local coordinate system of this node. Note that this will modify the point parameter.

   :param parentPoint: point in parent's coordinate system to be transformed.
   :return: point in this node's local coordinate system

parentToLocal
^^^^^^^^^^^^^

.. java:method:: public Rectangle2D parentToLocal(Rectangle2D parentRectangle)
   :outertype: WorldObject

   Transform the given rectangle from this node's parent's local coordinate system to the local coordinate system of this node. Note that this will modify the rectangle parameter.

   :param parentRectangle: rectangle in parent's coordinate system to be transformed.
   :return: rectangle in this node's local coordinate system

removeChild
^^^^^^^^^^^

.. java:method:: public void removeChild(WorldObject wo)
   :outertype: WorldObject

removeChildrenListener
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChildrenListener(ChildListener listener)
   :outertype: WorldObject

removeFromParent
^^^^^^^^^^^^^^^^

.. java:method:: public void removeFromParent()
   :outertype: WorldObject

   Delete this node by removing it from its parent's list of children.

removeFromWorld
^^^^^^^^^^^^^^^

.. java:method:: public void removeFromWorld()
   :outertype: WorldObject

   This function must be called before the object is removed from the world, or placed in a new one

removePropertyChangeListener
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removePropertyChangeListener(Property event, Listener listener)
   :outertype: WorldObject

repaint
^^^^^^^

.. java:method:: public void repaint()
   :outertype: WorldObject

   Mark the area on the screen represented by this nodes full bounds as needing a repaint.

setBounds
^^^^^^^^^

.. java:method:: public boolean setBounds(double x, double y, double width, double height)
   :outertype: WorldObject

   Set the bounds of this node to the given value. These bounds are stored in the local coordinate system of this node. If the width or height is less then or equal to zero then the bound's emtpy bit will be set to true. Subclasses must call the super.setBounds() method.

   :return: true if the bounds changed.

setBounds
^^^^^^^^^

.. java:method:: public boolean setBounds(Rectangle2D newBounds)
   :outertype: WorldObject

   Set the bounds of this node to the given value. These bounds are stored in the local coordinate system of this node.

   :return: true if the bounds changed.

setChildrenPickable
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setChildrenPickable(boolean areChildrenPickable)
   :outertype: WorldObject

   Set the children pickable flag. If this flag is false then this node will not try to pick its children. Children are pickable by default.

   :param areChildrenPickable: true if this node tries to pick its children

setDraggable
^^^^^^^^^^^^

.. java:method:: public void setDraggable(boolean draggable)
   :outertype: WorldObject

setHeight
^^^^^^^^^

.. java:method:: public boolean setHeight(double height)
   :outertype: WorldObject

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: WorldObject

   :param name: New name for this object

setOffset
^^^^^^^^^

.. java:method:: public void setOffset(double x, double y)
   :outertype: WorldObject

   Set the offset that is being applied to this node by its transform. This offset effects this node and all of its descendents and is specified in the nodes parent coordinate system. This directly sets the values of the m02 and m12 positions in the affine transform. Unlike "PNode.translate()" it is not effected by the transforms scale.

   :param x: amount of x offset
   :param y: amount of y offset

setOffset
^^^^^^^^^

.. java:method:: public void setOffset(Point2D point)
   :outertype: WorldObject

   Set the offset that is being applied to this node by its transform. This offset effects this node and all of its descendents and is specified in the nodes parent coordinate system. This directly sets the values of the m02 and m12 positions in the affine transform. Unlike "PNode.translate()" it is not effected by the transforms scale.

   :param point: a point representing the x and y offset

setPaint
^^^^^^^^

.. java:method:: public void setPaint(Paint newPaint)
   :outertype: WorldObject

   Set the paint used to paint this node. This value may be set to null.

setPickable
^^^^^^^^^^^

.. java:method:: public void setPickable(boolean isPickable)
   :outertype: WorldObject

   Set the pickable flag for this node. Only pickable nodes can receive input events. Nodes are pickable by default.

   :param isPickable: true if this node is pickable

setRotation
^^^^^^^^^^^

.. java:method:: public void setRotation(double theta)
   :outertype: WorldObject

   Sets the rotation of this nodes transform in radians. This will affect this node and all its descendents.

   :param theta: rotation in radians

setScale
^^^^^^^^

.. java:method:: public void setScale(double scale)
   :outertype: WorldObject

   Set the scale of this node's transform. The scale will affect this node and all its descendents.

   :param scale: the scale to set the transform to

setSelectable
^^^^^^^^^^^^^

.. java:method:: public void setSelectable(boolean isSelectable)
   :outertype: WorldObject

   :param isSelectable: Whether this object is selectable by a Selection handler

setSelected
^^^^^^^^^^^

.. java:method:: public void setSelected(boolean isSelected)
   :outertype: WorldObject

setTransparency
^^^^^^^^^^^^^^^

.. java:method:: public void setTransparency(float zeroToOne)
   :outertype: WorldObject

   Set the transparency used to paint this node. Note that this transparency applies to this node and all of its descendents.

setVisible
^^^^^^^^^^

.. java:method:: public void setVisible(boolean isVisible)
   :outertype: WorldObject

setWidth
^^^^^^^^

.. java:method:: public boolean setWidth(double width)
   :outertype: WorldObject

showPopupMessage
^^^^^^^^^^^^^^^^

.. java:method:: public void showPopupMessage(String msg)
   :outertype: WorldObject

   Show a transient message which appears over the object. The message is added to the world's sky layer.

   :param msg:

translate
^^^^^^^^^

.. java:method:: public void translate(double dx, double dy)
   :outertype: WorldObject

   Translate this node's transform by the given amount, using the standard affine transform translate method. This translation effects this node and all of its descendents.

