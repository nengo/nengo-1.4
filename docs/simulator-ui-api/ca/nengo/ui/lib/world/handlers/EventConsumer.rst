.. java:import:: edu.umd.cs.piccolo.event PInputEvent

.. java:import:: edu.umd.cs.piccolo.event PInputEventListener

EventConsumer
=============

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public class EventConsumer implements PInputEventListener

   Handler which consumes all events passed to it. Used when an object wants to shield its events from the outside.

   :author: Shu Wu

Methods
-------
processEvent
^^^^^^^^^^^^

.. java:method:: public void processEvent(PInputEvent aEvent, int type)
   :outertype: EventConsumer

