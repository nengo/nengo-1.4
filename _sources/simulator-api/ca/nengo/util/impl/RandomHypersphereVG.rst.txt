.. java:import:: ca.nengo.math PDFTools

.. java:import:: ca.nengo.math.impl GaussianPDF

.. java:import:: ca.nengo.util VectorGenerator

RandomHypersphereVG
===================

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class RandomHypersphereVG implements VectorGenerator, java.io.Serializable

   Generates random vectors distributed on or in a hypersphere. TODO: Reference Deak, Muller

   :author: Bryan Tripp

Constructors
------------
RandomHypersphereVG
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RandomHypersphereVG(boolean surface, float radius, float axisClusterFactor)
   :outertype: RandomHypersphereVG

   :param surface: If true, vectors are generated on surface of hypersphere; if false, throughout volume of hypersphere
   :param radius: Radius of hypersphere
   :param axisClusterFactor: Value between 0 and 1, with higher values indicating greater clustering of vectors around axes. 0 means even distribution; 1 means all vectors on axes.

RandomHypersphereVG
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RandomHypersphereVG()
   :outertype: RandomHypersphereVG

   Uses default settings (on surface; radius 1; no axis cluster)

Methods
-------
genVectors
^^^^^^^^^^

.. java:method:: public float[][] genVectors(int number, int dimension)
   :outertype: RandomHypersphereVG

   **See also:** :java:ref:`ca.nengo.util.VectorGenerator.genVectors(int,int)`

getAxisClusterFactor
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public float getAxisClusterFactor()
   :outertype: RandomHypersphereVG

   :return: Value between 0 and 1, with higher values indicating greater clustering of vectors around axes. 0 means even distribution; 1 means all vectors on axes.

getOnSurface
^^^^^^^^^^^^

.. java:method:: public boolean getOnSurface()
   :outertype: RandomHypersphereVG

   :return: True if generated vectors are on surface of hypersphere

getRadius
^^^^^^^^^

.. java:method:: public float getRadius()
   :outertype: RandomHypersphereVG

   :return: Radius of hypersphere

setAxisClusterFactor
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setAxisClusterFactor(float axisClusterFactor)
   :outertype: RandomHypersphereVG

   :param axisClusterFactor: Value between 0 and 1, with higher values indicating greater clustering of vectors around axes. 0 means even distribution; 1 means all vectors on axes.

setOnSurface
^^^^^^^^^^^^

.. java:method:: public void setOnSurface(boolean onSurface)
   :outertype: RandomHypersphereVG

   :param onSurface: True if generated vectors are on surface of hypersphere

setRadius
^^^^^^^^^

.. java:method:: public void setRadius(float radius)
   :outertype: RandomHypersphereVG

   :param radius: Radius of hypersphere

