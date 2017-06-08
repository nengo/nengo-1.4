.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

NoiseFactory.NoiseImplFunction
==============================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public static class NoiseImplFunction implements Noise
   :outertype: NoiseFactory

   Note: there are no public setters here for the same reason as in NoiseImplPDF.

   :author: Bryan Tripp

Constructors
------------
NoiseImplFunction
^^^^^^^^^^^^^^^^^

.. java:constructor:: public NoiseImplFunction(Function function)
   :outertype: NoiseFactory.NoiseImplFunction

   :param function: A function of time that explicitly defines the noise

NoiseImplFunction
^^^^^^^^^^^^^^^^^

.. java:constructor:: public NoiseImplFunction()
   :outertype: NoiseFactory.NoiseImplFunction

   Default zero noise.

Methods
-------
clone
^^^^^

.. java:method:: @Override public Noise clone()
   :outertype: NoiseFactory.NoiseImplFunction

getFunction
^^^^^^^^^^^

.. java:method:: public Function getFunction()
   :outertype: NoiseFactory.NoiseImplFunction

   :return: The function of time that explicitly defines the noise

getValue
^^^^^^^^

.. java:method:: public float getValue(float startTime, float endTime, float input)
   :outertype: NoiseFactory.NoiseImplFunction

   **See also:** :java:ref:`ca.nengo.model.Noise.getValue(float,float,float)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: NoiseFactory.NoiseImplFunction

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

