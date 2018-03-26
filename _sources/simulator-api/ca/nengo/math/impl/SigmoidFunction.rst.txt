.. java:import:: ca.nengo.math DifferentiableFunction

.. java:import:: ca.nengo.math Function

SigmoidFunction
===============

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class SigmoidFunction extends AbstractFunction implements DifferentiableFunction

   A one-dimensional sigmoid function with configurable high and low values, slope, and inflection point. TODO: unit tests

   :author: Bryan Tripp

Constructors
------------
SigmoidFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public SigmoidFunction()
   :outertype: SigmoidFunction

   Default parameters (inflection=0; slope=1/4; low=0; high=1).

SigmoidFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public SigmoidFunction(float inflection, float slope, float low, float high)
   :outertype: SigmoidFunction

   :param inflection: Inflection point
   :param slope: Slope at inflection point (usually 1/4)
   :param low: Result for inputs much lower than inflection point
   :param high: Result for inputs much higher than inflection point

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: SigmoidFunction

getDerivative
^^^^^^^^^^^^^

.. java:method:: public Function getDerivative()
   :outertype: SigmoidFunction

   **See also:** :java:ref:`ca.nengo.math.DifferentiableFunction.getDerivative()`

getHigh
^^^^^^^

.. java:method:: public float getHigh()
   :outertype: SigmoidFunction

   :return: Result for inputs much higher than inflection point

getInflection
^^^^^^^^^^^^^

.. java:method:: public float getInflection()
   :outertype: SigmoidFunction

   :return: Inflection point

getLow
^^^^^^

.. java:method:: public float getLow()
   :outertype: SigmoidFunction

   :return: Result for inputs much lower than inflection point

getSlope
^^^^^^^^

.. java:method:: public float getSlope()
   :outertype: SigmoidFunction

   :return: Slope at inflection point

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: SigmoidFunction

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

setHigh
^^^^^^^

.. java:method:: public void setHigh(float high)
   :outertype: SigmoidFunction

   :param high: Result for inputs much higher than inflection point

setInflection
^^^^^^^^^^^^^

.. java:method:: public void setInflection(float inflection)
   :outertype: SigmoidFunction

   :param inflection: Inflection point

setLow
^^^^^^

.. java:method:: public void setLow(float low)
   :outertype: SigmoidFunction

   :param low: Result for inputs much lower than inflection point

setSlope
^^^^^^^^

.. java:method:: public void setSlope(float slope)
   :outertype: SigmoidFunction

   :param slope: Slope at inflection point

