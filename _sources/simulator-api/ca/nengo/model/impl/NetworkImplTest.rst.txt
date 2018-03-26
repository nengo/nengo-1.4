.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: ca.nengo TestUtil

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: junit.framework TestCase

NetworkImplTest
===============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class NetworkImplTest extends TestCase

Methods
-------
setUp
^^^^^

.. java:method:: protected void setUp() throws Exception
   :outertype: NetworkImplTest

testAddNode
^^^^^^^^^^^

.. java:method:: public void testAddNode() throws StructuralException
   :outertype: NetworkImplTest

testClone
^^^^^^^^^

.. java:method:: public void testClone() throws StructuralException, CloneNotSupportedException
   :outertype: NetworkImplTest

testExposeOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public void testExposeOrigin() throws StructuralException
   :outertype: NetworkImplTest

testGetNodeOrigins
^^^^^^^^^^^^^^^^^^

.. java:method:: public void testGetNodeOrigins() throws StructuralException
   :outertype: NetworkImplTest

testGetNodeTerminations
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void testGetNodeTerminations() throws StructuralException
   :outertype: NetworkImplTest

testGetNodes
^^^^^^^^^^^^

.. java:method:: public void testGetNodes() throws StructuralException
   :outertype: NetworkImplTest

testGetProjections
^^^^^^^^^^^^^^^^^^

.. java:method:: public void testGetProjections() throws StructuralException
   :outertype: NetworkImplTest

testHideOrigin
^^^^^^^^^^^^^^

.. java:method:: public void testHideOrigin() throws StructuralException
   :outertype: NetworkImplTest

testKillNeurons
^^^^^^^^^^^^^^^

.. java:method:: public void testKillNeurons() throws StructuralException
   :outertype: NetworkImplTest

testNodeNameChange
^^^^^^^^^^^^^^^^^^

.. java:method:: public void testNodeNameChange() throws StructuralException
   :outertype: NetworkImplTest

testRemoveNode
^^^^^^^^^^^^^^

.. java:method:: public void testRemoveNode() throws StructuralException, SimulationException
   :outertype: NetworkImplTest

testReset
^^^^^^^^^

.. java:method:: public void testReset() throws StructuralException, SimulationException
   :outertype: NetworkImplTest

