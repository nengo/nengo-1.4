.. java:import:: java.util Arrays

.. java:import:: java.util HashMap

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util Map

.. java:import:: junit.framework TestCase

.. java:import:: ca.nengo.dynamics.impl SimpleLTISystem

.. java:import:: ca.nengo.model ExpandableNode

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

EnsembleImplTest.MockExpandableNode
===================================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class MockExpandableNode extends AbstractNode implements ExpandableNode
   :outertype: EnsembleImplTest

Constructors
------------
MockExpandableNode
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public MockExpandableNode(String name, Origin[] origins, Termination[] terminations)
   :outertype: EnsembleImplTest.MockExpandableNode

Methods
-------
addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: EnsembleImplTest.MockExpandableNode

clone
^^^^^

.. java:method:: @Override public MockExpandableNode clone() throws CloneNotSupportedException
   :outertype: EnsembleImplTest.MockExpandableNode

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: EnsembleImplTest.MockExpandableNode

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: EnsembleImplTest.MockExpandableNode

getTermination
^^^^^^^^^^^^^^

.. java:method:: @Override public Termination getTermination(String name) throws StructuralException
   :outertype: EnsembleImplTest.MockExpandableNode

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: @Override public Termination[] getTerminations()
   :outertype: EnsembleImplTest.MockExpandableNode

removeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public Termination removeTermination(String name) throws StructuralException
   :outertype: EnsembleImplTest.MockExpandableNode

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: EnsembleImplTest.MockExpandableNode

run
^^^

.. java:method:: @Override public void run(float startTime, float endTime)
   :outertype: EnsembleImplTest.MockExpandableNode

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: EnsembleImplTest.MockExpandableNode

