.. java:import:: ca.nengo.model Units

TimeSeries1D
============

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface TimeSeries1D extends TimeSeries

   A TimeSeries that consists of 1-dimensional values, with convenience methods for accessing 1D values and units.

   :author: Bryan Tripp

Methods
-------
getUnits1D
^^^^^^^^^^

.. java:method:: public Units getUnits1D()
   :outertype: TimeSeries1D

   :return: Units in which values are expressed

getValues1D
^^^^^^^^^^^

.. java:method:: public float[] getValues1D()
   :outertype: TimeSeries1D

   :return: Values at getTimes()

