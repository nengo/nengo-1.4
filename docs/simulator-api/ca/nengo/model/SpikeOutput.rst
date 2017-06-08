SpikeOutput
===========

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface SpikeOutput extends InstantaneousOutput

   InstantaneousOutput consisting of spikes.

   :author: Bryan Tripp

Methods
-------
getValues
^^^^^^^^^

.. java:method:: public boolean[] getValues()
   :outertype: SpikeOutput

   :return: Instantaneous output in spiking channels (true means spike; false means no spike).

