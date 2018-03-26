.. java:import:: ca.nengo.util IndexFinder

.. java:import:: ca.nengo.util InterpolatorND

.. java:import:: ca.nengo.util TimeSeries

LinearInterpolatorND
====================

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class LinearInterpolatorND implements InterpolatorND

   Interpolates linearly between adjacent values of a vector time series. TODO: test

   :author: Bryan Tripp

Constructors
------------
LinearInterpolatorND
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public LinearInterpolatorND(TimeSeries series)
   :outertype: LinearInterpolatorND

   :param series: Series to interpolate

Methods
-------
clone
^^^^^

.. java:method:: @Override protected LinearInterpolatorND clone() throws CloneNotSupportedException
   :outertype: LinearInterpolatorND

getFinder
^^^^^^^^^

.. java:method:: public IndexFinder getFinder(float[] times)
   :outertype: LinearInterpolatorND

   Uses a StatefulIndexFinder by default. Override to change this.

   :param times: Times of time series
   :return: IndexFinder on times

interpolate
^^^^^^^^^^^

.. java:method:: public float[] interpolate(float time)
   :outertype: LinearInterpolatorND

   **See also:** :java:ref:`ca.nengo.util.InterpolatorND.interpolate(float)`

setTimeSeries
^^^^^^^^^^^^^

.. java:method:: public void setTimeSeries(TimeSeries series)
   :outertype: LinearInterpolatorND

   **See also:** :java:ref:`ca.nengo.util.InterpolatorND.setTimeSeries(ca.nengo.util.TimeSeries)`

