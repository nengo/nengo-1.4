.. java:import:: java.util Properties

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl IndicatorPDF

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

LIFSpikeGenerator
=================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class LIFSpikeGenerator implements SpikeGenerator, Probeable

   A leaky-integrate-and-fire model of spike generation. From Koch, 1999, the subthreshold model is: C dV(t)/dt + V(t)/R = I(t). When V reaches a threshold, a spike occurs (spike-related currents are not modelled).

   For simplicity we take Vth = R = 1, which does not limit the behaviour of the model, although transformations may be needed if it is desired to convert to more realistic parameter ranges.

   :author: Bryan Tripp

Constructors
------------
LIFSpikeGenerator
^^^^^^^^^^^^^^^^^

.. java:constructor:: public LIFSpikeGenerator()
   :outertype: LIFSpikeGenerator

   Uses default values.

LIFSpikeGenerator
^^^^^^^^^^^^^^^^^

.. java:constructor:: public LIFSpikeGenerator(float maxTimeStep, float tauRC, float tauRef)
   :outertype: LIFSpikeGenerator

   :param maxTimeStep: maximum integration time step (s). Shorter time steps may be used if a run(...) is requested with a length that is not an integer multiple of this value.
   :param tauRC: resistive-capacitive time constant (s)
   :param tauRef: refracory period (s)

LIFSpikeGenerator
^^^^^^^^^^^^^^^^^

.. java:constructor:: public LIFSpikeGenerator(float maxTimeStep, float tauRC, float tauRef, float initialVoltage)
   :outertype: LIFSpikeGenerator

   :param maxTimeStep: Maximum integration time step (s). Shorter time steps may be used if a run(...) is requested with a length that is not an integer multiple of this value.
   :param tauRC: Resistive-capacitive time constant (s)
   :param tauRef: Refracory period (s)
   :param initialVoltage: Initial condition on V

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: LIFSpikeGenerator

constantRateRun
^^^^^^^^^^^^^^^

.. java:method:: public float constantRateRun(float current)
   :outertype: LIFSpikeGenerator

   :param current: Given current
   :return: Result of solving for activity given current

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: LIFSpikeGenerator

   **See also:** :java:ref:`Probeable.getHistory(String)`

getMaxTimeStep
^^^^^^^^^^^^^^

.. java:method:: public float getMaxTimeStep()
   :outertype: LIFSpikeGenerator

   :return: Maximum integration time step (s).

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: LIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getTauRC
^^^^^^^^

.. java:method:: public float getTauRC()
   :outertype: LIFSpikeGenerator

   :return: Resistive-capacitive time constant (s)

getTauRef
^^^^^^^^^

.. java:method:: public float getTauRef()
   :outertype: LIFSpikeGenerator

   :return: Refracory period (s)

getVoltage
^^^^^^^^^^

.. java:method:: public float getVoltage()
   :outertype: LIFSpikeGenerator

   :return: membrane voltage

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: LIFSpikeGenerator

   **See also:** :java:ref:`Probeable.listStates()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: LIFSpikeGenerator

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: LIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.neuron.SpikeGenerator.run(float[],float[])`

setMaxTimeStep
^^^^^^^^^^^^^^

.. java:method:: public void setMaxTimeStep(float max)
   :outertype: LIFSpikeGenerator

   :param max: Maximum integration time step (s).

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: LIFSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setTauRC
^^^^^^^^

.. java:method:: public void setTauRC(float tauRC)
   :outertype: LIFSpikeGenerator

   :param tauRC: Resistive-capacitive time constant (s)

setTauRef
^^^^^^^^^

.. java:method:: public void setTauRef(float tauRef)
   :outertype: LIFSpikeGenerator

   :param tauRef: Refracory period (s)

