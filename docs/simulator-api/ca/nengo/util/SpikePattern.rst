.. java:import:: java.io Serializable

SpikePattern
============

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface SpikePattern extends Serializable, Cloneable

   A temporal pattern of spiking in an Ensemble.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public SpikePattern clone() throws CloneNotSupportedException
   :outertype: SpikePattern

getNumNeurons
^^^^^^^^^^^^^

.. java:method:: public int getNumNeurons()
   :outertype: SpikePattern

   :return: Number of neurons in the ensemble

getSpikeTimes
^^^^^^^^^^^^^

.. java:method:: public float[] getSpikeTimes(int neuron)
   :outertype: SpikePattern

   :param neuron: Index of a neuron in the ensemble (from 0)
   :return: Times at which neuron spiked since the Ensemble was last reset

