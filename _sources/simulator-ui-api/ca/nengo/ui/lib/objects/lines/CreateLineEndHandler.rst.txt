.. java:import:: java.awt Color

.. java:import:: java.awt.event MouseEvent

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.event PBasicInputEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.util PPickPath

CreateLineEndHandler
====================

.. java:package:: ca.nengo.ui.lib.objects.lines
   :noindex:

.. java:type::  class CreateLineEndHandler extends PBasicInputEventHandler

   This handler listens for mouse events on the line end well and creates new line ends when needed.

   :author: Shu Wu

Constructors
------------
CreateLineEndHandler
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CreateLineEndHandler(LineWell lineEndWell)
   :outertype: CreateLineEndHandler

Methods
-------
mousePressed
^^^^^^^^^^^^

.. java:method:: @Override public void mousePressed(PInputEvent event)
   :outertype: CreateLineEndHandler

