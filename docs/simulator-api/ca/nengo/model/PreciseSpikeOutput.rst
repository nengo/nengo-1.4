PreciseSpikeOutput
==================

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface PreciseSpikeOutput extends SpikeOutput

   InstantaneousOutput consisting of spikes and the time since they occurred.

   :author: Terry Stewart

Methods
-------
getSpikeTimes
^^^^^^^^^^^^^

.. java:method:: public float[] getSpikeTimes()
   :outertype: PreciseSpikeOutput

   :return: The times when the spikes occurred, as offsets from the previous time step. Values negative values indicate no spike.

