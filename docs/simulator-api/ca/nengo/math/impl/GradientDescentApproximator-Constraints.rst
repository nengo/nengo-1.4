.. java:import:: java.io Serializable

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

GradientDescentApproximator.Constraints
=======================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static interface Constraints extends Serializable, Cloneable
   :outertype: GradientDescentApproximator

   Enforces constraints on coefficients. TODO: should this be generalized to LinearApproximator?

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public Constraints clone() throws CloneNotSupportedException
   :outertype: GradientDescentApproximator.Constraints

   :throws CloneNotSupportedException: if clone can't be made
   :return: Valid clone

correct
^^^^^^^

.. java:method::  boolean correct(float[] coefficients)
   :outertype: GradientDescentApproximator.Constraints

   :param coefficients: A set of coefficients which may violate constraints (they are altered as little as possible by this method so that they satisfy constraints after the call)
   :return: True if all coefficients had to be corrected (no further improvement is possible in the attempted direction)

