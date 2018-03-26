.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.handlers AbstractStatusHandler

.. java:import:: ca.nengo.ui.lib.world.handlers EventConsumer

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldGroundImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives PXImage

.. java:import:: ca.nengo.ui.lib.world.piccolo.primitives Text

.. java:import:: edu.umd.cs.piccolo.event PDragSequenceEventHandler

.. java:import:: edu.umd.cs.piccolo.event PInputEvent

BrainViewStatusHandler
======================

.. java:package:: ca.nengo.ui.brainView
   :noindex:

.. java:type::  class BrainViewStatusHandler extends AbstractStatusHandler

Constructors
------------
BrainViewStatusHandler
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public BrainViewStatusHandler(BrainViewer world)
   :outertype: BrainViewStatusHandler

Methods
-------
getStatusMessage
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected String getStatusMessage(PInputEvent event)
   :outertype: BrainViewStatusHandler

getWorld
^^^^^^^^

.. java:method:: @Override protected BrainViewer getWorld()
   :outertype: BrainViewStatusHandler

