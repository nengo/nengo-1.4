.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: ca.nengo TestUtil

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.neuron.impl IzhikevichSpikeGenerator

.. java:import:: ca.nengo.model.neuron.impl LinearSynapticIntegrator

.. java:import:: ca.nengo.model.neuron.impl ExpandableSpikingNeuron

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util Probe

.. java:import:: junit.framework TestCase

IzhikevichSpikeGeneratorTest
============================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class IzhikevichSpikeGeneratorTest extends TestCase

   Unit tests for IzhikevichSpikeGenerator.

   :author: Bryan Tripp

Methods
-------
main
^^^^

.. java:method:: public static void main(String[] args) throws StructuralException, SimulationException
   :outertype: IzhikevichSpikeGeneratorTest

   Plots voltage and recovery variable for a simulation

setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: IzhikevichSpikeGeneratorTest

testRun
^^^^^^^

.. java:method:: public void testRun()
   :outertype: IzhikevichSpikeGeneratorTest

testSetPreset
^^^^^^^^^^^^^

.. java:method:: public void testSetPreset()
   :outertype: IzhikevichSpikeGeneratorTest

