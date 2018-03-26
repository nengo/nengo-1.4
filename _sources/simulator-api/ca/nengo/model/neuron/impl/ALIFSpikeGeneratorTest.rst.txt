.. java:import:: junit.framework TestCase

.. java:import:: ca.nengo TestUtil

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl PiecewiseConstantFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl EnsembleImpl

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util TimeSeries

ALIFSpikeGeneratorTest
======================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class ALIFSpikeGeneratorTest extends TestCase

   Unit tests for ALIFSpikeGenerator.

   :author: Bryan Tripp

Methods
-------
main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: ALIFSpikeGeneratorTest

main2
^^^^^

.. java:method:: public static void main2(String[] args)
   :outertype: ALIFSpikeGeneratorTest

setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: ALIFSpikeGeneratorTest

testAdaptation
^^^^^^^^^^^^^^

.. java:method:: public void testAdaptation() throws StructuralException, SimulationException
   :outertype: ALIFSpikeGeneratorTest

testGetAdaptedRate
^^^^^^^^^^^^^^^^^^

.. java:method:: public void testGetAdaptedRate() throws SimulationException
   :outertype: ALIFSpikeGeneratorTest

testGetOnsetRate
^^^^^^^^^^^^^^^^

.. java:method:: public void testGetOnsetRate() throws SimulationException
   :outertype: ALIFSpikeGeneratorTest

testRun
^^^^^^^

.. java:method:: public void testRun() throws SimulationException
   :outertype: ALIFSpikeGeneratorTest

