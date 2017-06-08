.. java:import:: java.util Arrays

.. java:import:: java.util Properties

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.dynamics.impl SimpleLTISystem

.. java:import:: ca.nengo.math CurveFitter

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl LinearCurveFitter

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

DynamicalSystemSpikeGenerator
=============================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class DynamicalSystemSpikeGenerator implements SpikeGenerator, Probeable

   A SpikeGenerator for which spiking dynamics are expressed in terms of a DynamicalSystem.

   :author: Bryan Tripp

Fields
------
DYNAMICS
^^^^^^^^

.. java:field:: public static final String DYNAMICS
   :outertype: DynamicalSystemSpikeGenerator

   Default name for dynamics?

Constructors
------------
DynamicalSystemSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DynamicalSystemSpikeGenerator(DynamicalSystem dynamics, Integrator integrator, int voltageDim, float spikeThreshold, float minIntraSpikeTime)
   :outertype: DynamicalSystemSpikeGenerator

   :param dynamics: A DynamicalSystem that defines the dynamics of spike generation.
   :param integrator: An integrator with which to simulate the DynamicalSystem
   :param voltageDim: Dimension of output that corresponds to membrane potential
   :param spikeThreshold: Threshold membrane potential at which a spike is considered to have occurred
   :param minIntraSpikeTime: Minimum time between spike onsets. If there appears to be a spike onset at the beginning of a timestep, this value is used to determine whether this is just the continuation of a spike onset that was already registered in the last timestep

DynamicalSystemSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DynamicalSystemSpikeGenerator(DynamicalSystem dynamics, Integrator integrator, int voltageDim, float spikeThreshold, float minIntraSpikeTime, float[] currentRange, float transientTime)
   :outertype: DynamicalSystemSpikeGenerator

   Creates a SpikeGenerator that supports CONSTANT_RATE mode. The rate for a given driving current is estimated by interpolating steady-state spike counts for simulations with different driving currents (given in the currents arg).

   :param dynamics: A DynamicalSystem that defines the dynamics of spike generation.
   :param integrator: An integrator with which to simulate the DynamicalSystem
   :param voltageDim: Dimension of output that corresponds to membrane potential
   :param spikeThreshold: Threshold membrane potential at which a spike is considered to have occurred
   :param minIntraSpikeTime: Minimum time between spike onsets. If there appears to be a spike onset at the beginning of a timestep, this value is used to determine whether this is just the continuation of a spike onset that was already registered in the last timestep
   :param currentRange: Range of driving currents at which to simulate to find steady-state firing rates for CONSTANT_RATE mode
   :param transientTime: Simulation time to ignore before counting spikes when finding steady-state rates

DynamicalSystemSpikeGenerator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DynamicalSystemSpikeGenerator()
   :outertype: DynamicalSystemSpikeGenerator

   Uses default parameters to allow later configuration.

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: DynamicalSystemSpikeGenerator

getConstantRateModeSupported
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean getConstantRateModeSupported()
   :outertype: DynamicalSystemSpikeGenerator

   :return: True if this SpikeGenerator supports CONSTANT_RATE simulation mode

getCurrentRange
^^^^^^^^^^^^^^^

.. java:method:: public float[] getCurrentRange()
   :outertype: DynamicalSystemSpikeGenerator

   :return: Range of driving currents at which to simulate to find steady-state firing rates for CONSTANT_RATE mode

getDynamics
^^^^^^^^^^^

.. java:method:: public DynamicalSystem getDynamics()
   :outertype: DynamicalSystemSpikeGenerator

   :return: A DynamicalSystem that defines the dynamics of spike generation.

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: DynamicalSystemSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getIntegrator
^^^^^^^^^^^^^

.. java:method:: public Integrator getIntegrator()
   :outertype: DynamicalSystemSpikeGenerator

   :return: An integrator with which to simulate the DynamicalSystem

getMinIntraSpikeTime
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public float getMinIntraSpikeTime()
   :outertype: DynamicalSystemSpikeGenerator

   :return: Minimum time between spike onsets. If there appears to be a spike onset at the beginning of a timestep, this value is used to determine whether this is just the continuation of a spike onset that was already registered in the last timestep

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: DynamicalSystemSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getSpikeThreshold
^^^^^^^^^^^^^^^^^

.. java:method:: public float getSpikeThreshold()
   :outertype: DynamicalSystemSpikeGenerator

   :return: Threshold membrane potential at which a spike is considered to have occurred

getTransientTime
^^^^^^^^^^^^^^^^

.. java:method:: public float getTransientTime()
   :outertype: DynamicalSystemSpikeGenerator

   :return: Simulation time to ignore before counting spikes when finding steady-state rates

getVoltageDim
^^^^^^^^^^^^^

.. java:method:: public int getVoltageDim()
   :outertype: DynamicalSystemSpikeGenerator

   :return: Dimension of output that corresponds to membrane potential

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: DynamicalSystemSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: DynamicalSystemSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: DynamicalSystemSpikeGenerator

   Runs the spike generation dynamics and returns a spike if membrane potential rises above spike threshold.

   **See also:** :java:ref:`ca.nengo.model.neuron.SpikeGenerator.run(float[],float[])`

setCurrentRange
^^^^^^^^^^^^^^^

.. java:method:: public void setCurrentRange(float[] range)
   :outertype: DynamicalSystemSpikeGenerator

   :param range: Range of driving currents at which to simulate to find steady-state firing rates for CONSTANT_RATE mode

setDynamics
^^^^^^^^^^^

.. java:method:: public void setDynamics(DynamicalSystem dynamics)
   :outertype: DynamicalSystemSpikeGenerator

   :param dynamics: A DynamicalSystem that defines the dynamics of spike generation.

setIntegrator
^^^^^^^^^^^^^

.. java:method:: public void setIntegrator(Integrator integrator)
   :outertype: DynamicalSystemSpikeGenerator

   :param integrator: An integrator with which to simulate the DynamicalSystem

setMinIntraSpikeTime
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setMinIntraSpikeTime(float min)
   :outertype: DynamicalSystemSpikeGenerator

   :param min: Minimum time between spike onsets.

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: DynamicalSystemSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setSpikeThreshold
^^^^^^^^^^^^^^^^^

.. java:method:: public void setSpikeThreshold(float threshold)
   :outertype: DynamicalSystemSpikeGenerator

   :param threshold: Threshold membrane potential at which a spike is considered to have occurred

setTransientTime
^^^^^^^^^^^^^^^^

.. java:method:: public void setTransientTime(float transientTime)
   :outertype: DynamicalSystemSpikeGenerator

   :param transientTime: Simulation time to ignore before counting spikes when finding steady-state rates

setVoltageDim
^^^^^^^^^^^^^

.. java:method:: public void setVoltageDim(int dim)
   :outertype: DynamicalSystemSpikeGenerator

   :param dim: Dimension of output that corresponds to membrane potential

