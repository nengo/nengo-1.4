.. java:import:: ca.nengo.dynamics.impl AbstractDynamicalSystem

.. java:import:: ca.nengo.dynamics.impl RK45Integrator

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron SpikeGenerator

HodgkinHuxleySpikeGenerator.HodgkinHuxleyNeuronFactory
======================================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class HodgkinHuxleyNeuronFactory implements NodeFactory
   :outertype: HodgkinHuxleySpikeGenerator

   A factory of neurons with linear synaptic integration and Hodgkin-Huxley spike generation.

   :author: Bryan Tripp

Methods
-------
getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleyNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.getTypeDescription()`

make
^^^^

.. java:method:: public Node make(String name) throws StructuralException
   :outertype: HodgkinHuxleySpikeGenerator.HodgkinHuxleyNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.make(java.lang.String)`

