.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.model ExpandableNode

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

SpikingNeuronFactory
====================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class SpikingNeuronFactory implements NodeFactory

   Creates spiking neurons by delegating to a SynapticIntegratorFactory and a SpikeGeneratorFactory.

   :author: Bryan Tripp

Constructors
------------
SpikingNeuronFactory
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SpikingNeuronFactory(SynapticIntegratorFactory intFact, SpikeGeneratorFactory genFact, PDF scale, PDF bias)
   :outertype: SpikingNeuronFactory

   :param intFact: Synaptic integrator factory
   :param genFact: Spike generator factory
   :param scale: PDF for neuron gain
   :param bias: PDF for bias current

Methods
-------
getGeneratorFactory
^^^^^^^^^^^^^^^^^^^

.. java:method:: public SpikeGeneratorFactory getGeneratorFactory()
   :outertype: SpikingNeuronFactory

   :return: spike generator factory

getIntegratorFactory
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public SynapticIntegratorFactory getIntegratorFactory()
   :outertype: SpikingNeuronFactory

   :return: integrator factory

getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: SpikingNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.getTypeDescription()`

make
^^^^

.. java:method:: public Node make(String name) throws StructuralException
   :outertype: SpikingNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.make(java.lang.String)`

setGeneratorFactory
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setGeneratorFactory(SpikeGeneratorFactory factory)
   :outertype: SpikingNeuronFactory

   :param factory: spike generator factory

setIntegratorFactory
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setIntegratorFactory(SynapticIntegratorFactory factory)
   :outertype: SpikingNeuronFactory

   :param factory: integrator factory

