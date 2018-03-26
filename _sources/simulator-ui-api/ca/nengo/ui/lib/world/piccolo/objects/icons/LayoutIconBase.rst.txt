.. java:import:: java.awt BasicStroke

.. java:import:: java.awt Graphics2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world PaintContext

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

LayoutIconBase
==============

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects.icons
   :noindex:

.. java:type:: public abstract class LayoutIconBase extends WorldObjectImpl

Fields
------
PADDING
^^^^^^^

.. java:field:: public static int PADDING
   :outertype: LayoutIconBase

STROKE_WIDTH
^^^^^^^^^^^^

.. java:field:: public static int STROKE_WIDTH
   :outertype: LayoutIconBase

Constructors
------------
LayoutIconBase
^^^^^^^^^^^^^^

.. java:constructor:: public LayoutIconBase(int size)
   :outertype: LayoutIconBase

Methods
-------
getSize
^^^^^^^

.. java:method:: public int getSize()
   :outertype: LayoutIconBase

paint
^^^^^

.. java:method:: @Override public void paint(PaintContext paintContext)
   :outertype: LayoutIconBase

paintIcon
^^^^^^^^^

.. java:method:: protected abstract void paintIcon(Graphics2D g2)
   :outertype: LayoutIconBase

