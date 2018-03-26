.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.activities PActivity

.. java:import:: edu.umd.cs.piccolo.activities PActivity.PActivityDelegate

Pulsator
========

.. java:package:: ca.nengo.ui.lib.world.activities
   :noindex:

.. java:type:: public class Pulsator

   Pulsates the target World Object until finished.

   :author: Shu Wu

Fields
------
PULSATION_RATE_PER_SEC
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final long PULSATION_RATE_PER_SEC
   :outertype: Pulsator

fadeActivity
^^^^^^^^^^^^

.. java:field::  PActivity fadeActivity
   :outertype: Pulsator

myFaderDelegate
^^^^^^^^^^^^^^^

.. java:field::  PActivityDelegate myFaderDelegate
   :outertype: Pulsator

pulsationState
^^^^^^^^^^^^^^

.. java:field::  PulsationState pulsationState
   :outertype: Pulsator

Constructors
------------
Pulsator
^^^^^^^^

.. java:constructor:: public Pulsator(WorldObjectImpl wo)
   :outertype: Pulsator

Methods
-------
finish
^^^^^^

.. java:method:: public void finish()
   :outertype: Pulsator

