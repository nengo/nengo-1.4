.. java:import:: java.io Serializable

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model SimulationMode

SpikeGenerator
==============

.. java:package:: ca.nengo.model.neuron
   :noindex:

.. java:type:: public interface SpikeGenerator extends Resettable, Serializable, SimulationMode.ModeConfigurable, Cloneable

   Spike generation model, ie a component of a neuron model that receives driving current and generates spikes.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: SpikeGenerator

   :throws CloneNotSupportedException: if clone can't be made
   :return: Valid clone

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: SpikeGenerator

   Runs the model for a given time segment. The total time is meant to be short (eg 1/2ms), in that the output of the model is either a spike or no spike during this period of simulation time.

   The model is responsible for maintaining its internal state, and the state is assumed to be consistent with the start time. That is, if a caller calls run({.001 .002}, ...) and then run({.501 .502}, ...), the results may not make any sense, but this is not the model's responsibility. Absolute times are provided to support explicitly time-varying models, and for the convenience of Probeable models.

   :param time: Array of points in time at which input current is defined. This includes at least the start and end times, and possibly intermediate times. (The SpikeGenerator model can use its own time step -- these times are only used to define the input.)
   :param current: Driving current at each given point in time (assumed to be constant until next time point)
   :return: true If there is a spike between the first and last times, false otherwise

