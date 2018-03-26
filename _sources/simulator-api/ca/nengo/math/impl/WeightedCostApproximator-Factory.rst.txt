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

WeightedCostApproximator.Factory
================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class Factory implements ApproximatorFactory
   :outertype: WeightedCostApproximator

   An ApproximatorFactory that produces WeightedCostApproximators.

   :author: Bryan Tripp

Constructors
------------
Factory
^^^^^^^

.. java:constructor:: public Factory(float noise)
   :outertype: WeightedCostApproximator.Factory

   :param noise: Random noise to add to component functions (proportion of largest value over all functions)

Factory
^^^^^^^

.. java:constructor:: public Factory(float noise, boolean quiet)
   :outertype: WeightedCostApproximator.Factory

   :param noise: Random noise to add to component functions (proportion of largest value over all functions)
   :param quiet: Turn off logging?

Factory
^^^^^^^

.. java:constructor:: public Factory(float noise, int NSV)
   :outertype: WeightedCostApproximator.Factory

   :param noise: Random noise to add to component functions (proportion of largest value over all functions)
   :param NSV: Number of singular values to keep

Factory
^^^^^^^

.. java:constructor:: public Factory(float noise, int NSV, boolean quiet)
   :outertype: WeightedCostApproximator.Factory

   :param noise: Random noise to add to component functions (proportion of largest value over all functions)
   :param NSV: Number of singular values to keep
   :param quiet: Turn off logging?

Methods
-------
clone
^^^^^

.. java:method:: @Override public ApproximatorFactory clone() throws CloneNotSupportedException
   :outertype: WeightedCostApproximator.Factory

getApproximator
^^^^^^^^^^^^^^^

.. java:method:: public LinearApproximator getApproximator(float[][] evalPoints, float[][] values)
   :outertype: WeightedCostApproximator.Factory

   **See also:** :java:ref:`ca.nengo.math.ApproximatorFactory.getApproximator(float[][],float[][])`

getApproximator
^^^^^^^^^^^^^^^

.. java:method:: public LinearApproximator getApproximator(float[][][] evaluationSignals, float[][][] values)
   :outertype: WeightedCostApproximator.Factory

   Similar to getApproximator(float[][], float[][]) but uses evaluation signals and outputs computed over time.

   :param evaluationSignals: Signals over which component functions are evaluated. First dimension is the signal, second is the dimension, and third is time.
   :param values: values of component functions over the evaluation signals. First dimension is the component, second is the signal, and third is time.
   :return: A LinearApproximator that can be used to approximate new Functions as a weighted sum of the given components.

getCostFunction
^^^^^^^^^^^^^^^

.. java:method:: public Function getCostFunction(int dimension)
   :outertype: WeightedCostApproximator.Factory

   Note: override to use non-uniform error weighting.

   :param dimension: Dimension of the function to be approximated
   :return: A function over the input space that defines relative importance of error at each point (defaults to a ConstantFunction)

getNSV
^^^^^^

.. java:method:: public int getNSV()
   :outertype: WeightedCostApproximator.Factory

   :return: Maximum number of singular values to use in pseudoinverse of correlation matrix (zero or less means use as many as possible to a threshold magnitude determined by noise).

getNoise
^^^^^^^^

.. java:method:: public float getNoise()
   :outertype: WeightedCostApproximator.Factory

   :return: Random noise to add to component functions (proportion of largest value over all functions)

getQuiet
^^^^^^^^

.. java:method:: public boolean getQuiet()
   :outertype: WeightedCostApproximator.Factory

   :return: Whether or not information will be printed out to console during make process.

setNSV
^^^^^^

.. java:method:: public void setNSV(int nSV)
   :outertype: WeightedCostApproximator.Factory

   :param nSV: Maximum number of singular values to use in pseudoinverse of correlation matrix (zero or less means use as many as possible to a threshold magnitude determined by noise).

setNoise
^^^^^^^^

.. java:method:: public void setNoise(float noise)
   :outertype: WeightedCostApproximator.Factory

   :param noise: Random noise to add to component functions (proportion of largest value over all functions)

setQuiet
^^^^^^^^

.. java:method:: public void setQuiet(boolean quiet)
   :outertype: WeightedCostApproximator.Factory

   :param quiet: Controls whether or not information will be printed out to console during make process.
