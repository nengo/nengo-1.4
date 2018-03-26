.. java:import:: java.awt.event MouseEvent

.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.lang.ref WeakReference

.. java:import:: javax.swing JPopupMenu

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world Interactable

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.handlers EventConsumer

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons CloseIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons MaximizeIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons MinimizeIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.objects.icons RestoreIcon

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Path

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.event PInputEventListener

.. java:import:: edu.umd.cs.piccolox.nodes PClip

MenuBar
=======

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type::  class MenuBar extends WorldObjectImpl implements PInputEventListener

Constructors
------------
MenuBar
^^^^^^^

.. java:constructor:: public MenuBar(Window window)
   :outertype: MenuBar

Methods
-------
layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override public void layoutChildren()
   :outertype: MenuBar

processEvent
^^^^^^^^^^^^

.. java:method:: public void processEvent(PInputEvent event, int type)
   :outertype: MenuBar

setHighlighted
^^^^^^^^^^^^^^

.. java:method:: public void setHighlighted(boolean bool)
   :outertype: MenuBar

updateButtons
^^^^^^^^^^^^^

.. java:method:: public void updateButtons()
   :outertype: MenuBar

