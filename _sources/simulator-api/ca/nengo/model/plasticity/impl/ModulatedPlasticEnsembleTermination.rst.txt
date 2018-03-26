.. java:import:: java.util Arrays

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model StructuralException

ModulatedPlasticEnsembleTermination
===================================

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public abstract class ModulatedPlasticEnsembleTermination extends PlasticEnsembleTermination

   A Termination that is composed of Terminations onto multiple Nodes. The dimensions of the Terminations onto each Node must be the same.

   Physiologically, this might correspond to a set of n axons passing into a neuron pool. Each neuron in the pool receives synaptic connections from as many as n of these axons (zero weight is equivalent to no connection). Sometimes we deal with this set of axons only in terms of the branches they send to one specific Neuron (a Node-level Termination) but here we deal with all branches (an Ensemble-level Termination). In either case the spikes transmitted by the axons are the same.

   TODO: test

   :author: Trevor Bekolay, Jonathan Lai

Fields
------
myFilteredModInput
^^^^^^^^^^^^^^^^^^

.. java:field:: protected float[] myFilteredModInput
   :outertype: ModulatedPlasticEnsembleTermination

myModInput
^^^^^^^^^^

.. java:field:: protected float[] myModInput
   :outertype: ModulatedPlasticEnsembleTermination

myModTermName
^^^^^^^^^^^^^

.. java:field:: protected String myModTermName
   :outertype: ModulatedPlasticEnsembleTermination

Constructors
------------
ModulatedPlasticEnsembleTermination
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ModulatedPlasticEnsembleTermination(Node node, String name, PlasticNodeTermination[] nodeTerminations) throws StructuralException
   :outertype: ModulatedPlasticEnsembleTermination

   :param node: The parent Node
   :param name: Name of this Termination
   :param nodeTerminations: Node-level Terminations that make up this Termination. Must be all LinearExponentialTerminations
   :throws StructuralException: If dimensions of different terminations are not all the same

Methods
-------
clone
^^^^^

.. java:method:: @Override public ModulatedPlasticEnsembleTermination clone(Node node) throws CloneNotSupportedException
   :outertype: ModulatedPlasticEnsembleTermination

getModTermName
^^^^^^^^^^^^^^

.. java:method:: public String getModTermName()
   :outertype: ModulatedPlasticEnsembleTermination

   :return: Name of the Termination from which modulatory input is drawn (can be null if not used)

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: ModulatedPlasticEnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setModTermName
^^^^^^^^^^^^^^

.. java:method:: public void setModTermName(String name)
   :outertype: ModulatedPlasticEnsembleTermination

   :param name: Name of the Termination from which modulatory input is drawn (can be null if not used)

setModTerminationState
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setModTerminationState(String name, InstantaneousOutput state, float time) throws StructuralException
   :outertype: ModulatedPlasticEnsembleTermination

   :param name: Name of the termination from which modulatory input is drawn
   :param state: The state to set
   :param time: Current time
   :throws StructuralException: if modulatory termination does not exist

