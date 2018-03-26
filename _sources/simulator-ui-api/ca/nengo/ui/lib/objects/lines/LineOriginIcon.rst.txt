.. java:import:: java.awt Color

.. java:import:: java.awt Graphics2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world PaintContext

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

LineOriginIcon
==============

.. java:package:: ca.nengo.ui.lib.objects.lines
   :noindex:

.. java:type:: public class LineOriginIcon extends WorldObjectImpl

   Icon for a line end well

   :author: Shu Wu

Fields
------
ICON_RADIUS
^^^^^^^^^^^

.. java:field:: protected static final double ICON_RADIUS
   :outertype: LineOriginIcon

Constructors
------------
LineOriginIcon
^^^^^^^^^^^^^^

.. java:constructor:: public LineOriginIcon()
   :outertype: LineOriginIcon

Methods
-------
getColor
^^^^^^^^

.. java:method:: public Color getColor()
   :outertype: LineOriginIcon

paint
^^^^^

.. java:method:: @Override public void paint(PaintContext paintContext)
   :outertype: LineOriginIcon

setColor
^^^^^^^^

.. java:method:: public void setColor(Color color)
   :outertype: LineOriginIcon

