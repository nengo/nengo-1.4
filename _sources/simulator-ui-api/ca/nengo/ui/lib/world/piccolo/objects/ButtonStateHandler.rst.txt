.. java:import:: java.awt Color

.. java:import:: java.awt Cursor

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

ButtonStateHandler
==================

.. java:package:: ca.nengo.ui.lib.world.piccolo.objects
   :noindex:

.. java:type::  class ButtonStateHandler extends PBasicInputEventHandler

   Changes the button state from mouse events

   :author: Shu Wu

Constructors
------------
ButtonStateHandler
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ButtonStateHandler(AbstractButton button)
   :outertype: ButtonStateHandler

Methods
-------
mouseClicked
^^^^^^^^^^^^

.. java:method:: @Override public void mouseClicked(PInputEvent event)
   :outertype: ButtonStateHandler

mouseEntered
^^^^^^^^^^^^

.. java:method:: @Override public void mouseEntered(PInputEvent event)
   :outertype: ButtonStateHandler

mouseExited
^^^^^^^^^^^

.. java:method:: @Override public void mouseExited(PInputEvent event)
   :outertype: ButtonStateHandler

mousePressed
^^^^^^^^^^^^

.. java:method:: @Override public void mousePressed(PInputEvent event)
   :outertype: ButtonStateHandler

mouseReleased
^^^^^^^^^^^^^

.. java:method:: @Override public void mouseReleased(PInputEvent event)
   :outertype: ButtonStateHandler

