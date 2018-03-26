.. java:import:: ca.nengo.math Function

Convolution
===========

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class Convolution extends AbstractFunction

   A numerical convolution of two one-dimensional functions. TODO: unit tests

   :author: Bryan Tripp

Constructors
------------
Convolution
^^^^^^^^^^^

.. java:constructor:: public Convolution(Function one, Function two, float stepSize, float window)
   :outertype: Convolution

   :param one: First of two functions to convolve together
   :param two: Second of two functions to convolve together
   :param stepSize: Step size at which to numerically evaluate convolution integral
   :param window: Window over which to evaluate convolution integral

Methods
-------
clone
^^^^^

.. java:method:: @Override public Convolution clone() throws CloneNotSupportedException
   :outertype: Convolution

getFunctionOne
^^^^^^^^^^^^^^

.. java:method:: public Function getFunctionOne()
   :outertype: Convolution

   :return: First of two functions to convolve together

getFunctionTwo
^^^^^^^^^^^^^^

.. java:method:: public Function getFunctionTwo()
   :outertype: Convolution

   :return: Second of two functions to convolve together

getStepSize
^^^^^^^^^^^

.. java:method:: public float getStepSize()
   :outertype: Convolution

   :return: Step size at which to numerically evaluate convolution integral

getWindow
^^^^^^^^^

.. java:method:: public float getWindow()
   :outertype: Convolution

   :return: Window over which to evaluate convolution integral

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: Convolution

   **See also:** :java:ref:`ca.nengo.math.impl.AbstractFunction.map(float[])`

setFunctionOne
^^^^^^^^^^^^^^

.. java:method:: public void setFunctionOne(Function function)
   :outertype: Convolution

   :param function: First of two functions to convolve together

setFunctionTwo
^^^^^^^^^^^^^^

.. java:method:: public void setFunctionTwo(Function function)
   :outertype: Convolution

   :param function: Second of two functions to convolve together

setStepSize
^^^^^^^^^^^

.. java:method:: public void setStepSize(float stepSize)
   :outertype: Convolution

   :param stepSize: Step size at which to numerically evaluate convolution integral

setWindow
^^^^^^^^^

.. java:method:: public void setWindow(float window)
   :outertype: Convolution

   :param window: Window over which to evaluate convolution integral

