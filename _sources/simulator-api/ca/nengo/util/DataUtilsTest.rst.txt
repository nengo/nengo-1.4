.. java:import:: ca.nengo TestUtil

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl SineFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl SpikePatternImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

.. java:import:: junit.framework TestCase

DataUtilsTest
=============

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public class DataUtilsTest extends TestCase

   Unit tests for DataUtils.

   :author: Bryan Tripp

Methods
-------
functionalTestSort
^^^^^^^^^^^^^^^^^^

.. java:method:: public void functionalTestSort() throws StructuralException, SimulationException
   :outertype: DataUtilsTest

   Note: this isn't run automatically but it's run from the main()

   :throws SimulationException:
   :throws StructuralException:

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: DataUtilsTest

setUp
^^^^^

.. java:method:: @Override protected void setUp() throws Exception
   :outertype: DataUtilsTest

testExtractDimension
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void testExtractDimension()
   :outertype: DataUtilsTest

testExtractTime
^^^^^^^^^^^^^^^

.. java:method:: public void testExtractTime()
   :outertype: DataUtilsTest

testSubsample
^^^^^^^^^^^^^

.. java:method:: public void testSubsample()
   :outertype: DataUtilsTest

testSubsetSpikePatternIntArray
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void testSubsetSpikePatternIntArray()
   :outertype: DataUtilsTest

testSubsetSpikePatternIntIntInt
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void testSubsetSpikePatternIntIntInt()
   :outertype: DataUtilsTest

