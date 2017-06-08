.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

SimpleLTISystem
===============

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public class SimpleLTISystem extends LTISystem

   A linear time-invariant system with the following properties:

   ..

   * A diagonal dynamics matrix
   * A zero passthrough matrix

   This implementation will run faster than an instance of the superclass that has these properties.

   :author: Bryan Tripp

Constructors
------------
SimpleLTISystem
^^^^^^^^^^^^^^^

.. java:constructor:: public SimpleLTISystem(float[] A, float[][] B, float[][] C, float[] x0, Units[] outputUnits)
   :outertype: SimpleLTISystem

   See also LTISystem.

   :param A: Diagonal entries of dynamics matrix
   :param B: Input matrix
   :param C: Output matrix
   :param x0: Initial state
   :param outputUnits: Units in which each dimension of the output are expressed

SimpleLTISystem
^^^^^^^^^^^^^^^

.. java:constructor:: public SimpleLTISystem(int stateDim, int inputDim, int outputDim)
   :outertype: SimpleLTISystem

   Creates an appropriately-dimensioned system with all-zero matrices, so that elements can be changed later.

   :param stateDim: Number of state variables
   :param inputDim: Number of inputs
   :param outputDim: Number of outputs

Methods
-------
f
^

.. java:method:: public float[] f(float t, float[] u)
   :outertype: SimpleLTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.f(float,float[])`

g
^

.. java:method:: public float[] g(float t, float[] u)
   :outertype: SimpleLTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.g(float,float[])`

setA
^^^^

.. java:method:: @Override public void setA(float[][] newA)
   :outertype: SimpleLTISystem

setB
^^^^

.. java:method:: @Override public void setB(float[][] newB)
   :outertype: SimpleLTISystem

setC
^^^^

.. java:method:: @Override public void setC(float[][] newC)
   :outertype: SimpleLTISystem

