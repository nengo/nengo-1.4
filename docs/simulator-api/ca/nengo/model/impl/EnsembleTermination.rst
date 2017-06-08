.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

EnsembleTermination
===================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class EnsembleTermination implements Termination

   A Termination that is composed of Terminations onto multiple Nodes. The dimensions of the Terminations onto each Node must be the same.

   Physiologically, this might correspond to a set of n axons passing into a neuron pool. Each neuron in the pool receives synaptic connections from as many as n of these axons (zero weight is equivalent to no connection). Sometimes we deal with this set of axons only in terms of the branches they send to one specific Neuron (a Node-level Termination) but here we deal with all branches (an Ensemble-level Termination). In either case the spikes transmitted by the axons are the same.

   TODO: test

   :author: Bryan Tripp

Constructors
------------
EnsembleTermination
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public EnsembleTermination(Node node, String name, Termination[] nodeTerminations) throws StructuralException
   :outertype: EnsembleTermination

   :param node: The parent Node
   :param name: Name of this Termination
   :param nodeTerminations: Node-level Terminations that make up this Termination
   :throws StructuralException: If dimensions of different terminations are not all the same

Methods
-------
clone
^^^^^

.. java:method:: @Override public EnsembleTermination clone() throws CloneNotSupportedException
   :outertype: EnsembleTermination

clone
^^^^^

.. java:method:: public EnsembleTermination clone(Node node) throws CloneNotSupportedException
   :outertype: EnsembleTermination

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: EnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getDimensions()`

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: EnsembleTermination

   :return: Latest input to the underlying terminations.

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: EnsembleTermination

   Returns true if more than half of node terminations are modulatory.

   **See also:** :java:ref:`ca.nengo.model.Termination.getModulatory()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: EnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: EnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getNode()`

getNodeTerminations
^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getNodeTerminations()
   :outertype: EnsembleTermination

   :return: Array with all of the underlying node terminations

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: EnsembleTermination

   Returns the average.

   **See also:** :java:ref:`ca.nengo.model.Termination.getTau()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: EnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: EnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setModulatory(boolean)`

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: EnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setTau(float)`

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: EnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setValues(ca.nengo.model.InstantaneousOutput)`

