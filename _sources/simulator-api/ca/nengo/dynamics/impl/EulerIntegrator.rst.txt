.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl LinearInterpolatorND

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

EulerIntegrator
===============

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public class EulerIntegrator implements Integrator

   Euler's method of numerical integration: x(t+h) ~ x(t) + h*x'(t) TODO: test TODO: should there be some means for aborting early (aside from exceptions, e.g. if output converges to constant)?

   :author: Bryan Tripp

Constructors
------------
EulerIntegrator
^^^^^^^^^^^^^^^

.. java:constructor:: public EulerIntegrator(float stepSize)
   :outertype: EulerIntegrator

   :param stepSize: Timestep size (dt)

EulerIntegrator
^^^^^^^^^^^^^^^

.. java:constructor:: public EulerIntegrator()
   :outertype: EulerIntegrator

   Uses default step size of .0001

Methods
-------
clone
^^^^^

.. java:method:: @Override public Integrator clone() throws CloneNotSupportedException
   :outertype: EulerIntegrator

getStepSize
^^^^^^^^^^^

.. java:method:: public float getStepSize()
   :outertype: EulerIntegrator

   :return: get Timestep size

integrate
^^^^^^^^^

.. java:method:: public TimeSeries integrate(DynamicalSystem system, TimeSeries input)
   :outertype: EulerIntegrator

   Linear interpolation is performed between given input points.

   **See also:** :java:ref:`ca.nengo.dynamics.Integrator.integrate(ca.nengo.dynamics.DynamicalSystem,ca.nengo.util.TimeSeries)`

setStepSize
^^^^^^^^^^^

.. java:method:: public void setStepSize(float stepSize)
   :outertype: EulerIntegrator

   :param stepSize: Timestep size

