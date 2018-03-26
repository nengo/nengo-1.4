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

BrainImageWrapper
=================

.. java:package:: ca.nengo.ui.brainView
   :noindex:

.. java:type::  class BrainImageWrapper extends WorldObjectImpl

Fields
------
myBrainImage
^^^^^^^^^^^^

.. java:field::  AbstractBrainImage2D myBrainImage
   :outertype: BrainImageWrapper

myLabel
^^^^^^^

.. java:field::  Text myLabel
   :outertype: BrainImageWrapper

Constructors
------------
BrainImageWrapper
^^^^^^^^^^^^^^^^^

.. java:constructor:: public BrainImageWrapper(AbstractBrainImage2D brainImage)
   :outertype: BrainImageWrapper

Methods
-------
layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override public void layoutChildren()
   :outertype: BrainImageWrapper

