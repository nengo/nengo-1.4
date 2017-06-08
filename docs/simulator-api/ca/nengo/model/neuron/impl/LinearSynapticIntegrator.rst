.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util Map

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl DelayedLinearExponentialTermination

.. java:import:: ca.nengo.model.impl LinearExponentialTermination

.. java:import:: ca.nengo.model.neuron ExpandableSynapticIntegrator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

LinearSynapticIntegrator
========================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class LinearSynapticIntegrator implements ExpandableSynapticIntegrator

   A basic linear \ ``SynapticIntegrator``\  model.

   Synaptic inputs are individually weighted, passed through decaying exponential dynamics, and summed.

   A synaptic weight corresponds to the time integral of the current induced by one spike, or to the time integral of current induced by a real-valued input of 1 over 1 second. Thus a real-valued firing-rate input has roughly the same effect as a series spikes at the same rate. So a simulation can switch between spike and rate inputs, with minimal impact and without the need to modify synaptic weights.

   :author: Bryan Tripp

Constructors
------------
LinearSynapticIntegrator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public LinearSynapticIntegrator(float maxTimeStep, Units currentUnits)
   :outertype: LinearSynapticIntegrator

   :param maxTimeStep: Maximum length of integration time step. Shorter steps may be used to better match length of run(...)
   :param currentUnits: Units of current in input weights, scale, bias, and result of run(...)

LinearSynapticIntegrator
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public LinearSynapticIntegrator()
   :outertype: LinearSynapticIntegrator

   Defaults to max timestep 1ms and units Units.ACU.

Methods
-------
addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: LinearSynapticIntegrator

   **See also:** :java:ref:`ca.nengo.model.neuron.ExpandableSynapticIntegrator.addTermination(java.lang.String,float[],float,boolean)`

addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[] weights, float tauPSC, float delay, boolean modulatory) throws StructuralException
   :outertype: LinearSynapticIntegrator

clone
^^^^^

.. java:method:: @Override public LinearSynapticIntegrator clone() throws CloneNotSupportedException
   :outertype: LinearSynapticIntegrator

getCurrentUnits
^^^^^^^^^^^^^^^

.. java:method:: public Units getCurrentUnits()
   :outertype: LinearSynapticIntegrator

   :return: Units that current is expressed in

getMaxTimeStep
^^^^^^^^^^^^^^

.. java:method:: public float getMaxTimeStep()
   :outertype: LinearSynapticIntegrator

   :return: maximum time step

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: LinearSynapticIntegrator

   **See also:** :java:ref:`ca.nengo.model.neuron.SynapticIntegrator.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: LinearSynapticIntegrator

   **See also:** :java:ref:`ca.nengo.model.neuron.SynapticIntegrator.getTerminations()`

removeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public Termination removeTermination(String name) throws StructuralException
   :outertype: LinearSynapticIntegrator

   **See also:** :java:ref:`ca.nengo.model.neuron.ExpandableSynapticIntegrator.removeTermination(java.lang.String)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: LinearSynapticIntegrator

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public TimeSeries1D run(float startTime, float endTime)
   :outertype: LinearSynapticIntegrator

   **See also:** :java:ref:`ca.nengo.model.neuron.SynapticIntegrator.run(float,float)`

setCurrentUnits
^^^^^^^^^^^^^^^

.. java:method:: public void setCurrentUnits(Units units)
   :outertype: LinearSynapticIntegrator

   :param units: Units that current should be expressed in

setMaxTimeStep
^^^^^^^^^^^^^^

.. java:method:: public void setMaxTimeStep(float maxTimeStep)
   :outertype: LinearSynapticIntegrator

   :param maxTimeStep: maximum time step

setNode
^^^^^^^

.. java:method:: public void setNode(Node node)
   :outertype: LinearSynapticIntegrator

   :param node: The parent node (Terminations need a reference to this)

