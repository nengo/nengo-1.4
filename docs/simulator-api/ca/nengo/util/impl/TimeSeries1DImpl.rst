.. java:import:: java.io Serializable

.. java:import:: java.lang.reflect Method

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config SingleValuedProperty

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.config.impl SingleValuedPropertyImpl

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util TimeSeries1D

TimeSeries1DImpl
================

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class TimeSeries1DImpl implements TimeSeries1D, Serializable

   Default implementation of TimeSeries.

   :author: Bryan Tripp

Constructors
------------
TimeSeries1DImpl
^^^^^^^^^^^^^^^^

.. java:constructor:: public TimeSeries1DImpl(float[] times, float[] values, Units units)
   :outertype: TimeSeries1DImpl

   :param times: @see ca.bpt.cn.util.TimeSeries#getTimes()
   :param values: @see ca.bpt.cn.util.TimeSeries#getValues()
   :param units: @see ca.bpt.cn.util.TimeSeries#getUnits()

Methods
-------
clone
^^^^^

.. java:method:: @Override public TimeSeries1D clone() throws CloneNotSupportedException
   :outertype: TimeSeries1DImpl

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: TimeSeries1DImpl

   :return: Custom Configuration (to more cleanly handle properties in 1D)

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getDimension()`

getLabels
^^^^^^^^^

.. java:method:: public String[] getLabels()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getLabels()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getName()`

getTimes
^^^^^^^^

.. java:method:: public float[] getTimes()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries1D.getTimes()`

getUnits
^^^^^^^^

.. java:method:: public Units[] getUnits()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getUnits()`

getUnits1D
^^^^^^^^^^

.. java:method:: public Units getUnits1D()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries1D.getUnits1D()`

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries.getValues()`

getValues1D
^^^^^^^^^^^

.. java:method:: public float[] getValues1D()
   :outertype: TimeSeries1DImpl

   **See also:** :java:ref:`ca.nengo.util.TimeSeries1D.getValues1D()`

setLabel
^^^^^^^^

.. java:method:: public void setLabel(String label)
   :outertype: TimeSeries1DImpl

   :param label: New label

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: TimeSeries1DImpl

   :param name: Name of the TimeSeries

setUnits
^^^^^^^^

.. java:method:: public void setUnits(Units units)
   :outertype: TimeSeries1DImpl

   :param units: New Units

