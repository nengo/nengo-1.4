.. java:import:: java.awt.geom Point2D

.. java:import:: ca.nengo.ui.lib.world WorldSky

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

ScrollZoomHandler
=================

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public class ScrollZoomHandler extends PBasicInputEventHandler

   Zooms the world using the scroll wheel.

   :author: Shu Wu

Methods
-------
mouseWheelRotated
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void mouseWheelRotated(PInputEvent event)
   :outertype: ScrollZoomHandler

