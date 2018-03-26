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

ProjectionImplTest.MockTermination
==================================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public static class MockTermination implements Termination
   :outertype: ProjectionImplTest

Constructors
------------
MockTermination
^^^^^^^^^^^^^^^

.. java:constructor:: public MockTermination(String name, int dimensions)
   :outertype: ProjectionImplTest.MockTermination

Methods
-------
clone
^^^^^

.. java:method:: @Override public MockTermination clone() throws CloneNotSupportedException
   :outertype: ProjectionImplTest.MockTermination

clone
^^^^^

.. java:method:: public MockTermination clone(Node node) throws CloneNotSupportedException
   :outertype: ProjectionImplTest.MockTermination

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: ProjectionImplTest.MockTermination

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: ProjectionImplTest.MockTermination

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: ProjectionImplTest.MockTermination

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: ProjectionImplTest.MockTermination

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: ProjectionImplTest.MockTermination

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: ProjectionImplTest.MockTermination

propertyChange
^^^^^^^^^^^^^^

.. java:method:: public void propertyChange(String propertyName, Object newValue)
   :outertype: ProjectionImplTest.MockTermination

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: ProjectionImplTest.MockTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: ProjectionImplTest.MockTermination

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: ProjectionImplTest.MockTermination

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: ProjectionImplTest.MockTermination

