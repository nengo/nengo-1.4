.. java:import:: ca.nengo.model PreciseSpikeOutput

.. java:import:: ca.nengo.model Units

PreciseSpikeOutputImpl
======================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class PreciseSpikeOutputImpl implements PreciseSpikeOutput

   A class for representing precise spike times. Does this mean spike times between timesteps?

Constructors
------------
PreciseSpikeOutputImpl
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PreciseSpikeOutputImpl(float[] spikeTimes, Units units, float time)
   :outertype: PreciseSpikeOutputImpl

   :param spikeTimes: @see #getSpikeTimes()
   :param units: @see #getUnits()
   :param time: @see #getTime()

Methods
-------
clone
^^^^^

.. java:method:: @Override public PreciseSpikeOutput clone() throws CloneNotSupportedException
   :outertype: PreciseSpikeOutputImpl

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: PreciseSpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getDimension()`

getSpikeTimes
^^^^^^^^^^^^^

.. java:method:: public float[] getSpikeTimes()
   :outertype: PreciseSpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.PreciseSpikeOutput.getSpikeTimes()`

getTime
^^^^^^^

.. java:method:: public float getTime()
   :outertype: PreciseSpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getTime()`

getUnits
^^^^^^^^

.. java:method:: public Units getUnits()
   :outertype: PreciseSpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.InstantaneousOutput.getUnits()`

getValues
^^^^^^^^^

.. java:method:: public boolean[] getValues()
   :outertype: PreciseSpikeOutputImpl

   **See also:** :java:ref:`ca.nengo.model.SpikeOutput.getValues()`

