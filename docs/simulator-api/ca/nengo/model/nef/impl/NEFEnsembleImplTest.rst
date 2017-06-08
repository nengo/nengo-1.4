.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl BiasOrigin

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

.. java:import:: junit.framework TestCase

NEFEnsembleImplTest
===================

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class NEFEnsembleImplTest extends TestCase

   Unit tests for NEFEnsembleImpl. TODO: this is a functional test with no failures ... convert to unit test TODO: make sure performance optimization works with inhibitory projections

   :author: Bryan Tripp

Methods
-------
functionalTestAddBiasOrigin
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void functionalTestAddBiasOrigin() throws StructuralException, SimulationException
   :outertype: NEFEnsembleImplTest

functionalTestBiasOriginError
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void functionalTestBiasOriginError() throws StructuralException, SimulationException
   :outertype: NEFEnsembleImplTest

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: NEFEnsembleImplTest

setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: NEFEnsembleImplTest

testAddDecodedSignalOrigin
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void testAddDecodedSignalOrigin() throws StructuralException
   :outertype: NEFEnsembleImplTest

testClone
^^^^^^^^^

.. java:method:: public void testClone() throws StructuralException, CloneNotSupportedException
   :outertype: NEFEnsembleImplTest

testKillNeurons
^^^^^^^^^^^^^^^

.. java:method:: public void testKillNeurons() throws StructuralException
   :outertype: NEFEnsembleImplTest

