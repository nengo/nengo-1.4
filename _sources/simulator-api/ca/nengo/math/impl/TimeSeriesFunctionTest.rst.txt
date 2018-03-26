.. java:import:: junit.framework TestCase

.. java:import:: ca.nengo TestUtil

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

TimeSeriesFunctionTest
======================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class TimeSeriesFunctionTest extends TestCase

   Unit tests for TimeSeriesFunction.

   :author: Bryan Tripp

Constructors
------------
TimeSeriesFunctionTest
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TimeSeriesFunctionTest(String arg0)
   :outertype: TimeSeriesFunctionTest

   :param arg0:

Methods
-------
main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: TimeSeriesFunctionTest

setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: TimeSeriesFunctionTest

   **See also:** :java:ref:`junit.framework.TestCase.setUp()`

testMap
^^^^^^^

.. java:method:: public void testMap() throws StructuralException
   :outertype: TimeSeriesFunctionTest

   Test method for \ :java:ref:`ca.nengo.math.impl.TimeSeriesFunction.map(float[])`\ .

   :throws StructuralException:

