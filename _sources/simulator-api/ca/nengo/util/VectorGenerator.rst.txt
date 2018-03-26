VectorGenerator
===============

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface VectorGenerator

   A tool for generating sets of uniformly or randomly distributed vectors.

   :author: Bryan Tripp

Methods
-------
genVectors
^^^^^^^^^^

.. java:method:: public float[][] genVectors(int number, int dimension)
   :outertype: VectorGenerator

   The vector distribution is decided by implementing classes.

   :param number: Number of vectors to be returned
   :param dimension: Dimension of the vectors to be returned
   :return: A List of float[] vectors

