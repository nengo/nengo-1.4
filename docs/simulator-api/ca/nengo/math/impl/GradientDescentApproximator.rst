.. java:import:: java.io Serializable

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

GradientDescentApproximator
===========================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class GradientDescentApproximator implements LinearApproximator

   A LinearApproximator that searches for coefficients by descending an error gradient. This method is slower and less powerful than WeightedCostApproximator, but constraints on coefficients are allowed.

   :author: Bryan Tripp

Constructors
------------
GradientDescentApproximator
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GradientDescentApproximator(float[][] evaluationPoints, float[][] values, Constraints constraints, boolean ignoreBias)
   :outertype: GradientDescentApproximator

   :param evaluationPoints: Points at which error is evaluated (should be uniformly distributed, as the sum of error at these points is treated as an integral over the domain of interest). Examples include vector inputs to an ensemble, or different points in time within different simulation regimes.
   :param values: The values of whatever functions are being combined, at the evaluationPoints. Commonly neuron firing rates. The first dimension makes up the list of functions, and the second the values of these functions at each evaluation point.
   :param constraints: Constraints on coefficients
   :param ignoreBias: If true, bias in constituent and target functions is ignored (resulting estimate will be biased)

Methods
-------
clone
^^^^^

.. java:method:: @Override public LinearApproximator clone() throws CloneNotSupportedException
   :outertype: GradientDescentApproximator

findCoefficients
^^^^^^^^^^^^^^^^

.. java:method:: public float[] findCoefficients(Function target)
   :outertype: GradientDescentApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.findCoefficients(ca.nengo.math.Function)`

getEvalPoints
^^^^^^^^^^^^^

.. java:method:: public float[][] getEvalPoints()
   :outertype: GradientDescentApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getEvalPoints()`

getMaxIterations
^^^^^^^^^^^^^^^^

.. java:method:: public int getMaxIterations()
   :outertype: GradientDescentApproximator

   :return: Maximum iterations per findCoefficients(...)

getTolerance
^^^^^^^^^^^^

.. java:method:: public float getTolerance()
   :outertype: GradientDescentApproximator

   :return: Target mean-squared error

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: GradientDescentApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getValues()`

setMaxIterations
^^^^^^^^^^^^^^^^

.. java:method:: public void setMaxIterations(int max)
   :outertype: GradientDescentApproximator

   :param max: New maximum number of iterations per findCoefficients(...)

setStartingCoefficients
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setStartingCoefficients(float[] coefficients)
   :outertype: GradientDescentApproximator

   :param coefficients: Coefficients at which to start the optimization

setTolerance
^^^^^^^^^^^^

.. java:method:: public void setTolerance(float tolerance)
   :outertype: GradientDescentApproximator

   :param tolerance: Target mean-squared error

