.. java:import:: ca.nengo.math DifferentiableFunction

.. java:import:: ca.nengo.math Function

NumericallyDifferentiableFunction
=================================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class NumericallyDifferentiableFunction implements DifferentiableFunction

   A wrapper around any Function that provides a numerical approximation of its derivative, so that it can be used as a DifferentiableFunction. A Function should provide its exact derivative if available, rather than forcing callers to rely on this wrapper. TODO: test

   :author: Bryan Tripp

Constructors
------------
NumericallyDifferentiableFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public NumericallyDifferentiableFunction(Function function, int derivativeDimension, float delta)
   :outertype: NumericallyDifferentiableFunction

   :param function: An underlying Function
   :param derivativeDimension: The dimension along which the derivative is to be calculated (note that the gradient of a multi-dimensional Function consists of multiple DifferentiableFunctions)
   :param delta: Derivative approximation of f(x) is [f(x+delta)-f(x-delta)]/[2*delta]

NumericallyDifferentiableFunction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public NumericallyDifferentiableFunction()
   :outertype: NumericallyDifferentiableFunction

   Uses dummy parameters to allow setting after construction.

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: NumericallyDifferentiableFunction

getDelta
^^^^^^^^

.. java:method:: public float getDelta()
   :outertype: NumericallyDifferentiableFunction

   :return: Delta in derivative approximation [f(x+delta)-f(x-delta)]/[2*delta]

getDerivative
^^^^^^^^^^^^^

.. java:method:: public Function getDerivative()
   :outertype: NumericallyDifferentiableFunction

   :return: A numerical approximation of the derivative

   **See also:** :java:ref:`ca.nengo.math.DifferentiableFunction.getDerivative()`

getDerivativeDimension
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getDerivativeDimension()
   :outertype: NumericallyDifferentiableFunction

   :return: The dimension along which the derivative is to be calculated

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: NumericallyDifferentiableFunction

   Passed through to underlying Function.

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getFunction
^^^^^^^^^^^

.. java:method:: public Function getFunction()
   :outertype: NumericallyDifferentiableFunction

   :return: The underlying Function

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: NumericallyDifferentiableFunction

   Passed through to underlying Function.

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: NumericallyDifferentiableFunction

   Passed through to underlying Function.

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setFunction
^^^^^^^^^^^

.. java:method:: public void setFunction(Function function)
   :outertype: NumericallyDifferentiableFunction

   :param function: A new underlying Function

