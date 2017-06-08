.. java:import:: java.io Serializable

InterpolatorND
==============

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface InterpolatorND extends Serializable

   A tool for interpolating within a VECTOR time series (see also Interpolator for scalar time series').

   :author: Bryan Tripp

Methods
-------
interpolate
^^^^^^^^^^^

.. java:method:: public float[] interpolate(float time)
   :outertype: InterpolatorND

setTimeSeries
^^^^^^^^^^^^^

.. java:method:: public void setTimeSeries(TimeSeries series)
   :outertype: InterpolatorND

