.. java:import:: Jama Matrix

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

ImpulseIntegral
===============

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public class ImpulseIntegral

   A tool for finding the integral of the impulse response of an LTI system. The impulse response of an LTI system is the matrix D*d(t) + C*exp(A*t)*B, where A,B,C,D are defined as usual and d(t) is an impulse. We are interested here in the integral of this matrix (which we may want so that we can normalize it somehow).

   There are many ways to calculate e^At (see Moler & Van Loan, 2003). Here we use simulation, which is simple to implement, and numerically attractive when the result is needed at many t.

   :author: Bryan Tripp

Methods
-------
integrate
^^^^^^^^^

.. java:method:: public static float[][] integrate(LTISystem system)
   :outertype: ImpulseIntegral

   :param system: The system for which integrals of impulse responses are needed
   :return: Integrals of impulse responses. This is a matrix with the same dimensions as the passthrough matrix of the system. Each column is the integral of the response to an impulse at the corresponding input.
