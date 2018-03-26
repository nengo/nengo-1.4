.. java:import:: java.awt Color

.. java:import:: java.awt Cursor

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

HandCursorHandler
=================

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type::  class HandCursorHandler extends PBasicInputEventHandler

   Changes the mouse cursor to a hand when it enters the object

   :author: Shu Wu

Methods
-------
mouseEntered
^^^^^^^^^^^^

.. java:method:: @Override public void mouseEntered(PInputEvent event)
   :outertype: HandCursorHandler

mouseExited
^^^^^^^^^^^

.. java:method:: @Override public void mouseExited(PInputEvent event)
   :outertype: HandCursorHandler

