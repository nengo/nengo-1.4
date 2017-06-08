.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDFTools

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util MU

RateFunctionSpikeGenerator
==========================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class RateFunctionSpikeGenerator implements SpikeGenerator

   Rate Function Spike Generator This class generates spikes based on a user defined function. Initially, spikes should be generated without any sort of random distribution (eventually this may change) Modified version of LIFSpikeGenerator, original code taken from other SpikeGenerator classes.

   :author: Bryan Tripp

Constructors
------------
RateFunctionSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RateFunctionSpikeGenerator(Function rateFunction)
   :outertype: RateFunctionSpikeGenerator

   :param rateFunction: Maps input current to spiking rate

RateFunctionSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RateFunctionSpikeGenerator(Function rateFunction, boolean smooth)
   :outertype: RateFunctionSpikeGenerator

   :param rateFunction: Maps input current to spiking rate
   :param smooth: Apply smoothing?

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: RateFunctionSpikeGenerator

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: RateFunctionSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getRateFunction
^^^^^^^^^^^^^^^

.. java:method:: public Function getRateFunction()
   :outertype: RateFunctionSpikeGenerator

   :return: Function that maps input current to spiking rate

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: RateFunctionSpikeGenerator

   useless method for current implementations

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: RateFunctionSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.neuron.SpikeGenerator.run(float[],float[])`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: RateFunctionSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setRateFunction
^^^^^^^^^^^^^^^

.. java:method:: public void setRateFunction(Function function)
   :outertype: RateFunctionSpikeGenerator

   :param function: Function that maps input current to spiking rate

