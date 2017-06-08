.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math RootFinder

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.math.impl NewtonRootFinder

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

HillMuscle.Dynamics
===================

.. java:package:: ca.nengo.model.muscle.impl
   :noindex:

.. java:type:: public static class Dynamics implements DynamicalSystem
   :outertype: HillMuscle

   Dynamical system for the Hill muscle model

Constructors
------------
Dynamics
^^^^^^^^

.. java:constructor:: public Dynamics(float tauEA, float maxIsometricForce, Function CEForceLength, Function CEForceVelocity, Function SEForceLength, boolean torque)
   :outertype: HillMuscle.Dynamics

   :param tauEA: see Hill model
   :param maxIsometricForce: Isometric force produced by CE at maximal activation and optimal length
   :param CEForceLength: see Hill model
   :param CEForceVelocity: see Hill model
   :param SEForceLength: see Hill model
   :param torque: true indicates a torque muscle (input in rads, output in Nm); false indicates a linear muscle (input in m, output in N)

Methods
-------
clone
^^^^^

.. java:method:: public DynamicalSystem clone() throws CloneNotSupportedException
   :outertype: HillMuscle.Dynamics

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.clone()`

f
^

.. java:method:: public float[] f(float t, float[] u)
   :outertype: HillMuscle.Dynamics

   :param t: Simulation time (s)
   :param u: Input: [excitation (0-1), muscle-tendon length, muscle-tendon lengthening velocity]

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.f(float,float[])`

g
^

.. java:method:: public float[] g(float t, float[] u)
   :outertype: HillMuscle.Dynamics

   :param t: Simulation time (s)
   :param u: Input: [excitation (0-1), muscle-tendon length, muscle-tendon lengthening velocity]

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.g(float,float[])`

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: HillMuscle.Dynamics

   :return: Configuration

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: HillMuscle.Dynamics

   :return: 3 (activation, muscle-tendon length, muscle-tendon velocity)

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getInputDimension()`

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getOutputDimension()
   :outertype: HillMuscle.Dynamics

   :return: 1 (force)

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getOutputDimension()`

getOutputUnits
^^^^^^^^^^^^^^

.. java:method:: public Units getOutputUnits(int outputDimension)
   :outertype: HillMuscle.Dynamics

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getOutputUnits(int)`

getState
^^^^^^^^

.. java:method:: public float[] getState()
   :outertype: HillMuscle.Dynamics

   :return: [activation, CE length]

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getState()`

setState
^^^^^^^^

.. java:method:: public void setState(float[] state)
   :outertype: HillMuscle.Dynamics

   :param state: [activation, CE length]

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.setState(float[])`

