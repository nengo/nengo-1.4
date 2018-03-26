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

LinearSynapticIntegrator.Factory
================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class Factory implements SynapticIntegratorFactory
   :outertype: LinearSynapticIntegrator

   Factory for making LinearSynapticIntegrators

Constructors
------------
Factory
^^^^^^^

.. java:constructor:: public Factory()
   :outertype: LinearSynapticIntegrator.Factory

   Set defaults

Methods
-------
getMaxTimeStep
^^^^^^^^^^^^^^

.. java:method:: public float getMaxTimeStep()
   :outertype: LinearSynapticIntegrator.Factory

   :return: Maximum time step taken by the synaptic integrators produced here, regardless of network time step

getUnits
^^^^^^^^

.. java:method:: public Units getUnits()
   :outertype: LinearSynapticIntegrator.Factory

   :return: Units of output current value

make
^^^^

.. java:method:: public SynapticIntegrator make()
   :outertype: LinearSynapticIntegrator.Factory

   **See also:** :java:ref:`ca.nengo.model.neuron.impl.SynapticIntegratorFactory.make()`

setMaxTimeStep
^^^^^^^^^^^^^^

.. java:method:: public void setMaxTimeStep(float maxTimeStep)
   :outertype: LinearSynapticIntegrator.Factory

   :param maxTimeStep: Maximum time step taken by the synaptic integrators produced here, regardless of network time step

setUnits
^^^^^^^^

.. java:method:: public void setUnits(Units units)
   :outertype: LinearSynapticIntegrator.Factory

   :param units: Units of output current value

