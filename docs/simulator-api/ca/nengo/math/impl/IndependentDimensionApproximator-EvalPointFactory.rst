.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

IndependentDimensionApproximator.EvalPointFactory
=================================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class EvalPointFactory implements VectorGenerator
   :outertype: IndependentDimensionApproximator

   A VectorGenerator for use with IndependentDimensionApproximator as an evaluation point factory. It returns a constant number of vectors regardless of the number requested. In each vector, all the elements are the same. The element is drawn from an underlying one-dimensional VectorGenerator. This allows creation of high dimensional ensembles where all encoders are on an axis, without evaluation responses at a number of evaluation points that grows with the number of dimensions (as would normally be required).

   :author: Bryan Tripp

Constructors
------------
EvalPointFactory
^^^^^^^^^^^^^^^^

.. java:constructor:: public EvalPointFactory(float radius, int points)
   :outertype: IndependentDimensionApproximator.EvalPointFactory

   :param radius: As RandomHypersphereGenerator arg
   :param points: Number of vectors produced, regardless of number requested

Methods
-------
genVectors
^^^^^^^^^^

.. java:method:: public float[][] genVectors(int number, int dimension)
   :outertype: IndependentDimensionApproximator.EvalPointFactory

   **See also:** :java:ref:`ca.nengo.util.VectorGenerator.genVectors(int,int)`

getPoints
^^^^^^^^^

.. java:method:: public int getPoints()
   :outertype: IndependentDimensionApproximator.EvalPointFactory

   :return: Number of evaluation points

getRadius
^^^^^^^^^

.. java:method:: public float getRadius()
   :outertype: IndependentDimensionApproximator.EvalPointFactory

   :return: radius

setPoints
^^^^^^^^^

.. java:method:: public void setPoints(int points)
   :outertype: IndependentDimensionApproximator.EvalPointFactory

   :param points: Number of evaluation points

setRadius
^^^^^^^^^

.. java:method:: public void setRadius(float radius)
   :outertype: IndependentDimensionApproximator.EvalPointFactory

   :param radius: Radius

