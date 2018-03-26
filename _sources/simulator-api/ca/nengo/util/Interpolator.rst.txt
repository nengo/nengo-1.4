.. java:import:: java.io Serializable

Interpolator
============

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface Interpolator extends Serializable, Cloneable

   A tool for interpolating within a SCALAR time series (see also InterpolatorND for vector time series').

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public Interpolator clone() throws CloneNotSupportedException
   :outertype: Interpolator

interpolate
^^^^^^^^^^^

.. java:method:: public float interpolate(float time)
   :outertype: Interpolator

setTimeSeries
^^^^^^^^^^^^^

.. java:method:: public void setTimeSeries(TimeSeries1D series)
   :outertype: Interpolator

