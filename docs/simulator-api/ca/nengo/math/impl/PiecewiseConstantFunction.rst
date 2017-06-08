.. java:import:: java.util Arrays

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl SingleValuedPropertyImpl

.. java:import:: ca.nengo.math Function

PiecewiseConstantFunction
=========================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class PiecewiseConstantFunction extends AbstractFunction

   A one-dimensional function for which the output is constant between a finite number of discontinuities. TODO: unit test

   :author: Bryan Tripp

Constructors
------------
PiecewiseConstantFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PiecewiseConstantFunction(float[] discontinuities, float[] values)
   :outertype: PiecewiseConstantFunction

   :param discontinuities: Ordered points x at which the function is y = f(x) is discontinuous
   :param values: Values y below x1 and above x1..xn

PiecewiseConstantFunction
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PiecewiseConstantFunction(float[] discontinuities, float[] values, int dimension)
   :outertype: PiecewiseConstantFunction

   A version of the constructor that allows you to specify the dimension (this doesn't do anything since this function makes no use of its input, but it allows these functions to be attached to multidimensional ensembles).

   :param discontinuities: Ordered points x at which the function is y = f(x) is discontinuous
   :param values: Values y below x1 and above x1..xn

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: PiecewiseConstantFunction

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: PiecewiseConstantFunction

   :return: Custom configuration

getDiscontinuities
^^^^^^^^^^^^^^^^^^

.. java:method:: public float[] getDiscontinuities()
   :outertype: PiecewiseConstantFunction

   :return: Ordered points x at which the function is y = f(x) is discontinuous

getNumDiscontinuities
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getNumDiscontinuities()
   :outertype: PiecewiseConstantFunction

   :return: Number of discontinuities

getValues
^^^^^^^^^

.. java:method:: public float[] getValues()
   :outertype: PiecewiseConstantFunction

   :return: Values y below x1 and above x1..xn

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: PiecewiseConstantFunction

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

setDiscontinuities
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDiscontinuities(float[] discontinuities)
   :outertype: PiecewiseConstantFunction

   :param discontinuities: Ordered points x at which the function is y = f(x) is discontinuous

setNumDiscontinuities
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setNumDiscontinuities(int num)
   :outertype: PiecewiseConstantFunction

   :param num: New number of discontinuities

setValues
^^^^^^^^^

.. java:method:: public void setValues(float[] values)
   :outertype: PiecewiseConstantFunction

   :param values: Values y below x1 and above x1..xn

