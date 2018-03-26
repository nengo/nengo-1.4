.. java:import:: java.io File

.. java:import:: java.io FileNotFoundException

.. java:import:: java.util Scanner

.. java:import:: junit.framework TestCase

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.util Probe

WriteToDiskSimulatorListenerTest
================================

.. java:package:: ca.nengo.sim.impl
   :noindex:

.. java:type:: public class WriteToDiskSimulatorListenerTest extends TestCase

   Unit tests for WriteToDiskSimulatorListener.

   :author: Trevor Bekolay

Methods
-------
testBasicWriting
^^^^^^^^^^^^^^^^

.. java:method:: public void testBasicWriting() throws StructuralException, SimulationException
   :outertype: WriteToDiskSimulatorListenerTest

testInterval
^^^^^^^^^^^^

.. java:method:: public void testInterval() throws StructuralException, SimulationException, FileNotFoundException
   :outertype: WriteToDiskSimulatorListenerTest

