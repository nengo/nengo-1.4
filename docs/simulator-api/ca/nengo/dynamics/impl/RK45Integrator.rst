.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl LinearInterpolatorND

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

RK45Integrator
==============

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public class RK45Integrator implements Integrator

   A variable-timestep Integrator, which uses the Dormand-Prince 4th and 5th-order Runge-Kutta formulae.

   This code is adapted from a GPL Octave implementation by Marc Compere (see http://users.powernet.co.uk/kienzle/octave/matcompat/scripts/ode_v1.11/ode45.m)

   See also Dormand & Prince, 1980, J Computational and Applied Mathematics 6(1), 19-26.

   TODO: should re-use initial time step estimate from last integration if available

   :author: Bryan Tripp

Constructors
------------
RK45Integrator
^^^^^^^^^^^^^^

.. java:constructor:: public RK45Integrator(float tolerance)
   :outertype: RK45Integrator

   :param tolerance: Error tolerance

RK45Integrator
^^^^^^^^^^^^^^

.. java:constructor:: public RK45Integrator()
   :outertype: RK45Integrator

   Uses default error tolerance of 1e-6

Methods
-------
clone
^^^^^

.. java:method:: @Override public Integrator clone() throws CloneNotSupportedException
   :outertype: RK45Integrator

getTolerance
^^^^^^^^^^^^

.. java:method:: public float getTolerance()
   :outertype: RK45Integrator

   :return: Error tolerance

integrate
^^^^^^^^^

.. java:method:: public TimeSeries integrate(DynamicalSystem system, TimeSeries input)
   :outertype: RK45Integrator

   **See also:** :java:ref:`ca.nengo.dynamics.Integrator.integrate(ca.nengo.dynamics.DynamicalSystem,ca.nengo.util.TimeSeries)`

setTolerance
^^^^^^^^^^^^

.. java:method:: public void setTolerance(float tolerance)
   :outertype: RK45Integrator

   :param tolerance: Error tolerance

