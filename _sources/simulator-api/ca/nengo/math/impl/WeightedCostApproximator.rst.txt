.. java:import:: java.io FileNotFoundException

.. java:import:: java.util Random

.. java:import:: org.apache.log4j Logger

.. java:import:: Jama Matrix

.. java:import:: Jama SingularValueDecomposition

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util Memory

WeightedCostApproximator
========================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class WeightedCostApproximator implements LinearApproximator

   A LinearApproximator in which error is evaluated at a fixed set of points, and the cost function that is minimized is a weighted integral of squared error.

   Uses the Moore-Penrose pseudoinverse.

   TODO: test

   :author: Bryan Tripp

Constructors
------------
WeightedCostApproximator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public WeightedCostApproximator(float[][] evaluationPoints, float[][] values, Function costFunction, float noise, int nSV, boolean quiet)
   :outertype: WeightedCostApproximator

   :param evaluationPoints: Points at which error is evaluated (should be uniformly distributed, as the sum of error at these points is treated as an integral over the domain of interest). Examples include vector inputs to an ensemble, or different points in time within different simulation regimes.
   :param values: The values of whatever functions are being combined, at the evaluationPoints. Commonly neuron firing rates. The first dimension makes up the list of functions, and the second the values of these functions at each evaluation point.
   :param costFunction: A cost function that weights squared error over the domain of evaluation points
   :param noise: Standard deviation of Gaussian noise to add to values (to reduce sensitivity to simulation noise) as a proportion of the maximum absolute value over all values
   :param nSV: Number of singular values to keep from the singular value decomposition (SVD)
   :param quiet: Turn off logging?

WeightedCostApproximator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public WeightedCostApproximator(float[][][] evaluationSignals, float[][][] values, Function costFunction, float noise, int nSV, boolean quiet)
   :outertype: WeightedCostApproximator

   :param evaluationSignals: Signals over which error is evaluated. First dimension is for each evaluation signal. Second dimension is for the dimensions of each signal. Third dimension is the value of the signal dimension over time.
   :param values: The values of whatever functions are being combined, over the evaluation signals. Commonly neuron firing rates. The first dimension makes up the list of functions, the second the values of these functions for each evaluation signal, and the third the value of the function over time.
   :param costFunction: A cost function that weights squared error over the domain of evaluation points
   :param noise: Standard deviation of Gaussian noise to add to values (to reduce sensitivity to simulation noise) as a proportion of the maximum absolute value over all values
   :param nSV: Number of singular values to keep from the singular value decomposition (SVD)
   :param quiet: Turn off logging?

WeightedCostApproximator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public WeightedCostApproximator(float[][] evaluationPoints, float[][] values, Function costFunction, float noise, int nSV)
   :outertype: WeightedCostApproximator

   :param evaluationPoints: Points at which error is evaluated (should be uniformly distributed, as the sum of error at these points is treated as an integral over the domain of interest). Examples include vector inputs to an ensemble, or different points in time within different simulation regimes.
   :param values: The values of whatever functions are being combined, at the evaluationPoints. Commonly neuron firing rates. The first dimension makes up the list of functions, and the second the values of these functions at each evaluation point.
   :param costFunction: A cost function that weights squared error over the domain of evaluation points
   :param noise: Standard deviation of Gaussian noise to add to values (to reduce sensitivity to simulation noise) as a proportion of the maximum absolute value over all values
   :param nSV: Number of singular values to keep from the singular value decomposition (SVD)

Methods
-------
canUseGPU
^^^^^^^^^

.. java:method:: public static boolean canUseGPU()
   :outertype: WeightedCostApproximator

clone
^^^^^

.. java:method:: @Override public LinearApproximator clone() throws CloneNotSupportedException
   :outertype: WeightedCostApproximator

findCoefficients
^^^^^^^^^^^^^^^^

.. java:method:: public float[] findCoefficients(Function target)
   :outertype: WeightedCostApproximator

   This implementation is adapted from Eliasmith & Anderson, 2003, appendix A.

   It solves PHI = GAMMA" UPSILON, where " denotes pseudoinverse, UPSILON_i = < cost(x) x a_i(x) >, and GAMMA_ij = < cost(x) a_i(x) a_j(x) >. <> denotes integration (the sum over eval points).

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.findCoefficients(ca.nengo.math.Function)`

findCoefficients
^^^^^^^^^^^^^^^^

.. java:method:: public float[] findCoefficients(float[] targetSignal)
   :outertype: WeightedCostApproximator

   Similar to findCoefficients(ca.nengo.math.Function), but finds coefficients for a target signal (over time) rather than a target function.

   :param targetSignal: signal over time that the coefficients should fit to
   :return: coefficients (weights on the output of each neuron)

getEvalPoints
^^^^^^^^^^^^^

.. java:method:: public float[][] getEvalPoints()
   :outertype: WeightedCostApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getEvalPoints()`

getGPUErrorMessage
^^^^^^^^^^^^^^^^^^

.. java:method:: public static String getGPUErrorMessage()
   :outertype: WeightedCostApproximator

getUseGPU
^^^^^^^^^

.. java:method:: public static boolean getUseGPU()
   :outertype: WeightedCostApproximator

   :return: Using the GPU?

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: WeightedCostApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getValues()`

pseudoInverse
^^^^^^^^^^^^^

.. java:method:: public double[][] pseudoInverse(double[][] matrix, float minSV, int nSV)
   :outertype: WeightedCostApproximator

   Override this method to use a different pseudoinverse implementation (eg clustered).

   :param matrix: Any matrix
   :param minSV: Hint as to smallest singular value to use
   :param nSV: Max number of singular values to use
   :return: The pseudoinverse of the given matrix

setUseGPU
^^^^^^^^^

.. java:method:: public static void setUseGPU(boolean use)
   :outertype: WeightedCostApproximator

   :param use: Use the GPU?
