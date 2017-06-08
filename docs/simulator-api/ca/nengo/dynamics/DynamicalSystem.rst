.. java:import:: java.io Serializable

.. java:import:: ca.nengo.model Units

DynamicalSystem
===============

.. java:package:: ca.nengo.dynamics
   :noindex:

.. java:type:: public interface DynamicalSystem extends Serializable, Cloneable

   A state-space model of a continuous-time dynamical system. The system can be linear or non-linear, and autonomous or time-varying.

   While a DynamicalSystem can be time-varying, it must be immutable. That is, its properties can change over simulation time, but not over run time.

   TODO: units here or in subinterface? TODO: reference Chen

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public DynamicalSystem clone() throws CloneNotSupportedException
   :outertype: DynamicalSystem

   :throws CloneNotSupportedException: if something causes clone not to work
   :return: An identical copy of this system which references an independent copy of the state variables

f
^

.. java:method:: public float[] f(float t, float[] u)
   :outertype: DynamicalSystem

   The dynamic equation.

   :param t: Time
   :param u: Input vector
   :return: 1st derivative of state vector

g
^

.. java:method:: public float[] g(float t, float[] u)
   :outertype: DynamicalSystem

   The output equation.

   :param t: Time
   :param u: Input vector
   :return: Output vector

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public int getInputDimension()
   :outertype: DynamicalSystem

   :return: Dimension of input vector

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getOutputDimension()
   :outertype: DynamicalSystem

   :return: Dimension of output vector

getOutputUnits
^^^^^^^^^^^^^^

.. java:method:: public Units getOutputUnits(int outputDimension)
   :outertype: DynamicalSystem

   :param outputDimension: Numbered from 0
   :return: Units of output in the given dimension

getState
^^^^^^^^

.. java:method:: public float[] getState()
   :outertype: DynamicalSystem

   :return: State vector

setState
^^^^^^^^

.. java:method:: public void setState(float[] state)
   :outertype: DynamicalSystem

   :param state: New state vector

