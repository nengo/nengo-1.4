.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util InterpolatorND

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: ca.nengo.util.impl LinearInterpolatorND

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

TimeSeriesFunction
==================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class TimeSeriesFunction extends AbstractFunction

   A Function based on interpolation of a TimeSeries.

   A TimeSeriesFunction can be used to apply the results of a simulation as input to other simulations.

   TODO: unit tests TODO: this could be made more efficient for n-D series by wrapping them in 1D, so interpolation is only done as needed

   :author: Bryan Tripp

Constructors
------------
TimeSeriesFunction
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TimeSeriesFunction(TimeSeries series, int dimension)
   :outertype: TimeSeriesFunction

   :param series: TimeSeries from which to obtain Function of time
   :param dimension: Dimension of series on which to base Function output

Methods
-------
getSeriesDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public int getSeriesDimension()
   :outertype: TimeSeriesFunction

   :return: Dimension of series on which to base Function output

getTimeSeries
^^^^^^^^^^^^^

.. java:method:: public TimeSeries getTimeSeries()
   :outertype: TimeSeriesFunction

   :return: TimeSeries from which to obtain Function of time

makeSeries
^^^^^^^^^^

.. java:method:: public static TimeSeries1D makeSeries(Function function, float start, float increment, float end, Units units)
   :outertype: TimeSeriesFunction

   :param function: A 1-dimensional Function
   :param start: Start time
   :param increment: Time step
   :param end: End time
   :param units: Units of Function output
   :return: A TimeSeries consisting of values output by the given function over the given time range

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: TimeSeriesFunction

   **See also:** :java:ref:`ca.nengo.math.impl.AbstractFunction.map(float[])`

setSeriesDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setSeriesDimension(int dim)
   :outertype: TimeSeriesFunction

   :param dim: Dimension of series on which to base Function output

setTimeSeries
^^^^^^^^^^^^^

.. java:method:: public void setTimeSeries(TimeSeries series)
   :outertype: TimeSeriesFunction

   :param series: TimeSeries from which to obtain Function of time

