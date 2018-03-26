.. java:import:: java.util Properties

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl AbstractDynamicalSystem

.. java:import:: ca.nengo.dynamics.impl RK45Integrator

.. java:import:: ca.nengo.math CurveFitter

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl LinearCurveFitter

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

GruberSpikeGenerator
====================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class GruberSpikeGenerator implements SpikeGenerator, Probeable

   Model of spike generation in medium-spiny striatal neurons from: Gruber, Solla, Surmeier & Houk (2003) Modulation of striatal single units by expected reward: a spiny neuron model displaying dopamine-induced bistability, J Neurophysiol 90: 1095-1114.

   :author: Bryan Tripp

Fields
------
MEMBRANE_POTENTIAL
^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String MEMBRANE_POTENTIAL
   :outertype: GruberSpikeGenerator

   String that is used for membrane potential

Constructors
------------
GruberSpikeGenerator
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GruberSpikeGenerator()
   :outertype: GruberSpikeGenerator

   Create a spike generator that follows Gruber et al.'s medium-spiny striatal neuron model.

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGenerator clone() throws CloneNotSupportedException
   :outertype: GruberSpikeGenerator

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: GruberSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: GruberSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: GruberSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: GruberSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public InstantaneousOutput run(float[] time, float[] current)
   :outertype: GruberSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.neuron.SpikeGenerator.run(float[],float[])`

setDopamine
^^^^^^^^^^^

.. java:method:: public void setDopamine(float dopamine)
   :outertype: GruberSpikeGenerator

   :param dopamine: Dopamine concentration (between 1 and 1.4)

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: GruberSpikeGenerator

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

