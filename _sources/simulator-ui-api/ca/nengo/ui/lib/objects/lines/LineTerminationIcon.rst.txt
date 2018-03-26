.. java:import:: java.awt Color

.. java:import:: java.awt Graphics2D

.. java:import:: java.awt.geom Area

.. java:import:: java.awt.geom Ellipse2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world PaintContext

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

LineTerminationIcon
===================

.. java:package:: ca.nengo.ui.lib.objects.lines
   :noindex:

.. java:type:: public class LineTerminationIcon extends WorldObjectImpl

   Standard Icon for a line end holder

   :author: Shu Wu

Fields
------
LINE_IN_HEIGHT
^^^^^^^^^^^^^^

.. java:field:: static final int LINE_IN_HEIGHT
   :outertype: LineTerminationIcon

LINE_IN_WIDTH
^^^^^^^^^^^^^

.. java:field:: static final int LINE_IN_WIDTH
   :outertype: LineTerminationIcon

Constructors
------------
LineTerminationIcon
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public LineTerminationIcon()
   :outertype: LineTerminationIcon

Methods
-------
getColor
^^^^^^^^

.. java:method:: public Color getColor()
   :outertype: LineTerminationIcon

paint
^^^^^

.. java:method:: @Override public void paint(PaintContext paintContext)
   :outertype: LineTerminationIcon

setColor
^^^^^^^^

.. java:method:: public void setColor(Color color)
   :outertype: LineTerminationIcon

