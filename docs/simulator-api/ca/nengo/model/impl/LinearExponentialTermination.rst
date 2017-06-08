.. java:import:: java.util Random

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model PreciseSpikeOutput

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

LinearExponentialTermination
============================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class LinearExponentialTermination implements PlasticNodeTermination

   A Termination at which incoming spikes induce exponentially decaying post-synaptic currents that are combined linearly. Real-valued spike rate inputs have approximately the same effect over time as actual (boolean) spike inputs at the same rate.

   Each input is weighted (weights specified in the constructor) so that the time integral of the post-synaptic current arising from one spike equals the weight. The time integral of post-synaptic current arising from real-valued input of 1 over a period of 1s also equals the weight. This means that spike input and spike-rate input have roughly the same effects.

   :author: Bryan Tripp

Constructors
------------
LinearExponentialTermination
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public LinearExponentialTermination(Node node, String name, float[] weights, float tauPSC)
   :outertype: LinearExponentialTermination

   :param node: The parent Node
   :param name: Name of the Termination (must be unique within the Neuron or Ensemble to which it is attached)
   :param weights: Ordered list of synaptic weights of each input channel
   :param tauPSC: Time constant of exponential post-synaptic current decay

Methods
-------
clone
^^^^^

.. java:method:: @Override public LinearExponentialTermination clone() throws CloneNotSupportedException
   :outertype: LinearExponentialTermination

clone
^^^^^

.. java:method:: public LinearExponentialTermination clone(Node node) throws CloneNotSupportedException
   :outertype: LinearExponentialTermination

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: LinearExponentialTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getDimensions()`

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: LinearExponentialTermination

   :return: The most recent input to the Termination

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: LinearExponentialTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getModulatory()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: LinearExponentialTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: LinearExponentialTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getNode()`

getOutput
^^^^^^^^^

.. java:method:: public float getOutput()
   :outertype: LinearExponentialTermination

   :return: The most recent output of the Termination (after summation and dynamics)

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: LinearExponentialTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getTau()`

getWeightProbabilities
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public float[] getWeightProbabilities()
   :outertype: LinearExponentialTermination

   :return: List of synaptic release probabilities for each input channel

getWeights
^^^^^^^^^^

.. java:method:: public float[] getWeights()
   :outertype: LinearExponentialTermination

   :return: List of synaptic weights for each input channel

modifyWeights
^^^^^^^^^^^^^

.. java:method:: public void modifyWeights(float[] change, boolean save)
   :outertype: LinearExponentialTermination

   This modifies the weights in-place, rather than creating new ones, so will usually be faster than calling setWeights.

   :param change: The change in the synaptic weights for each input channel

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: LinearExponentialTermination

   Resets current to 0 (randomize arg is ignored).

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

saveWeights
^^^^^^^^^^^

.. java:method:: public void saveWeights()
   :outertype: LinearExponentialTermination

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: LinearExponentialTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setModulatory(boolean)`

setNode
^^^^^^^

.. java:method:: public void setNode(Node node)
   :outertype: LinearExponentialTermination

   :param node: Parent node

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: LinearExponentialTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setTau(float)`

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: LinearExponentialTermination

   :param values: Can be either SpikeOutput or RealOutput

   **See also:** :java:ref:`ca.nengo.model.Termination.setValues(ca.nengo.model.InstantaneousOutput)`

setWeightProbabilities
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setWeightProbabilities(float[] probs)
   :outertype: LinearExponentialTermination

   :param probs: The new synaptic vesicle release probabilities for each input channel

setWeights
^^^^^^^^^^

.. java:method:: public void setWeights(float[] weights, boolean save)
   :outertype: LinearExponentialTermination

   :param weights: The new synaptic weights for each input channel

updateCurrent
^^^^^^^^^^^^^

.. java:method:: public float updateCurrent(boolean applySpikes, float integrationTime, float decayTime)
   :outertype: LinearExponentialTermination

   Updates net post-synaptic current for this Termination according to new inputs and exponential dynamics applied to previous inputs. The arguments provide flexibility in updating the current, in terms of whether spike inputs are applied, for how long real-valued inputs are applied, and for how long the net current decays exponentially. A usage example follows: Suppose the SynapticIntegrator that contains this Termination models each network time step in three steps of its own. Suppose also that the SynapticIntegrator uses updateCurrent() to find the current at the beginning and end of each network time step, and at the two points in between. A reasonable way for the SynapticIntegrator to use updateCurrent() in this scenario would be as follows (the variable tau represents 1/3 of the length of the network time step):

   ..

   #. At the beginning of the network time step call updateCurrent(true, tau, 0) to model the application of spikes and real-valued inputs from the previous time step, without decaying them.
   #. To advance to each of the two intermediate times call updateCurrent(false, tau, tau). Spikes are not re-applied (a given spike should only be applied once). Real-valued inputs are continuous in time, so they are integrated again. Currents also begin to decay.
   #. At the end of the network time step call updateCurrent(false, 0, tau). Real-valued inputs for this time interval are not applied at the end of this network time step, since they will be applied at the (identical) beginning of the next network time step.

   :param applySpikes: True if spike inputs are to be applied
   :param integrationTime: Time over which real-valued inputs are to be integrated
   :param decayTime: Time over which post-synaptic currents are to decay
   :return: Net synaptic current flowing into this termination after specified input and decay

