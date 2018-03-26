.. java:import:: java.util Properties

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math RootFinder

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl NewtonRootFinder

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl PreciseSpikeOutputImpl

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

ALIFSpikeGenerator
==================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class ALIFSpikeGenerator implements SpikeGenerator, Probeable

   An adapting leaky-integrate-and-fire model of spike generation. The mechanism of adaptation is a current I_ahp that is related to firing frequency. This current is proportional to the concentration of an ion species N, as I_ahp = -g_N * [N]. [N] increases with every spike and decays between spikes, as follows: d[N]/dt = -[N]/tau_N + inc_N sum_k(delta(t-t_k).

   This form is taken from La Camera et al. (2004) Minimal models of adapted neuronal response to in vivo-like input currents, Neural Computation 16, 2101-24. This form of adaptation (as opposed to variation in firing threshold or membrane time constant) is convenient because it allows a rate model as well as a spiking model.

   Some example parameter values are the same as a standard LIF, plus TauN = .2; incN = .05 - .2. for greater amounts of differing adaptation, increase the range of incN

   TODO: unit tests (particularly verify numbers of spikes and rate match in various cases -- they seem to)

   :author: Bryan Tripp

Constructors
------------
ALIFSpikeGenerator
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ALIFSpikeGenerator()
   :outertype: ALIFSpikeGenerator

   Uses default parameters

ALIFSpikeGenerator
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ALIFSpikeGenerator(float tauRef, float tauRC, float tauN, float incN)
   :outertype: ALIFSpikeGenerator

   :param tauRef: Refracory period (s)
   :param tauRC: Resistive-capacitive time constant (s)
   :param tauN: Time constant of adaptation-related ion
   :param incN: Increment of adaptation-related ion with each spike

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: ALIFSpikeGenerator

getAdaptedRate
^^^^^^^^^^^^^^

.. java:method:: public float getAdaptedRate(float I)
   :outertype: ALIFSpikeGenerator

   :param I: driving current
   :return: Adapted firing rate given this current

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: ALIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getIncN
^^^^^^^

.. java:method:: public float getIncN()
   :outertype: ALIFSpikeGenerator

   :return: Increment of adaptation-related ion with each spike

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: ALIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getOnsetRate
^^^^^^^^^^^^

.. java:method:: public float getOnsetRate(float I)
   :outertype: ALIFSpikeGenerator

   :param I: driving current
   :return: Unadapted firing rate given this current

getTauN
^^^^^^^

.. java:method:: public float getTauN()
   :outertype: ALIFSpikeGenerator

   :return: Time constant of adaptation-related ion

getTauRC
^^^^^^^^

.. java:method:: public float getTauRC()
   :outertype: ALIFSpikeGenerator

   :return: Resistive-capacitive time constant (s)

getTauRef
^^^^^^^^^

.. java:method:: public float getTauRef()
   :outertype: ALIFSpikeGenerator

   :return: Refracory period (s)

getVoltage
^^^^^^^^^^

.. java:method:: public float getVoltage()
   :outertype: ALIFSpikeGenerator

   :return: Current membrane voltage

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: ALIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: ALIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: ALIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.neuron.SpikeGenerator.run(float[],float[])`

setIncN
^^^^^^^

.. java:method:: public void setIncN(float incN)
   :outertype: ALIFSpikeGenerator

   :param incN: Increment of adaptation-related ion with each spike

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: ALIFSpikeGenerator

   DEFAULT and RATE are supported.

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setTauN
^^^^^^^

.. java:method:: public void setTauN(float tauN)
   :outertype: ALIFSpikeGenerator

   :param tauN: Time constant of adaptation-related ion

setTauRC
^^^^^^^^

.. java:method:: public void setTauRC(float tauRC)
   :outertype: ALIFSpikeGenerator

   :param tauRC: Resistive-capacitive time constant (s)

setTauRef
^^^^^^^^^

.. java:method:: public void setTauRef(float tauRef)
   :outertype: ALIFSpikeGenerator

   :param tauRef: Refracory period (s)

