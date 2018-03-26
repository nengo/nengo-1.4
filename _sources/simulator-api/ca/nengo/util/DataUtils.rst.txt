.. java:import:: java.util Arrays

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.dynamics.impl LTISystem

.. java:import:: ca.nengo.dynamics.impl SimpleLTISystem

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: ca.nengo.util.impl SpikePatternImpl

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

DataUtils
=========

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public class DataUtils

   Tools manipulating TimeSeries and SpikePattern data. TODO: test; remove Plotter.filter() references

   :author: Bryan Tripp

Methods
-------
extractDimension
^^^^^^^^^^^^^^^^

.. java:method:: public static TimeSeries extractDimension(TimeSeries series, int dim)
   :outertype: DataUtils

   :param series: An n-dimensional TimeSeries
   :param dim: Index (less than n-1) of dimension to extract
   :return: One-dimensional TimeSeries composed of extracted dimension

extractTime
^^^^^^^^^^^

.. java:method:: public static TimeSeries extractTime(TimeSeries series, float start, float end)
   :outertype: DataUtils

   :param series: Any TimeSeries
   :param start: Beginning of extracted portion of series
   :param end: End of extracted portion of series
   :return: A TimeSeries that includes any samples in the given TimeSeries between the start and end times

filter
^^^^^^

.. java:method:: public static TimeSeries filter(TimeSeries series, float tau)
   :outertype: DataUtils

   :param series: A TimeSeries to which to apply a 1-D linear filter
   :param tau: Filter time constant
   :return: Filtered TimeSeries

sort
^^^^

.. java:method:: public static SpikePattern sort(SpikePattern pattern, Ensemble ensemble)
   :outertype: DataUtils

   Attempts to sort a SpikePattern by properties of the associated neurons.

   :param pattern: A SpikePattern
   :param ensemble: Ensemble from which spikes come
   :return: A SpikePattern that is re-ordered according to neuron properties, if possible

subsample
^^^^^^^^^

.. java:method:: public static TimeSeries subsample(TimeSeries series, int period)
   :outertype: DataUtils

   Draws one of every \ ``period``\  samples from a given TimeSeries.

   :param series: Any TimeSeries
   :param period: The sub-sampling period
   :return: New TimeSeries composed of one of every \ ``period``\  samples in the original

subset
^^^^^^

.. java:method:: public static SpikePattern subset(SpikePattern pattern, int start, int interval, int end)
   :outertype: DataUtils

   Extracts spikes of selected neurons from a given SpikePattern.

   :param pattern: Any SpikePattern
   :param start: Neuron number at which to start extraction
   :param interval: Spikes are taken from one every \ ``interval``\  neurons
   :param end: Neuron number at which to end extraction
   :return: Spikes from selected neurons in the original pattern

subset
^^^^^^

.. java:method:: public static SpikePattern subset(SpikePattern pattern, int[] indices)
   :outertype: DataUtils

   Extracts spikes of selected neurons from a given SpikePattern.

   :param pattern: Any SpikePattern
   :param indices: Indices of neurons in original pattern from which to extract spikes
   :return: Spikes from selected neurons in the original pattern

