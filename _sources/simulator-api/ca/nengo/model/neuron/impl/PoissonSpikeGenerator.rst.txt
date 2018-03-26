.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math PDFTools

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl LinearFunction

.. java:import:: ca.nengo.math.impl PoissonPDF

.. java:import:: ca.nengo.math.impl SigmoidFunction

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.util MU

PoissonSpikeGenerator
=====================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class PoissonSpikeGenerator implements SpikeGenerator

   A phenomenological SpikeGenerator that produces spikes according to a Poisson process with a rate that varies as a function of current. TODO: test

   :author: Bryan Tripp

Constructors
------------
PoissonSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PoissonSpikeGenerator(Function rateFunction)
   :outertype: PoissonSpikeGenerator

   :param rateFunction: Maps input current to Poisson spiking rate

PoissonSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PoissonSpikeGenerator()
   :outertype: PoissonSpikeGenerator

   Uses a default sigmoid rate function

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: PoissonSpikeGenerator

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: PoissonSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getRateFunction
^^^^^^^^^^^^^^^

.. java:method:: public Function getRateFunction()
   :outertype: PoissonSpikeGenerator

   :return: Function that maps input current to Poisson spiking rate

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: PoissonSpikeGenerator

   This method does nothing, because a Poisson process is stateless.

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: PoissonSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.neuron.SpikeGenerator.run(float[],float[])`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: PoissonSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setRateFunction
^^^^^^^^^^^^^^^

.. java:method:: public void setRateFunction(Function function)
   :outertype: PoissonSpikeGenerator

   :param function: Function that maps input current to Poisson spiking rate

