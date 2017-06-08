.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics LinearSystem

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

LTISystem
=========

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public class LTISystem implements LinearSystem

   A linear time-invariant dynamical system model in state-space form. Such a system can be defined in terms of the four matrices that must be provided in the constructor. TODO: test

   :author: Bryan Tripp

Constructors
------------
LTISystem
^^^^^^^^^

.. java:constructor:: public LTISystem(float[][] A, float[][] B, float[][] C, float[][] D, float[] x0, Units[] outputUnits)
   :outertype: LTISystem

   Each argument is an array of arrays that represents a matrix. The first dimension represents the matrix row and the second the matrix column, so that A_ij corresponds to A[i-1][j-1] (since arrays are indexed from 0). The matrices must have valid dimensions for a state-space model: A must be n x n; B must be n x p; C must be q x n; and D must be q x p.

   :param A: Dynamics matrix
   :param B: Input matrix
   :param C: Output matrix
   :param D: Passthrough matrix
   :param x0: Initial state
   :param outputUnits: Units in which each dimension of the output are expressed

Methods
-------
clone
^^^^^

.. java:method:: public DynamicalSystem clone() throws CloneNotSupportedException
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.clone()`

f
^

.. java:method:: public float[] f(float t, float[] u)
   :outertype: LTISystem

   :return: Ax + Bu

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.f(float,float[])`

g
^

.. java:method:: public float[] g(float t, float[] u)
   :outertype: LTISystem

   :return: Cx + Du

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.g(float,float[])`

getA
^^^^

.. java:method:: public float[][] getA(float t)
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.LinearSystem.getA(float)`

getA
^^^^

.. java:method:: public float[][] getA()
   :outertype: LTISystem

   :return: The dynamics matrix at the current time

getB
^^^^

.. java:method:: public float[][] getB(float t)
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.LinearSystem.getB(float)`

getB
^^^^

.. java:method:: public float[][] getB()
   :outertype: LTISystem

   :return: The input matrix at the current time

getC
^^^^

.. java:method:: public float[][] getC(float t)
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.LinearSystem.getC(float)`

getC
^^^^

.. java:method:: public float[][] getC()
   :outertype: LTISystem

   :return: The output matrix at the current time

getD
^^^^

.. java:method:: public float[][] getD(float t)
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.LinearSystem.getD(float)`

getD
^^^^

.. java:method:: public float[][] getD()
   :outertype: LTISystem

   :return: The passthrough matrix at the current time

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getInputDimension()`

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getOutputDimension()
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getOutputDimension()`

getOutputUnits
^^^^^^^^^^^^^^

.. java:method:: public Units getOutputUnits(int outputDimension)
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getOutputUnits(int)`

getState
^^^^^^^^

.. java:method:: public float[] getState()
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getState()`

getStateDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getStateDimension()
   :outertype: LTISystem

   :return: State (x) dimensionality.

setA
^^^^

.. java:method:: public void setA(float[][] newA)
   :outertype: LTISystem

   :param newA: New dynamics matrix

setB
^^^^

.. java:method:: public void setB(float[][] newB)
   :outertype: LTISystem

   :param newB: New input matrix

setC
^^^^

.. java:method:: public void setC(float[][] newC)
   :outertype: LTISystem

   :param newC: New output matrix

setD
^^^^

.. java:method:: public void setD(float[][] newD)
   :outertype: LTISystem

   :param newD: New passthrough matrix

setInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public void setInputDimension(int dim)
   :outertype: LTISystem

   :param dim: Input dimensionality. Affects B and D.

setOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setOutputDimension(int dim)
   :outertype: LTISystem

   :param dim: Output dimensionality. Affects C and D.

setOutputUnits
^^^^^^^^^^^^^^

.. java:method:: public void setOutputUnits(int outputDimension, Units units)
   :outertype: LTISystem

   :param outputDimension: dimensionality of output
   :param units: Units to work in

setState
^^^^^^^^

.. java:method:: public void setState(float[] state)
   :outertype: LTISystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.setState(float[])`

setStateDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public void setStateDimension(int dim)
   :outertype: LTISystem

   :param dim: State (x) dimensionality

