.. java:import:: java.util Arrays

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl EnsembleTermination

.. java:import:: ca.nengo.model.nef NEFEnsemble

PlasticEnsembleTermination
==========================

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public abstract class PlasticEnsembleTermination extends EnsembleTermination

   A Termination that is composed of Terminations onto multiple Nodes. The dimensions of the Terminations onto each Node must be the same.

   Physiologically, this might correspond to a set of n axons passing into a neuron pool. Each neuron in the pool receives synaptic connections from as many as n of these axons (zero weight is equivalent to no connection). Sometimes we deal with this set of axons only in terms of the branches they send to one specific Neuron (a Node-level Termination) but here we deal with all branches (an Ensemble-level Termination). In either case the spikes transmitted by the axons are the same.

   TODO: test

   :author: Trevor Bekolay, Jonathan Lai

Fields
------
myFilteredInput
^^^^^^^^^^^^^^^

.. java:field:: protected float[] myFilteredInput
   :outertype: PlasticEnsembleTermination

myFilteredOutput
^^^^^^^^^^^^^^^^

.. java:field:: protected float[] myFilteredOutput
   :outertype: PlasticEnsembleTermination

myInput
^^^^^^^

.. java:field:: protected float[] myInput
   :outertype: PlasticEnsembleTermination

myLearning
^^^^^^^^^^

.. java:field:: protected boolean myLearning
   :outertype: PlasticEnsembleTermination

myLearningRate
^^^^^^^^^^^^^^

.. java:field:: protected float myLearningRate
   :outertype: PlasticEnsembleTermination

myOriginName
^^^^^^^^^^^^

.. java:field:: protected String myOriginName
   :outertype: PlasticEnsembleTermination

myOutput
^^^^^^^^

.. java:field:: protected float[] myOutput
   :outertype: PlasticEnsembleTermination

Constructors
------------
PlasticEnsembleTermination
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PlasticEnsembleTermination(Node node, String name, PlasticNodeTermination[] nodeTerminations) throws StructuralException
   :outertype: PlasticEnsembleTermination

   :param node: The parent Node
   :param name: Name of this Termination
   :param nodeTerminations: Node-level Terminations that make up this Termination. Must be all LinearExponentialTerminations
   :throws StructuralException: If dimensions of different terminations are not all the same

Methods
-------
clone
^^^^^

.. java:method:: @Override public PlasticEnsembleTermination clone(Node node) throws CloneNotSupportedException
   :outertype: PlasticEnsembleTermination

getFilteredOutput
^^^^^^^^^^^^^^^^^

.. java:method:: public float[] getFilteredOutput()
   :outertype: PlasticEnsembleTermination

   :return: Filtered output

getInput
^^^^^^^^

.. java:method:: @Override public InstantaneousOutput getInput()
   :outertype: PlasticEnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.impl.EnsembleTermination.getInput()`

getLearning
^^^^^^^^^^^

.. java:method:: public boolean getLearning()
   :outertype: PlasticEnsembleTermination

   :return: Whether or not the termination is currently learning

getLearningRate
^^^^^^^^^^^^^^^

.. java:method:: public float getLearningRate()
   :outertype: PlasticEnsembleTermination

   :return: Learning rate of the termination

getOriginName
^^^^^^^^^^^^^

.. java:method:: public String getOriginName()
   :outertype: PlasticEnsembleTermination

   :return: Name of Origin from which postsynaptic activity is drawn

getOutputs
^^^^^^^^^^

.. java:method:: public float[] getOutputs()
   :outertype: PlasticEnsembleTermination

   :return: The output currents from the PlasticNodeTermination being wrapped

getTransform
^^^^^^^^^^^^

.. java:method:: public float[][] getTransform()
   :outertype: PlasticEnsembleTermination

   :return: The transformation matrix, which is made up of the weight vectors for each of the PlasticNodeTerminations within. This can be thought of as the connection weight matrix in most cases.

modifyTransform
^^^^^^^^^^^^^^^

.. java:method:: public void modifyTransform(float[][] change, boolean save, int start, int end)
   :outertype: PlasticEnsembleTermination

   Modifies the transformation weights in-place.

   :param change: The change in the transformation matrix
   :param save: Whether or not to save the new transformation matrix
   :param start: Row in transformation matrix to start modifications
   :param end: Row in transformation matrix to end modifications

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: PlasticEnsembleTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

saveTransform
^^^^^^^^^^^^^

.. java:method:: public void saveTransform()
   :outertype: PlasticEnsembleTermination

   Saves the weights in the PlasticNodeTerminations within.

setLearning
^^^^^^^^^^^

.. java:method:: public void setLearning(boolean learning)
   :outertype: PlasticEnsembleTermination

   :param learning: Turn learning on or off for this termination

setLearningRate
^^^^^^^^^^^^^^^

.. java:method:: public void setLearningRate(float learningRate)
   :outertype: PlasticEnsembleTermination

   :param learningRate: Learning rate of the termination

setOriginName
^^^^^^^^^^^^^

.. java:method:: public void setOriginName(String originName)
   :outertype: PlasticEnsembleTermination

   :param originName: Name of Origin from which postsynaptic activity is drawn

setOriginState
^^^^^^^^^^^^^^

.. java:method:: public void setOriginState(String name, InstantaneousOutput state, float time) throws StructuralException
   :outertype: PlasticEnsembleTermination

   :param name: Name of Origin from which postsynaptic activity is drawn
   :param state: State of named origin
   :param time: Current time
   :throws StructuralException: if Origin is not set

setTerminationState
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setTerminationState(float time) throws StructuralException
   :outertype: PlasticEnsembleTermination

setTransform
^^^^^^^^^^^^

.. java:method:: public void setTransform(float[][] transform, boolean save)
   :outertype: PlasticEnsembleTermination

   :param transform: The transformation matrix, which can be thought of as the connection weight matrix in most cases. This will be passed through to set the weight vectors on each PlasticNodeTermination within.

updateFiltered
^^^^^^^^^^^^^^

.. java:method:: protected static void updateFiltered(float[] raw, float[] filtered, float tauPSC, float integrationTime)
   :outertype: PlasticEnsembleTermination

updateRaw
^^^^^^^^^

.. java:method:: protected static void updateRaw(float[] raw, InstantaneousOutput state, float integrationTime) throws StructuralException
   :outertype: PlasticEnsembleTermination

updateTransform
^^^^^^^^^^^^^^^

.. java:method:: public abstract void updateTransform(float time, int start, int end) throws StructuralException
   :outertype: PlasticEnsembleTermination

   :param time: Current time
   :param start: The start index of the range of transform values to update (for multithreading)
   :param end: The end index of the range of transform values to update (for multithreading)
   :throws StructuralException: if

