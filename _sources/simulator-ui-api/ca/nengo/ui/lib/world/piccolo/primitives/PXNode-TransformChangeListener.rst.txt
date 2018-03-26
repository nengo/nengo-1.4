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

PXNode.TransformChangeListener
==============================

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type::  class TransformChangeListener implements PropertyChangeListener
   :outertype: PXNode

   Listens for transform changes, and signals that the global bounds for this object have changed

   :author: Shu Wu

Methods
-------
propertyChange
^^^^^^^^^^^^^^

.. java:method:: public void propertyChange(PropertyChangeEvent evt)
   :outertype: PXNode.TransformChangeListener

