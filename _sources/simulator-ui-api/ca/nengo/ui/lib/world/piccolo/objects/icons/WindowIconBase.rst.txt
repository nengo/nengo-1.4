.. java:import:: java.awt BasicStroke

.. java:import:: java.awt Graphics2D

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world PaintContext

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

WindowIconBase
==============

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects.icons
   :noindex:

.. java:type:: public abstract class WindowIconBase extends WorldObjectImpl

Fields
------
PADDING
^^^^^^^

.. java:field:: public static int PADDING
   :outertype: WindowIconBase

Constructors
------------
WindowIconBase
^^^^^^^^^^^^^^

.. java:constructor:: public WindowIconBase(int size)
   :outertype: WindowIconBase

Methods
-------
getSize
^^^^^^^

.. java:method:: public int getSize()
   :outertype: WindowIconBase

paint
^^^^^

.. java:method:: @Override public void paint(PaintContext paintContext)
   :outertype: WindowIconBase

paintIcon
^^^^^^^^^

.. java:method:: protected abstract void paintIcon(Graphics2D g2)
   :outertype: WindowIconBase

