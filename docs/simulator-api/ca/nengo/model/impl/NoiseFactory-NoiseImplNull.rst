.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

NoiseFactory.NoiseImplNull
==========================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public static class NoiseImplNull implements Noise
   :outertype: NoiseFactory

   Zero additive Noise

Methods
-------
clone
^^^^^

.. java:method:: @Override public Noise clone()
   :outertype: NoiseFactory.NoiseImplNull

getValue
^^^^^^^^

.. java:method:: public float getValue(float startTime, float endTime, float input)
   :outertype: NoiseFactory.NoiseImplNull

   **See also:** :java:ref:`ca.nengo.model.Noise.getValue(float,float,float)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: NoiseFactory.NoiseImplNull

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

