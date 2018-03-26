.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util SpikePattern

SpikePatternImpl
================

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class SpikePatternImpl implements SpikePattern

   Default implementation of SpikePattern.

   :author: Bryan Tripp

Fields
------
myIndices
^^^^^^^^^

.. java:field::  int[] myIndices
   :outertype: SpikePatternImpl

mySpikeTimes
^^^^^^^^^^^^

.. java:field::  float[][] mySpikeTimes
   :outertype: SpikePatternImpl

Constructors
------------
SpikePatternImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public SpikePatternImpl(int neurons)
   :outertype: SpikePatternImpl

   :param neurons: Number of neurons in the Ensemble that this SpikePattern belongs to

Methods
-------
addSpike
^^^^^^^^

.. java:method:: public void addSpike(int neuron, float time)
   :outertype: SpikePatternImpl

   :param neuron: Index of neuron
   :param time: Spike time

clone
^^^^^

.. java:method:: @Override public SpikePattern clone() throws CloneNotSupportedException
   :outertype: SpikePatternImpl

getNumNeurons
^^^^^^^^^^^^^

.. java:method:: public int getNumNeurons()
   :outertype: SpikePatternImpl

   **See also:** :java:ref:`ca.nengo.util.SpikePattern.getNumNeurons()`

getSpikeTimes
^^^^^^^^^^^^^

.. java:method:: public float[] getSpikeTimes(int neuron)
   :outertype: SpikePatternImpl

   **See also:** :java:ref:`ca.nengo.util.SpikePattern.getSpikeTimes(int)`

