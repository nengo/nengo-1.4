.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl LinearExponentialTermination

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

GruberNeuronFactory
===================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class GruberNeuronFactory implements NodeFactory

   Creates GruberNeurons

Fields
------
DOPAMINE
^^^^^^^^

.. java:field:: public static final String DOPAMINE
   :outertype: GruberNeuronFactory

   Name of distinguished dopamine termination.

Constructors
------------
GruberNeuronFactory
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public GruberNeuronFactory(PDF scale, PDF bias)
   :outertype: GruberNeuronFactory

   :param scale: PDF to pick neuron scale from
   :param bias: PDF to pick neuron bias from

Methods
-------
getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: GruberNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.getTypeDescription()`

make
^^^^

.. java:method:: public Node make(String name) throws StructuralException
   :outertype: GruberNeuronFactory

