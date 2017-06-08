.. java:import:: java.io Serializable

.. java:import:: java.util HashMap

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util VisiblyMutable

Node
====

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface Node extends Serializable, Resettable, SimulationMode.ModeConfigurable, VisiblyMutable, Cloneable

   A part of a Network that can be run independently (eg a Neuron). Normally a source of Origins and/or Terminations.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public Node clone() throws CloneNotSupportedException
   :outertype: Node

   :throws CloneNotSupportedException: if clone can't be made
   :return: An independent copy of the Node

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: Node

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: Node

   :return: User-specified documentation for the Node, if any

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: Node

   :return: Name of Node (must be unique in a Network)

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: Node

   :param name: Name of an Origin on this Node
   :throws StructuralException: if the named Origin does not exist
   :return: The named Origin if it exists

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: Node

   :return: Sets of ouput channels (eg spiking outputs, gap junctional outputs, etc.)

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: Node

   :param name: Name of a Termination onto this Node
   :throws StructuralException: if the named Termination does not exist
   :return: The named Termination if it exists

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: Node

   :return: Sets of input channels (these have the same dimension as corresponding Origins to which they are connected).

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: Node

   Runs the Node (including all its components), updating internal state and outputs as needed. Runs should be short (eg 1ms), because inputs can not be changed during a run, and outputs will only be communicated to other Nodes after a run.

   :param startTime: simulation time at which running starts (s)
   :param endTime: simulation time at which running ends (s)
   :throws SimulationException: if a problem is encountered while trying to run

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: Node

   :param text: New user-specified documentation for the Node

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: Node

   :param name: The new name
   :throws StructuralException: if name already exists?

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: Node

   :param scriptData: Map of class parent and prefix data for generating python script
   :throws ScriptGenException: if the node cannot be generated in script
   :return: Python script for generating the node

