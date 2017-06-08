.. java:import:: java.awt.geom AffineTransform

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.activities PTransformActivity

.. java:import:: edu.umd.cs.piccolo.util PUtil

PXCamera
========

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class PXCamera extends PCamera implements PiccoloNodeInWorld

Methods
-------
addChild
^^^^^^^^

.. java:method:: @Override public void addChild(int index, PNode child)
   :outertype: PXCamera

animateViewToTransform
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public PTransformActivity animateViewToTransform(AffineTransform destTransform, long duration)
   :outertype: PXCamera

getWorldObject
^^^^^^^^^^^^^^

.. java:method:: public WorldObject getWorldObject()
   :outertype: PXCamera

isAnimating
^^^^^^^^^^^

.. java:method:: public boolean isAnimating()
   :outertype: PXCamera

removeChild
^^^^^^^^^^^

.. java:method:: public PNode removeChild(int arg0)
   :outertype: PXCamera

setWorldObject
^^^^^^^^^^^^^^

.. java:method:: public void setWorldObject(WorldObject worldObjectParent)
   :outertype: PXCamera

