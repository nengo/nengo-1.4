LinearSystem
============

.. java:package:: ca.nengo.dynamics
   :noindex:

.. java:type:: public interface LinearSystem extends DynamicalSystem

   A linear dynamical system, which may or may not be time-varying. We use the state-space model of linear systems, which consist of four (possibly time-varying) matrices.

   TODO: ref chen

   The distinction between linear and non-linear dynamical systems is important, because many assumptions that hold for linear systems do not hold in general. For this reason, only linear systems can be used in some situations, and we need this interface to enforce their use.

   :author: Bryan Tripp

Methods
-------
getA
^^^^

.. java:method:: public float[][] getA(float t)
   :outertype: LinearSystem

   :param t: Simulation time
   :return: The dynamics matrix at the given time

getB
^^^^

.. java:method:: public float[][] getB(float t)
   :outertype: LinearSystem

   :param t: Simulation time
   :return: The input matrix at the given time

getC
^^^^

.. java:method:: public float[][] getC(float t)
   :outertype: LinearSystem

   :param t: Simulation time
   :return: The output matrix at the given time

getD
^^^^

.. java:method:: public float[][] getD(float t)
   :outertype: LinearSystem

   :param t: Simulation time
   :return: The passthrough matrix at the given time

