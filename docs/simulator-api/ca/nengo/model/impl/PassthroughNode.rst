.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

PassthroughNode
===============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class PassthroughNode implements Node

   A Node that passes values through unaltered.

   This can be useful if an input to a Network is actually routed to multiple destinations, but you want to handle this connectivity within the Network rather than expose multiple terminations.

   :author: Bryan Tripp

Fields
------
ORIGIN
^^^^^^

.. java:field:: public static final String ORIGIN
   :outertype: PassthroughNode

   Default name for an origin

TERMINATION
^^^^^^^^^^^

.. java:field:: public static final String TERMINATION
   :outertype: PassthroughNode

   Default name for a termination

Constructors
------------
PassthroughNode
^^^^^^^^^^^^^^^

.. java:constructor:: public PassthroughNode(String name, int dimension)
   :outertype: PassthroughNode

   Constructor for a simple passthrough with single input.

   :param name: Node name
   :param dimension: Dimension of data passing through

PassthroughNode
^^^^^^^^^^^^^^^

.. java:constructor:: public PassthroughNode(String name, int dimension, Map<String, float[][]> termDefinitions)
   :outertype: PassthroughNode

   Constructor for a summing junction with multiple inputs.

   :param name: Node name
   :param dimension: Dimension of data passing through
   :param termDefinitions: Name of each Termination (TERMINATION is used for the single-input case) and associated transform

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public Node clone() throws CloneNotSupportedException
   :outertype: PassthroughNode

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: PassthroughNode

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: PassthroughNode

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: PassthroughNode

   Does nothing (only DEFAULT mode is supported).

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: PassthroughNode

   :param name: The new name

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: PassthroughNode

