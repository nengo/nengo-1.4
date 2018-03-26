.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

IndependentDimensionApproximator.EncoderFactory
===============================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class EncoderFactory implements VectorGenerator
   :outertype: IndependentDimensionApproximator

   A VectorGenerator for use with IndependentDimensionApproximator as an encoder factory. Encoders are derived from 1D encoders, and distributed to different dimensions in a round-robin manner. This convention is needed so that the ApproximatorFactory knows which response is associated with which dimension.

   :author: Bryan Tripp

Constructors
------------
EncoderFactory
^^^^^^^^^^^^^^

.. java:constructor:: public EncoderFactory(float radius)
   :outertype: IndependentDimensionApproximator.EncoderFactory

   :param radius: As RandomHypersphereGenerator arg

EncoderFactory
^^^^^^^^^^^^^^

.. java:constructor:: public EncoderFactory()
   :outertype: IndependentDimensionApproximator.EncoderFactory

   Defaults to radius 1.

Methods
-------
genVectors
^^^^^^^^^^

.. java:method:: public float[][] genVectors(int number, int dimension)
   :outertype: IndependentDimensionApproximator.EncoderFactory

   **See also:** :java:ref:`ca.nengo.util.VectorGenerator.genVectors(int,int)`

getRadius
^^^^^^^^^

.. java:method:: public float getRadius()
   :outertype: IndependentDimensionApproximator.EncoderFactory

   :return: radius

setRadius
^^^^^^^^^

.. java:method:: public void setRadius(float radius)
   :outertype: IndependentDimensionApproximator.EncoderFactory

   :param radius: Radius

