.. java:import:: java.util Arrays

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl SingleValuedPropertyImpl

.. java:import:: ca.nengo.math Function

InterpolatedFunction
====================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class InterpolatedFunction extends AbstractFunction

   A 1-D Function based on interpolation between known points.

   :author: Bryan Tripp

Constructors
------------
InterpolatedFunction
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public InterpolatedFunction(float[] x, float[] y)
   :outertype: InterpolatedFunction

   :param x: Known function-argument points to interpolate between
   :param y: Known function-output points to interpolate between (must be same length as x)

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: InterpolatedFunction

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: InterpolatedFunction

   :return: Custom configuration

getNumPoints
^^^^^^^^^^^^

.. java:method:: public int getNumPoints()
   :outertype: InterpolatedFunction

   :return: Number of points between which this function interpolates

getX
^^^^

.. java:method:: public float[] getX()
   :outertype: InterpolatedFunction

   :return: Known function-argument points to interpolate between

getY
^^^^

.. java:method:: public float[] getY()
   :outertype: InterpolatedFunction

   :return: Known function-output points to interpolate between

interpolate
^^^^^^^^^^^

.. java:method:: public static float interpolate(float[] xs, float[] ys, float x)
   :outertype: InterpolatedFunction

   :param xs: List of x values
   :param ys: List of y values that x values map onto
   :param x: An x value at which to interpolate this mapping
   :return: The interpolated y value

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: InterpolatedFunction

   **See also:** :java:ref:`ca.nengo.math.impl.AbstractFunction.map(float[])`

setNumPoints
^^^^^^^^^^^^

.. java:method:: public void setNumPoints(int num)
   :outertype: InterpolatedFunction

   If this method is used to increase the number of interpolation points, new points are set to equal what was previously the last point.

   :param num: New number of points between which this function interpolates

setX
^^^^

.. java:method:: public void setX(float[] x)
   :outertype: InterpolatedFunction

   :param x: Known function-argument points to interpolate between

setY
^^^^

.. java:method:: public void setY(float[] y)
   :outertype: InterpolatedFunction

   :param y: Known function-output points to interpolate between

