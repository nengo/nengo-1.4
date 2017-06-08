.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util TimeSeries

TimeSeriesImpl
==============

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class TimeSeriesImpl implements TimeSeries

   Default implementation of TimeSeriesND.

   :author: Bryan Tripp

Constructors
------------
TimeSeriesImpl
^^^^^^^^^^^^^^

.. java:constructor:: public TimeSeriesImpl(float[] times, float[][] values, Units[] units)
   :outertype: TimeSeriesImpl

   :param times: @see ca.bpt.cn.util.TimeSeries#getTimes()
   :param values: @see ca.bpt.cn.util.TimeSeries#getValues()
   :param units: @see ca.bpt.cn.util.TimeSeries#getUnits()

TimeSeriesImpl
^^^^^^^^^^^^^^

.. java:constructor:: public TimeSeriesImpl(float[] times, float[][] values, Units[] units, String[] labels)
   :outertype: TimeSeriesImpl

   :param times: @see ca.nengo.util.TimeSeries#getTimes()
   :param values: @see ca.nengo.util.TimeSeries#getValues()
   :param units: @see ca.nengo.util.TimeSeries#getUnits()
   :param labels: @see ca.nengo.util.TimeSeries#getLabels()

Methods
-------
clone
^^^^^

.. java:method:: @Override public TimeSeries clone() throws CloneNotSupportedException
   :outertype: TimeSeriesImpl

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: TimeSeriesImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getDimension()`

getLabels
^^^^^^^^^

.. java:method:: public String[] getLabels()
   :outertype: TimeSeriesImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getLabels()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: TimeSeriesImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getName()`

getTimes
^^^^^^^^

.. java:method:: public float[] getTimes()
   :outertype: TimeSeriesImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries1D.getTimes()`

getUnits
^^^^^^^^

.. java:method:: public Units[] getUnits()
   :outertype: TimeSeriesImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries1D.getUnits()`

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: TimeSeriesImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries1D.getValues()`

setLabel
^^^^^^^^

.. java:method:: public void setLabel(int index, String label)
   :outertype: TimeSeriesImpl

   :param index: Index of dimension for which to change label
   :param label: New label for given dimension

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: TimeSeriesImpl

   :param name: Name of the TimeSeries

setUnits
^^^^^^^^

.. java:method:: public void setUnits(int index, Units units)
   :outertype: TimeSeriesImpl

   :param index: Index of dimension for which to change units
   :param units: New units for given dimension

