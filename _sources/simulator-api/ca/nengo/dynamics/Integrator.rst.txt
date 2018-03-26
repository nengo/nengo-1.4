.. java:import:: java.io Serializable

.. java:import:: ca.nengo.util TimeSeries

Integrator
==========

.. java:package:: ca.nengo.dynamics
   :noindex:

.. java:type:: public interface Integrator extends Serializable, Cloneable

   A numerical integrator of ordinary differential equations.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public Integrator clone() throws CloneNotSupportedException
   :outertype: Integrator

   :throws CloneNotSupportedException: is clone operation fails
   :return: cloned Integrator

integrate
^^^^^^^^^

.. java:method:: public TimeSeries integrate(DynamicalSystem system, TimeSeries input)
   :outertype: Integrator

   Integrates the given system over the time span defined by the input time series.

   :param system: The DynamicalSystem to solve.
   :param input: Input vector to the system, defined at the desired start and end times of integration, and optionally at times in between. The way in which the integrator interpolates between inputs at different times is decided by the Integrator implementation.
   :return: Time series of output vector

