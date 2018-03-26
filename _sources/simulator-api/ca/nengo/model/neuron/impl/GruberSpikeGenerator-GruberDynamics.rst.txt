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

GruberSpikeGenerator.GruberDynamics
===================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class GruberDynamics extends AbstractDynamicalSystem
   :outertype: GruberSpikeGenerator

   Implements dynamics of Gruber et al. bistable model of medium spiny neuron. State corresponds to membrane potential, and output is firing rate, as a static function of membrane potential.

   :author: Bryan Tripp

Constructors
------------
GruberDynamics
^^^^^^^^^^^^^^

.. java:constructor:: public GruberDynamics(float resetPotential)
   :outertype: GruberSpikeGenerator.GruberDynamics

   :param resetPotential: Potential at which membrane starts (is and reset to)

Methods
-------
f
^

.. java:method:: public float[] f(float t, float[] u)
   :outertype: GruberSpikeGenerator.GruberDynamics

   :param u: [driving current (~ 0 to 2); dopamine (~ 1 to 1.4)]

   **See also:** :java:ref:`ca.nengo.dynamics.impl.AbstractDynamicalSystem.f(float,float[])`

g
^

.. java:method:: public float[] g(float t, float[] u)
   :outertype: GruberSpikeGenerator.GruberDynamics

   **See also:** :java:ref:`ca.nengo.dynamics.impl.AbstractDynamicalSystem.g(float,float[])`

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: GruberSpikeGenerator.GruberDynamics

   **See also:** :java:ref:`ca.nengo.dynamics.impl.AbstractDynamicalSystem.getInputDimension()`

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getOutputDimension()
   :outertype: GruberSpikeGenerator.GruberDynamics

   **See also:** :java:ref:`ca.nengo.dynamics.impl.AbstractDynamicalSystem.getOutputDimension()`

getOutputUnits
^^^^^^^^^^^^^^

.. java:method:: @Override public Units getOutputUnits(int outputDimension)
   :outertype: GruberSpikeGenerator.GruberDynamics

