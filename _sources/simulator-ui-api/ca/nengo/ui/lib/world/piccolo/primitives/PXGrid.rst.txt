.. java:import:: java.awt BasicStroke

.. java:import:: java.awt Color

.. java:import:: java.awt Graphics2D

.. java:import:: java.awt Stroke

.. java:import:: java.awt.geom Line2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.beans PropertyChangeEvent

.. java:import:: java.beans PropertyChangeListener

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: edu.umd.cs.piccolo PCamera

.. java:import:: edu.umd.cs.piccolo PLayer

.. java:import:: edu.umd.cs.piccolo PNode

.. java:import:: edu.umd.cs.piccolo PRoot

.. java:import:: edu.umd.cs.piccolo.util PPaintContext

PXGrid
======

.. java:package:: ca.nengo.ui.lib.world.piccolo.primitives
   :noindex:

.. java:type:: public class PXGrid extends PXLayer

   A Grid layer which is zoomable and pannable.

   :author: Shu Wu

Constructors
------------
PXGrid
^^^^^^

.. java:constructor:: public PXGrid(Color gridPaint, double gridSpacing)
   :outertype: PXGrid

Methods
-------
createGrid
^^^^^^^^^^

.. java:method:: public static PXLayer createGrid(PCamera camera, PRoot root, Color gridPaint, double gridSpacing)
   :outertype: PXGrid

isGridVisible
^^^^^^^^^^^^^

.. java:method:: public static boolean isGridVisible()
   :outertype: PXGrid

paint
^^^^^

.. java:method:: @Override protected void paint(PPaintContext paintContext)
   :outertype: PXGrid

setGridVisible
^^^^^^^^^^^^^^

.. java:method:: public static void setGridVisible(boolean gridVisible)
   :outertype: PXGrid

