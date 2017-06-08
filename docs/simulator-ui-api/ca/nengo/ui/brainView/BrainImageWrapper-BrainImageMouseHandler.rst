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

BrainImageWrapper.BrainImageMouseHandler
========================================

.. java:package:: ca.nengo.ui.brainView
   :noindex:

.. java:type::  class BrainImageMouseHandler extends PDragSequenceEventHandler
   :outertype: BrainImageWrapper

Fields
------
roundingError
^^^^^^^^^^^^^

.. java:field::  double roundingError
   :outertype: BrainImageWrapper.BrainImageMouseHandler

Methods
-------
dragActivityStep
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void dragActivityStep(PInputEvent aEvent)
   :outertype: BrainImageWrapper.BrainImageMouseHandler

