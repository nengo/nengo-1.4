.. java:import:: java.text NumberFormat

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

TopWorldStatusHandler
=====================

.. java:package:: ca.nengo.ui.lib.world.handlers
   :noindex:

.. java:type:: public class TopWorldStatusHandler extends AbstractStatusHandler

   Shows the mouse coordinates using the status bar

   :author: Shu Wu

Constructors
------------
TopWorldStatusHandler
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TopWorldStatusHandler(WorldImpl world)
   :outertype: TopWorldStatusHandler

Methods
-------
getStatusMessage
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected String getStatusMessage(PInputEvent event)
   :outertype: TopWorldStatusHandler

   :param event: Input event
   :return: String related to that event

