.. java:import:: java.io File

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io Serializable

.. java:import:: java.lang.reflect Method

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StepListener

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef.impl DecodableEnsembleImpl

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.sim Simulator

.. java:import:: ca.nengo.sim.impl LocalSimulator

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util TaskSpawner

.. java:import:: ca.nengo.util ThreadTask

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl ProbeTask

.. java:import:: ca.nengo.util.impl ScriptGenerator

NetworkImpl
===========

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class NetworkImpl implements Network, VisiblyMutable, VisiblyMutable.Listener, TaskSpawner

   Default implementation of Network.

   :author: Bryan Tripp

Fields
------
DEFAULT_NAME
^^^^^^^^^^^^

.. java:field:: public static final String DEFAULT_NAME
   :outertype: NetworkImpl

   Default name for a Network

myNumGPU
^^^^^^^^

.. java:field:: protected int myNumGPU
   :outertype: NetworkImpl

myNumJavaThreads
^^^^^^^^^^^^^^^^

.. java:field:: protected int myNumJavaThreads
   :outertype: NetworkImpl

myUseGPU
^^^^^^^^

.. java:field:: protected boolean myUseGPU
   :outertype: NetworkImpl

Constructors
------------
NetworkImpl
^^^^^^^^^^^

.. java:constructor:: public NetworkImpl()
   :outertype: NetworkImpl

   Sets up a network's data structures

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

addNode
^^^^^^^

.. java:method:: public void addNode(Node node) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.addNode(ca.nengo.model.Node)`

addProjection
^^^^^^^^^^^^^

.. java:method:: public Projection addProjection(Origin origin, Termination termination) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.addProjection(ca.nengo.model.Origin,ca.nengo.model.Termination)`

addStepListener
^^^^^^^^^^^^^^^

.. java:method:: public void addStepListener(StepListener listener)
   :outertype: NetworkImpl

addTasks
^^^^^^^^

.. java:method:: public void addTasks(ThreadTask[] tasks)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.util.impl.TaskSpawner.addTasks()`

changed
^^^^^^^

.. java:method:: public void changed(Event e) throws StructuralException
   :outertype: NetworkImpl

   Handles any changes/errors that may arise from objects within the network changing.

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.Listener.changed(ca.nengo.util.VisiblyMutable.Event)`

clone
^^^^^

.. java:method:: @Override public Network clone() throws CloneNotSupportedException
   :outertype: NetworkImpl

countNeurons
^^^^^^^^^^^^

.. java:method:: public int countNeurons()
   :outertype: NetworkImpl

   Counts how many neurons are contained within this network.

   :return: number of neurons in this network

dumpToScript
^^^^^^^^^^^^

.. java:method:: public void dumpToScript() throws FileNotFoundException
   :outertype: NetworkImpl

dumpToScript
^^^^^^^^^^^^

.. java:method:: public void dumpToScript(String filepath) throws FileNotFoundException
   :outertype: NetworkImpl

exposeOrigin
^^^^^^^^^^^^

.. java:method:: public void exposeOrigin(Origin origin, String name)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.exposeOrigin(ca.nengo.model.Origin,
   java.lang.String)`

exposeState
^^^^^^^^^^^

.. java:method:: public void exposeState(Probeable probeable, String stateName, String name) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.exposeState(ca.nengo.model.Probeable,java.lang.String,java.lang.String)`

exposeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public void exposeTermination(Termination termination, String name)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.exposeTermination(ca.nengo.model.Termination,java.lang.String)`

fireStepListeners
^^^^^^^^^^^^^^^^^

.. java:method:: public void fireStepListeners(float time)
   :outertype: NetworkImpl

fixMode
^^^^^^^

.. java:method:: public void fixMode()
   :outertype: NetworkImpl

   Fix the simulation mode to the current mode.

fixMode
^^^^^^^

.. java:method:: public void fixMode(SimulationMode[] modes)
   :outertype: NetworkImpl

   Set the allowed simulation modes.

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: NetworkImpl

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getExposedOriginName
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExposedOriginName(Origin insideOrigin)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getExposedOriginName(ca.nengo.model.Origin)`

getExposedTerminationName
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExposedTerminationName(Termination insideTermination)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getExposedTerminationName(ca.nengo.model.Termination)`

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getMetaData
^^^^^^^^^^^

.. java:method:: public Object getMetaData(String key)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getMetaData(java.lang.String)`

getMetadata
^^^^^^^^^^^

.. java:method:: public Object getMetadata(String key)
   :outertype: NetworkImpl

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getNeuronCount
^^^^^^^^^^^^^^

.. java:method:: public int getNeuronCount()
   :outertype: NetworkImpl

   :return: number of neurons in all levels

getNode
^^^^^^^

.. java:method:: public Node getNode(String name) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getNode(java.lang.String)`

getNodeCount
^^^^^^^^^^^^

.. java:method:: public int getNodeCount()
   :outertype: NetworkImpl

   :return: number of top-level nodes

getNodeOrigins
^^^^^^^^^^^^^^

.. java:method:: public ArrayList<Origin> getNodeOrigins()
   :outertype: NetworkImpl

   Gathers all the origins of nodes contained in this network.

   :return: arraylist of origins

getNodeTerminations
^^^^^^^^^^^^^^^^^^^

.. java:method:: public ArrayList<Termination> getNodeTerminations()
   :outertype: NetworkImpl

   Gathers all the terminations of nodes contained in this network.

   :return: arraylist of terminations

getNodes
^^^^^^^^

.. java:method:: public Node[] getNodes()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getNodes()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getOrigins()`

