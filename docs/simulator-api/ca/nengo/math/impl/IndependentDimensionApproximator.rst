.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

IndependentDimensionApproximator
================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class IndependentDimensionApproximator implements LinearApproximator

   A LinearApproximator for functions with no multidimensional nonlinearities. Each of the source functions is assumed to be a function of one dimension. Consequently, only functions of one dimension can be decoded directly. Linear functions of multiple dimensions can obtained later by combining weights of one-dimensional functions.

   :author: Bryan Tripp

Constructors
------------
IndependentDimensionApproximator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public IndependentDimensionApproximator(float[] evaluationPoints, float[][] values, int[] dimensions, int dimension, Function costFunction, float noise)
   :outertype: IndependentDimensionApproximator

   :param evaluationPoints: Points of evaluation of source functions, in the dimension along which they vary
   :param values: Values of each source function at each point
   :param dimensions: The dimension along which each function varies
   :param dimension: Dimension of the space from which source functions map
   :param costFunction: As in WeightedCostApproximator, but in dimension along which functions vary
   :param noise: Proportion of noise to add

Methods
-------
clone
^^^^^

.. java:method:: @Override public LinearApproximator clone() throws CloneNotSupportedException
   :outertype: IndependentDimensionApproximator

findCoefficients
^^^^^^^^^^^^^^^^

.. java:method:: public float[] findCoefficients(Function target)
   :outertype: IndependentDimensionApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.findCoefficients(ca.nengo.math.Function)`

getEvalPoints
^^^^^^^^^^^^^

.. java:method:: public float[][] getEvalPoints()
   :outertype: IndependentDimensionApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getEvalPoints()`

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: IndependentDimensionApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getValues()`

