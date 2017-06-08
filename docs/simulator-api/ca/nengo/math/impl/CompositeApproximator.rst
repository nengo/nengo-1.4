.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

CompositeApproximator
=====================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class CompositeApproximator implements LinearApproximator

   A LinearApproximator that approximates multi-dimensional functions as sums of lower-dimensional functions. Each lower-dimensional function is approximated by a component approximator, which is provided in the constructor. The resulting approximation is the sum of approximations produced by each component.

   CompositeApproximator is similar to the simpler IndependentDimensionApproximator, but more general because dimensions can be handled either independently or in arbitrary groups.

   CompositeApproximator is useful for low-dimensionally non-linear functions of high-dimensional vectors, eg x1*x2 + x3*x4 - x5*x6.

   It is also useful for creating accurate, high-dimensional ensembles of neurons with a little overlap between dimensions.

   TODO: should LinearApproximator have getDimension()? would be possible to get rid of 2nd constructor arg then TODO: test

   :author: Bryan Tripp

Constructors
------------
CompositeApproximator
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CompositeApproximator(LinearApproximator[] components, int[][] dimensions)
   :outertype: CompositeApproximator

   :param components: LinearApproximators that make up the composite
   :param dimensions: dimensionality of each LinearApproximator

Methods
-------
clone
^^^^^

.. java:method:: @Override public LinearApproximator clone() throws CloneNotSupportedException
   :outertype: CompositeApproximator

findCoefficients
^^^^^^^^^^^^^^^^

.. java:method:: public float[] findCoefficients(Function target)
   :outertype: CompositeApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.findCoefficients(ca.nengo.math.Function)`

getEvalPoints
^^^^^^^^^^^^^

.. java:method:: public float[][] getEvalPoints()
   :outertype: CompositeApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getEvalPoints()`

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: CompositeApproximator

   **See also:** :java:ref:`ca.nengo.math.LinearApproximator.getValues()`

