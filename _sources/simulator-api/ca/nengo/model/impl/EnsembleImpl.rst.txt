.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util Map

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model ExpandableNode

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.neuron.impl ExpandableSpikingNeuron

.. java:import:: ca.nengo.util ScriptGenException

EnsembleImpl
============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class EnsembleImpl extends AbstractEnsemble implements ExpandableNode

   Default implementation of Ensemble.

   Origins or Terminations can be set up on Nodes before they are grouped into an Ensemble. After Nodes are added to an Ensemble, no Origins or Terminations should be added to them directly. Terminations can be added with EnsembleImpl.addTermination(...) If a Termination is added directly to a Node after the Node is added to the Ensemble, the Termination will not appear in Ensemble.getTerminations()

   TODO: test

   :author: Bryan Tripp

Fields
------
myExpandableNodes
^^^^^^^^^^^^^^^^^

.. java:field:: protected ExpandableNode[] myExpandableNodes
   :outertype: EnsembleImpl

myExpandedTerminations
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected Map<String, Termination> myExpandedTerminations
   :outertype: EnsembleImpl

Constructors
------------
EnsembleImpl
^^^^^^^^^^^^

.. java:constructor:: public EnsembleImpl(String name, Node[] nodes)
   :outertype: EnsembleImpl

   :param name: Name of Ensemble
   :param nodes: Nodes that make up the Ensemble

EnsembleImpl
^^^^^^^^^^^^

.. java:constructor:: public EnsembleImpl(String name, NodeFactory factory, int n) throws StructuralException
   :outertype: EnsembleImpl

   :param name: Name of Ensemble
   :param factory: Factory class that will create nodes
   :param n: Number of nodes to create
   :throws StructuralException: if any problem halts construction

Methods
-------
addTermination
^^^^^^^^^^^^^^

.. java:method:: public synchronized Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: EnsembleImpl

   :param weights: Each row is used as a 1 by m matrix of weights in a new termination on the nth expandable node

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.addTermination(java.lang.String,float[][],float,boolean)`

addTermination
^^^^^^^^^^^^^^

.. java:method:: public synchronized Termination addTermination(String name, float[][] weights, PDF tauPSC, PDF delays, boolean modulatory) throws StructuralException
   :outertype: EnsembleImpl

   :param weights: Each row is used as a 1 by m matrix of weights in a new termination on the nth expandable node
   :param tauPSC: PDF from which psc time constants will be sampled

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.addTermination(java.lang.String,float[][],float,boolean)`

clone
^^^^^

.. java:method:: @Override public EnsembleImpl clone() throws CloneNotSupportedException
   :outertype: EnsembleImpl

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: EnsembleImpl

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: EnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.getDimension()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: @Override public Termination getTermination(String name) throws StructuralException
   :outertype: EnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: EnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getTerminations()`

removeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public synchronized Termination removeTermination(String name) throws StructuralException
   :outertype: EnsembleImpl

   :throws StructuralException: if Termination does not exist

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.removeTermination(java.lang.String)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: EnsembleImpl

setMode
^^^^^^^

.. java:method:: @Override public void setMode(SimulationMode mode)
   :outertype: EnsembleImpl

   This Ensemble does not support SimulationMode.DIRECT.

   **See also:** :java:ref:`ca.nengo.model.Ensemble.setMode(ca.nengo.model.SimulationMode)`

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: EnsembleImpl

