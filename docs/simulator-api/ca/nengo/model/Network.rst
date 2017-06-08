.. java:import:: java.util HashMap

.. java:import:: ca.nengo.sim Simulator

.. java:import:: ca.nengo.util ScriptGenException

Network
=======

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface Network extends Node, Probeable

   A neural circuit, consisting of Nodes such as Ensembles and ExternalInputs. A Network is the usual object of a simulation. If you are new to this code, what you probably want to do is create some Neurons, group them into Ensembles, connect the Ensembles in a Network, and run the Network in a Simulator.

   Note: Multiple steps are needed to add a Projection between Ensembles. First, an Origin must be created on the presynaptic Ensemble, and a Termination with the same dimensionality must be created on the post-synaptic Ensemble. Then the Origin and Termination can be connected with the method addProjection(Origin, Termination). We don't do this in one step (ie automatically create the necessary Origin and Termination as needed) because there are various ways of doing so, and in fact some types of Origins and Terminations can only be created in the course of constructing the Ensemble. Creation of an Origin or Termination can also be a complex process. Rather than try to abstract these varied procedures into something that can be driven from the Network level, we just assume here that the necessary Origins and Terminations exist, and provide a method for connecting them.

   :author: Bryan Tripp

Methods
-------
addNode
^^^^^^^

.. java:method:: public void addNode(Node node) throws StructuralException
   :outertype: Network

   :param node: Node to add to the Network
   :throws StructuralException: if the Network already contains a Node of the same name

addProjection
^^^^^^^^^^^^^

.. java:method:: public Projection addProjection(Origin origin, Termination termination) throws StructuralException
   :outertype: Network

   Connects an Origin to a Termination. Origins and Terminations belong to Ensembles (or ExternalInputs). Both the Origin and Termination must be set up before calling this method. The way to do this will depend on the Ensemble.

   :param origin: Origin (data source) of Projection.
   :param termination: Termination (data destination) of Projection.
   :throws StructuralException: if the given Origin and Termination have different dimensions, or if there is already an Origin connected to the given Termination (note that an Origin can project to multiple Terminations though).
   :return: The created Projection

addStepListener
^^^^^^^^^^^^^^^

.. java:method:: public void addStepListener(StepListener listener)
   :outertype: Network

exposeOrigin
^^^^^^^^^^^^

.. java:method:: public void exposeOrigin(Origin origin, String name)
   :outertype: Network

   Declares the given Origin as available for connection outside the Network via getOrigins(). This Origin should not be connected within this Network.

   :param origin: An Origin within this Network that is to connect to something outside this Network
   :param name: Name of the Origin as it will appear outside this Network

exposeState
^^^^^^^^^^^

.. java:method:: public void exposeState(Probeable probeable, String stateName, String name) throws StructuralException
   :outertype: Network

   Declares the given Probeable state as being available for Probing from outside this Network.

   :param probeable: A Probeable within this Network.
   :param stateName: A state of the given Probeable
   :param name: A new name with which to access this state via Network.getHistory
   :throws StructuralException: if Probeable not in the Network

exposeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public void exposeTermination(Termination termination, String name)
   :outertype: Network

   Declares the given Termination as available for connection from outside the Network via getTerminations(). This Termination should not be connected within this Network.

   :param termination: A Termination within this Network that is to connect to something outside this Network
   :param name: Name of the Termination as it will appear outside this Network

fireStepListeners
^^^^^^^^^^^^^^^^^

.. java:method:: public void fireStepListeners(float time)
   :outertype: Network

getExposedOriginName
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExposedOriginName(Origin insideOrigin)
   :outertype: Network

   :param insideOrigin: Origin inside the network
   :return: Name of the exposed origin given the inner origin. null if no such origin is exposed.

getExposedTerminationName
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExposedTerminationName(Termination insideTermination)
   :outertype: Network

   :param insideTermination: Termination inside the network
   :return: Name of the exposed termination given the inner termination or null if no such termination is exposed.

getMetaData
^^^^^^^^^^^

.. java:method:: public Object getMetaData(String key)
   :outertype: Network

   Metadata is non-critical information about the Network (eg UI layout) that the user doesn't access directly. (Note: if there is a need for user-accessible metadata, Network could extend Configurable, but this doesn't seem to be necessary.)

   :param key: Name of a metadata item
   :return: Value of a metadata item

getNode
^^^^^^^

.. java:method:: public Node getNode(String name) throws StructuralException
   :outertype: Network

   :param name: Name of Node to remove
   :throws StructuralException: if named Node does not exist in network
   :return: Named node

getNodes
^^^^^^^^

.. java:method:: public Node[] getNodes()
   :outertype: Network

   :return: All the Nodes in the Network

getProjections
^^^^^^^^^^^^^^

.. java:method:: public Projection[] getProjections()
   :outertype: Network

   :return: All Projections in this Network

getSimulator
^^^^^^^^^^^^

.. java:method:: public Simulator getSimulator()
   :outertype: Network

   :return: The Simulator used to run this Network

hideOrigin
^^^^^^^^^^

.. java:method:: public void hideOrigin(String name) throws StructuralException
   :outertype: Network

   Undoes exposeOrigin(x, x, name).

   :param name: Name of Origin to unexpose.
   :throws StructuralException: if Origin does not exist

hideState
^^^^^^^^^

.. java:method:: public void hideState(String name)
   :outertype: Network

   Undoes exposeState(x, x, name).

   :param name: Name of state to unexpose.

hideTermination
^^^^^^^^^^^^^^^

.. java:method:: public void hideTermination(String name)
   :outertype: Network

   Undoes exposeTermination(x, x, name).

   :param name: Name of Termination to unexpose.

removeNode
^^^^^^^^^^

.. java:method:: public void removeNode(String name) throws StructuralException
   :outertype: Network

   :param name: Name of Node to remove
   :throws StructuralException: if named Node does not exist in network

removeProjection
^^^^^^^^^^^^^^^^

.. java:method:: public void removeProjection(Termination termination) throws StructuralException
   :outertype: Network

   :param termination: Termination of Projection to remove
   :throws StructuralException: if there exists no Projection between the specified Origin and Termination

removeStepListener
^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeStepListener(StepListener listener)
   :outertype: Network

setMetaData
^^^^^^^^^^^

.. java:method:: public void setMetaData(String key, Object value)
   :outertype: Network

   :param key: Name of a metadata item
   :param value: Value of the named metadata item

setSimulator
^^^^^^^^^^^^

.. java:method:: public void setSimulator(Simulator simulator)
   :outertype: Network

   :param simulator: The Simulator used to run this Network

toPostScript
^^^^^^^^^^^^

.. java:method:: public String toPostScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: Network

   :param scriptData: Map of class parent and prefix data for generating python script
   :throws ScriptGenException: if the node cannot be generated in script
   :return: Python script for generating special or template ensembles and terminations in the network

