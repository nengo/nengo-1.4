.. java:import:: java.awt.geom AffineTransform

.. java:import:: java.beans PropertyChangeEvent

.. java:import:: java.beans PropertyChangeListener

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world PaintContext

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.activities PTransformActivity

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

.. java:import:: edu.umd.cs.piccolo.util PUtil

PXNode
======

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class PXNode extends PNode implements PiccoloNodeInWorld

   :author: Shu Wu

Fields
------
PROPERTY_GLOBAL_BOUNDS
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_GLOBAL_BOUNDS
   :outertype: PXNode

   The property name that identifies a change in this object's global position

PROPERTY_PARENT_BOUNDS
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_PARENT_BOUNDS
   :outertype: PXNode

PROPERTY_REMOVED_FROM_WORLD
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String PROPERTY_REMOVED_FROM_WORLD
   :outertype: PXNode

Constructors
------------
PXNode
^^^^^^

.. java:constructor:: public PXNode()
   :outertype: PXNode

Methods
-------
addActivity
^^^^^^^^^^^

.. java:method:: @Override public boolean addActivity(PActivity arg0)
   :outertype: PXNode

addChild
^^^^^^^^

.. java:method:: @Override public void addChild(int index, PNode child)
   :outertype: PXNode

animateToTransform
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public PTransformActivity animateToTransform(AffineTransform destTransform, long duration)
   :outertype: PXNode

getWorldObject
^^^^^^^^^^^^^^

.. java:method:: public WorldObject getWorldObject()
   :outertype: PXNode

isAnimating
^^^^^^^^^^^

.. java:method:: public boolean isAnimating()
   :outertype: PXNode

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override protected final void layoutChildren()
   :outertype: PXNode

paint
^^^^^

.. java:method:: @Override protected void paint(PPaintContext paintContext)
   :outertype: PXNode

parentBoundsChanged
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void parentBoundsChanged()
   :outertype: PXNode

removeChild
^^^^^^^^^^^

.. java:method:: @Override public PNode removeChild(int arg0)
   :outertype: PXNode

removeFromWorld
^^^^^^^^^^^^^^^

.. java:method:: public void removeFromWorld()
   :outertype: PXNode

setParent
^^^^^^^^^

.. java:method:: @Override public void setParent(PNode newParent)
   :outertype: PXNode

setVisible
^^^^^^^^^^

.. java:method:: @Override public void setVisible(boolean isVisible)
   :outertype: PXNode

setWorldObject
^^^^^^^^^^^^^^

.. java:method:: public void setWorldObject(WorldObject worldObjectParent)
   :outertype: PXNode

signalBoundsChanged
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void signalBoundsChanged()
   :outertype: PXNode

signalGlobalBoundsChanged
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void signalGlobalBoundsChanged()
   :outertype: PXNode

   Signal to the attached edges that this node's position or transform in the World has changed

