.. java:import:: ca.nengo TestUtil

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl AbstractDynamicalSystem

.. java:import:: ca.nengo.dynamics.impl RK45Integrator

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util InterpolatorND

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl LinearInterpolatorND

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

.. java:import:: junit.framework TestCase

RK45IntegratorTest
==================

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public class RK45IntegratorTest extends TestCase

   Unit tests for RK45Integrator.

   :author: Bryan Tripp

Methods
-------
main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: RK45IntegratorTest

testIntegrate
^^^^^^^^^^^^^

.. java:method:: public void testIntegrate()
   :outertype: RK45IntegratorTest