getProjectionMap
^^^^^^^^^^^^^^^^

.. java:method:: public Map<Termination, Projection> getProjectionMap()
   :outertype: NetworkImpl

getProjections
^^^^^^^^^^^^^^

.. java:method:: public Projection[] getProjections()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getProjections()`

getSimulator
^^^^^^^^^^^^

.. java:method:: public Simulator getSimulator()
   :outertype: NetworkImpl

   :return: Simulator used to run this Network (a LocalSimulator by default)

getStepSize
^^^^^^^^^^^

.. java:method:: public float getStepSize()
   :outertype: NetworkImpl

   :return: Timestep size at which Network is simulated.

getTasks
^^^^^^^^

.. java:method:: public ThreadTask[] getTasks()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.util.impl.TaskSpawner.getTasks()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.getTerminations()`

getUseGPU
^^^^^^^^^

.. java:method:: public boolean getUseGPU()
   :outertype: NetworkImpl

   :return: Using GPU?

hideOrigin
^^^^^^^^^^

.. java:method:: public void hideOrigin(String name) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.hideOrigin(java.lang.String)`

hideState
^^^^^^^^^

.. java:method:: public void hideState(String name)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.hideState(java.lang.String)`

hideTermination
^^^^^^^^^^^^^^^

.. java:method:: public void hideTermination(String name)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.hideTermination(java.lang.String)`

killNeurons
^^^^^^^^^^^

.. java:method:: public void killNeurons(float killrate)
   :outertype: NetworkImpl

   Kills a certain percentage of neurons in the network (recursively including subnetworks).

   :param killrate: the percentage (0.0 to 1.0) of neurons to kill

killNeurons
^^^^^^^^^^^

.. java:method:: public void killNeurons(float killrate, boolean saveRelays)
   :outertype: NetworkImpl

   Kills a certain percentage of neurons in the network (recursively including subnetworks).

   :param killrate: the percentage (0.0 to 1.0) of neurons to kill
   :param saveRelays: if true, exempt populations with only one node from the slaughter

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

removeNode
^^^^^^^^^^

.. java:method:: public void removeNode(String name) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.removeNode(java.lang.String)`

removeProjection
^^^^^^^^^^^^^^^^

.. java:method:: public void removeProjection(Termination termination) throws StructuralException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.removeProjection(ca.nengo.model.Termination)`

removeStepListener
^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeStepListener(StepListener listener)
   :outertype: NetworkImpl

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

run
^^^

.. java:method:: public void run(float startTime, float endTime, boolean topLevel) throws SimulationException
   :outertype: NetworkImpl

   Runs the model with the optional parameter topLevel.

   :param startTime: simulation time at which running starts (s)
   :param endTime: simulation time at which running ends (s)
   :param topLevel: true if the network being run is the top level network, false if it is a subnetwork
   :throws SimulationException: if there's an error in the simulation

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setMetaData
^^^^^^^^^^^

.. java:method:: public void setMetaData(String key, Object value)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Network.setMetaData(java.lang.String,java.lang.Object)`

setMetadata
^^^^^^^^^^^

.. java:method:: public void setMetadata(String key, Object value)
   :outertype: NetworkImpl

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.model.Node.setMode(ca.nengo.model.SimulationMode)`

setMyMode
^^^^^^^^^

.. java:method:: protected void setMyMode(SimulationMode mode)
   :outertype: NetworkImpl

   Used to just change the mode of this network (without recursively changing the mode of nodes in the network)

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: NetworkImpl

   :param name: New name of Network (must be unique within any networks of which this one will be a part)

setSimulator
^^^^^^^^^^^^

.. java:method:: public void setSimulator(Simulator simulator)
   :outertype: NetworkImpl

   :param simulator: Simulator with which to run this Network

setStepSize
^^^^^^^^^^^

.. java:method:: public void setStepSize(float stepSize)
   :outertype: NetworkImpl

   :param stepSize: New timestep size at which to simulate Network (some components of the network may run with different step sizes, but information is exchanged between components with this step size). Defaults to 0.001s.

setTasks
^^^^^^^^

.. java:method:: public void setTasks(ThreadTask[] tasks)
   :outertype: NetworkImpl

   **See also:** :java:ref:`ca.nengo.util.impl.TaskSpawner.setTasks()`

setTime
^^^^^^^

.. java:method:: public void setTime(float time)
   :outertype: NetworkImpl

   :param time: The current simulation time. Sets the current time on the Network's subnodes. (Mainly for NEFEnsembles).

setUseGPU
^^^^^^^^^

.. java:method:: public void setUseGPU(boolean use)
   :outertype: NetworkImpl

   :param use: Use GPU?

toPostScript
^^^^^^^^^^^^

.. java:method:: @SuppressWarnings public String toPostScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: NetworkImpl

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: NetworkImpl

