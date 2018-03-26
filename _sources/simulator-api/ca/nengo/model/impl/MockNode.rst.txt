.. java:import:: java.util HashMap

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.util ScriptGenException

MockNode
========

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class MockNode implements Node, Cloneable

   A Cloneable Node for testing copy&paste / drag&drop.

   :author: Bryan Tripp

Constructors
------------
MockNode
^^^^^^^^

.. java:constructor:: public MockNode(String name)
   :outertype: MockNode

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public MockNode clone() throws CloneNotSupportedException
   :outertype: MockNode

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: MockNode

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: MockNode

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: MockNode

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String myName)
   :outertype: MockNode

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: MockNode

