.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util VectorGenerator

FixedVectorGenerator
====================

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class FixedVectorGenerator implements VectorGenerator, java.io.Serializable

   Generates vectors from a given set. TODO: Reference Deak, Muller

Constructors
------------
FixedVectorGenerator
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public FixedVectorGenerator(float[][] vectors)
   :outertype: FixedVectorGenerator

Methods
-------
genVectors
^^^^^^^^^^

.. java:method:: public float[][] genVectors(int number, int dimension)
   :outertype: FixedVectorGenerator

