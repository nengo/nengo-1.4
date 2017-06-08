.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

IndependentDimensionApproximator.Factory
========================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class Factory implements ApproximatorFactory
   :outertype: IndependentDimensionApproximator

   Factory for IndependentDimensionApproximators.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: @Override public ApproximatorFactory clone() throws CloneNotSupportedException
   :outertype: IndependentDimensionApproximator.Factory

getApproximator
^^^^^^^^^^^^^^^

.. java:method:: public LinearApproximator getApproximator(float[][] evalPoints, float[][] values)
   :outertype: IndependentDimensionApproximator.Factory

   **See also:** :java:ref:`ca.nengo.math.ApproximatorFactory.getApproximator(float[][],float[][])`

