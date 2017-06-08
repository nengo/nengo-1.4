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

RK45IntegratorTest.VanderPol
============================

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public static class VanderPol extends AbstractDynamicalSystem
   :outertype: RK45IntegratorTest

Constructors
------------
VanderPol
^^^^^^^^^

.. java:constructor:: public VanderPol(float[] state)
   :outertype: RK45IntegratorTest.VanderPol

VanderPol
^^^^^^^^^

.. java:constructor:: public VanderPol()
   :outertype: RK45IntegratorTest.VanderPol

Methods
-------
f
^

.. java:method:: public float[] f(float t, float[] u)
   :outertype: RK45IntegratorTest.VanderPol

g
^

.. java:method:: public float[] g(float t, float[] u)
   :outertype: RK45IntegratorTest.VanderPol

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: RK45IntegratorTest.VanderPol

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getOutputDimension()
   :outertype: RK45IntegratorTest.VanderPol

