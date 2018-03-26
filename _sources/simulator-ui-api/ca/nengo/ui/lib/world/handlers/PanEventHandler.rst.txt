.. java:import:: java.awt Rectangle

.. java:import:: java.awt.geom Point2D

.. java:import:: java.util Iterator

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.event PPanEventHandler

.. java:import:: edu.umd.cs.piccolo.util PBounds

.. java:import:: edu.umd.cs.piccolo.util PDimension

PanEventHandler
===============

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public class PanEventHandler extends PPanEventHandler

   Extend PPanEventHandler so that panning direction can be inverted

   :author: Shu Wu

Constructors
------------
PanEventHandler
^^^^^^^^^^^^^^^

.. java:constructor:: public PanEventHandler()
   :outertype: PanEventHandler

Methods
-------
dragActivityStep
^^^^^^^^^^^^^^^^

.. java:method:: protected void dragActivityStep(PInputEvent aEvent)
   :outertype: PanEventHandler

   Do auto panning even when the mouse is not moving.

getSelectionHandler
^^^^^^^^^^^^^^^^^^^

.. java:method:: public SelectionHandler getSelectionHandler()
   :outertype: PanEventHandler

isInverted
^^^^^^^^^^

.. java:method:: public boolean isInverted()
   :outertype: PanEventHandler

setInverted
^^^^^^^^^^^

.. java:method:: public void setInverted(boolean isInverted)
   :outertype: PanEventHandler

setSelectionHandler
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setSelectionHandler(SelectionHandler s)
   :outertype: PanEventHandler

