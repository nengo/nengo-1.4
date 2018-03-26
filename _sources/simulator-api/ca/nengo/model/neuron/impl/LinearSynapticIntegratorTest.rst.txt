.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron ExpandableSynapticIntegrator

.. java:import:: ca.nengo.model.neuron.impl LinearSynapticIntegrator

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: junit.framework TestCase

LinearSynapticIntegratorTest
============================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class LinearSynapticIntegratorTest extends TestCase

   Unit tests for LinearSynapticIntegrator.

   :author: Bryan Tripp

Methods
-------
setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: LinearSynapticIntegratorTest

testGetTerminations
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void testGetTerminations() throws StructuralException
   :outertype: LinearSynapticIntegratorTest

testReset
^^^^^^^^^

.. java:method:: public void testReset() throws StructuralException, SimulationException
   :outertype: LinearSynapticIntegratorTest

testRun
^^^^^^^

.. java:method:: public void testRun() throws StructuralException, SimulationException
   :outertype: LinearSynapticIntegratorTest

