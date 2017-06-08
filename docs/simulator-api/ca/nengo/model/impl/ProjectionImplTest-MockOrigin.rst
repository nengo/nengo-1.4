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

ProjectionImplTest.MockOrigin
=============================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public static class MockOrigin implements Origin
   :outertype: ProjectionImplTest

Constructors
------------
MockOrigin
^^^^^^^^^^

.. java:constructor:: public MockOrigin(String name, int dimensions)
   :outertype: ProjectionImplTest.MockOrigin

Methods
-------
clone
^^^^^

.. java:method:: @Override public Origin clone() throws CloneNotSupportedException
   :outertype: ProjectionImplTest.MockOrigin

clone
^^^^^

.. java:method:: public Origin clone(Node node) throws CloneNotSupportedException
   :outertype: ProjectionImplTest.MockOrigin

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: ProjectionImplTest.MockOrigin

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: ProjectionImplTest.MockOrigin

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: ProjectionImplTest.MockOrigin

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: ProjectionImplTest.MockOrigin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues()
   :outertype: ProjectionImplTest.MockOrigin

setDimensions
^^^^^^^^^^^^^

.. java:method:: public void setDimensions(int dim)
   :outertype: ProjectionImplTest.MockOrigin

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: ProjectionImplTest.MockOrigin

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: ProjectionImplTest.MockOrigin

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput val)
   :outertype: ProjectionImplTest.MockOrigin

