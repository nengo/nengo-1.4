.. java:import:: junit.framework TestCase

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl IdentityFunction

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl BiasOrigin

.. java:import:: ca.nengo.model.nef.impl BiasTermination

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util Probe

ProjectionImplTest
==================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class ProjectionImplTest extends TestCase

   Unit tests for ProjectionImpl.

   :author: Bryan Tripp

Methods
-------
main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: ProjectionImplTest

setUp
^^^^^

.. java:method:: @Override protected void setUp() throws Exception
   :outertype: ProjectionImplTest

testAddBias2D
^^^^^^^^^^^^^

.. java:method:: public void testAddBias2D() throws StructuralException, SimulationException
   :outertype: ProjectionImplTest

testGetOrigin
^^^^^^^^^^^^^

.. java:method:: public void testGetOrigin()
   :outertype: ProjectionImplTest

testGetTermination
^^^^^^^^^^^^^^^^^^

.. java:method:: public void testGetTermination()
   :outertype: ProjectionImplTest

