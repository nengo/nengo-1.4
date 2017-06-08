.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.neuron ExpandableSynapticIntegrator

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron.impl LIFSpikeGenerator

.. java:import:: ca.nengo.model.neuron.impl LinearSynapticIntegrator

.. java:import:: ca.nengo.model.neuron.impl SpikeGeneratorOrigin

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: junit.framework TestCase

SpikingNeuronTest
=================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class SpikingNeuronTest extends TestCase

   Unit tests for SpikingNeuron.

   :author: Bryan Tripp

Methods
-------
setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: SpikingNeuronTest

testGetHistory
^^^^^^^^^^^^^^

.. java:method:: public void testGetHistory() throws SimulationException
   :outertype: SpikingNeuronTest

testGetMode
^^^^^^^^^^^

.. java:method:: public void testGetMode()
   :outertype: SpikingNeuronTest

testGetOrigins
^^^^^^^^^^^^^^

.. java:method:: public void testGetOrigins()
   :outertype: SpikingNeuronTest

testRun
^^^^^^^

.. java:method:: public void testRun() throws StructuralException, SimulationException
   :outertype: SpikingNeuronTest

