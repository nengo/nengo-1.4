.. java:import:: java.util ArrayList

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model PreciseSpikeOutput

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl SpikePatternImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

AbstractEnsemble
================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public abstract class AbstractEnsemble implements Ensemble, Probeable, VisiblyMutable

   Abstract class that can be used as a basis for Ensemble implementations.

   :author: Bryan Tripp

Constructors
------------
AbstractEnsemble
^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractEnsemble(String name, Node[] nodes)
   :outertype: AbstractEnsemble

   Note that setMode(SimulationMode.DEFAULT) is called at construction time.

   :param name: Unique name of Ensemble
   :param nodes: Nodes that Ensemble contains

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public Ensemble clone() throws CloneNotSupportedException
   :outertype: AbstractEnsemble

collectSpikes
^^^^^^^^^^^^^

.. java:method:: public void collectSpikes(boolean collect)
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.collectSpikes(boolean)`

findCommon1DOrigins
^^^^^^^^^^^^^^^^^^^

.. java:method:: public static List<String> findCommon1DOrigins(Node[] nodes)
   :outertype: AbstractEnsemble

   :param nodes: A list of Nodes
   :return: Names of one-dimensional origins that are shared by all the nodes

fireVisibleChangeEvent
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void fireVisibleChangeEvent()
   :outertype: AbstractEnsemble

   Called by subclasses when properties have changed in such a way that the display of the ensemble may need updating.

getCollectSpikesRatio
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getCollectSpikesRatio()
   :outertype: AbstractEnsemble

   :return: Inverse of the proportion of nodes from which to collect spikes

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: AbstractEnsemble

   :return: Composite of Node states by given name. States of different nodes may be defined at different times, so only the states at the end of the most recent step are given. Only the first dimension of each Node state is included in the composite.

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getMetadata
^^^^^^^^^^^

.. java:method:: public Object getMetadata(String key)
   :outertype: AbstractEnsemble

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: AbstractEnsemble

   Note that this reflects the latest mode requested of the Ensemble, and that individual Neurons may run in different modes (see setMode).

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getName()`

getNodes
^^^^^^^^

.. java:method:: public Node[] getNodes()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getNodes()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getSpikePattern
^^^^^^^^^^^^^^^

.. java:method:: public SpikePattern getSpikePattern()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getSpikePattern()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getTerminations()`

isCollectingSpikes
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isCollectingSpikes()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Ensemble.isCollectingSpikes()`

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

redefineNodes
^^^^^^^^^^^^^

.. java:method:: public void redefineNodes(Node[] nodes)
   :outertype: AbstractEnsemble

   Replaces the set of nodes inside the Ensemble

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

removeOrigin
^^^^^^^^^^^^

.. java:method:: public synchronized Origin removeOrigin(String name) throws StructuralException
   :outertype: AbstractEnsemble

   :param name: Name of the Origin to remove from the ensemble
   :throws StructuralException: if named Origin does not exist
   :return: the removed Origin object

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.removeTermination(java.lang.String)`

removeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public synchronized Termination removeTermination(String name) throws StructuralException
   :outertype: AbstractEnsemble

   :param name: Name of the Termination to remove from the ensemble
   :throws StructuralException: if named Termination does not exist
   :return: the removed Termination object

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.removeTermination(java.lang.String)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: AbstractEnsemble

   Resets each Node in this Ensemble.

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: AbstractEnsemble

   Runs each neuron in the Ensemble.

   **See also:** :java:ref:`ca.nengo.model.Ensemble.run(float,float)`

setCollectSpikesRatio
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setCollectSpikesRatio(int n)
   :outertype: AbstractEnsemble

   :param n: Inverse of the proportion of nodes from which to collect spikes

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: AbstractEnsemble

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setMetadata
^^^^^^^^^^^

.. java:method:: public void setMetadata(String key, Object value)
   :outertype: AbstractEnsemble

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: AbstractEnsemble

   When this method is called, setMode(...) is called on each Node in the Ensemble. Each Node will then run in the mode that is closest to the requested mode (this could be different for different Node). Note that at Ensemble construction time, setMode(SimulationMode.DEFAULT) is called.

   **See also:** :java:ref:`ca.nengo.model.Ensemble.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: AbstractEnsemble

   :param name: The new name

setSpikePattern
^^^^^^^^^^^^^^^

.. java:method:: public void setSpikePattern(float[] spikes, float endTime)
   :outertype: AbstractEnsemble

   :param spikes: The pattern of spikes (0.0f for not spiking, else? for spiking)
   :param endTime: End time for the spike pattern

stopProbing
^^^^^^^^^^^

.. java:method:: public void stopProbing(String stateName)
   :outertype: AbstractEnsemble

