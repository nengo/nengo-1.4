.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl LinearExponentialTermination

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

GruberNeuronFactory.GruberNeuron
================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class GruberNeuron extends ExpandableSpikingNeuron
   :outertype: GruberNeuronFactory

   Class representing the actual neuron

Constructors
------------
GruberNeuron
^^^^^^^^^^^^

.. java:constructor:: public GruberNeuron(SynapticIntegrator integrator, GruberSpikeGenerator generator, float scale, float bias, String name, LinearExponentialTermination dopamineTermination)
   :outertype: GruberNeuronFactory.GruberNeuron

   :param integrator: synaptic integrator
   :param generator: generator object
   :param scale: Neuron gain
   :param bias: Neuron bias
   :param name: Neuron name
   :param dopamineTermination: Termination through which the dopamine signal is transmitted

Methods
-------
run
^^^

.. java:method:: @Override public void run(float startTime, float endTime) throws SimulationException
   :outertype: GruberNeuronFactory.GruberNeuron

