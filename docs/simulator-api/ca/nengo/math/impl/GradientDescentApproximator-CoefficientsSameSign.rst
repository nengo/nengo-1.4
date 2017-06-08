.. java:import:: java.io Serializable

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

GradientDescentApproximator.CoefficientsSameSign
================================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class CoefficientsSameSign implements Constraints
   :outertype: GradientDescentApproximator

   Forces all decoding coefficients to be >= 0.

   :author: Bryan Tripp

Constructors
------------
CoefficientsSameSign
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CoefficientsSameSign(boolean positive)
   :outertype: GradientDescentApproximator.CoefficientsSameSign

   :param positive: Sign to force all coefficients to

Methods
-------
clone
^^^^^

.. java:method:: @Override public Constraints clone() throws CloneNotSupportedException
   :outertype: GradientDescentApproximator.CoefficientsSameSign

correct
^^^^^^^

.. java:method:: public boolean correct(float[] coefficients)
   :outertype: GradientDescentApproximator.CoefficientsSameSign

   **See also:** :java:ref:`Constraints.correct(float[])`

