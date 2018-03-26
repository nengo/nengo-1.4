.. java:import:: ca.nengo.math DifferentiableFunction

.. java:import:: ca.nengo.math Function

NumericallyDifferentiableFunction.NumericalDerivative
=====================================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public static class NumericalDerivative implements Function
   :outertype: NumericallyDifferentiableFunction

   :author: Bryan Tripp

Constructors
------------
NumericalDerivative
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public NumericalDerivative(Function function, int derivativeDimension, float delta)
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :param function: The Function of which the derivative is to be approximated
   :param derivativeDimension: The dimension along which the derivative is to be calculated
   :param delta: Derivative approximation of f(x) is [f(x+delta)-f(x-delta)]/[2*delta]

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

getDelta
^^^^^^^^

.. java:method:: public float getDelta()
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :return: The variable delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]

getDerivativeDimension
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getDerivativeDimension()
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :return: The dimension along which the derivative is to be calculated

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getFunction
^^^^^^^^^^^

.. java:method:: public Function getFunction()
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :return: The Function of which the derivative is to be approximated

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :return: An approximation of the derivative of the underlying Function

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :return: Approximations of the derivative of the underlying Function at multiple points

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setDelta
^^^^^^^^

.. java:method:: public void setDelta(float delta)
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :param delta: The variable delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]

setDerivativeDimension
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDerivativeDimension(int dim)
   :outertype: NumericallyDifferentiableFunction.NumericalDerivative

   :param dim: The dimension along which the derivative is to be calculated

