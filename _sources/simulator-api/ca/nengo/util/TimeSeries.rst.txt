.. java:import:: java.io Serializable

.. java:import:: ca.nengo.model Units

TimeSeries
==========

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface TimeSeries extends Serializable, Cloneable

   A series of vector values at ordered points in time.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public TimeSeries clone() throws CloneNotSupportedException
   :outertype: TimeSeries

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: TimeSeries

   :return: dimension of vector values

getLabels
^^^^^^^^^

.. java:method:: public String[] getLabels()
   :outertype: TimeSeries

   :return: Name of each series (numbered by default)

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: TimeSeries

   :return: Name of the TimeSeries

getTimes
^^^^^^^^

.. java:method:: public float[] getTimes()
   :outertype: TimeSeries

   :return: Times for which values are available

getUnits
^^^^^^^^

.. java:method:: public Units[] getUnits()
   :outertype: TimeSeries

   :return: Units in which values in each dimension are expressed (length equals getDimension())

getValues
^^^^^^^^^

.. java:method:: public float[][] getValues()
   :outertype: TimeSeries

   :return: Values at getTimes(). Each value is a vector of size getDimension()

