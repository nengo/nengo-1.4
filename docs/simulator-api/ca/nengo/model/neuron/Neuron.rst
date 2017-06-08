.. java:import:: ca.nengo.model Node

Neuron
======

.. java:package:: ca.nengo.model.neuron
   :noindex:

.. java:type:: public interface Neuron extends Node

   A model of a single neuron cell. Neurons are Nodes which can have multiple Terminations, and multiple Origins (corresponding to axonal output and possibly other outpus such as gap junctions), but they always have a primary Origin which is named Neuron.AXON.

   :author: Bryan Tripp

Fields
------
AXON
^^^^

.. java:field:: public static final String AXON
   :outertype: Neuron

   Standard name for the primary Origin of a Neuron, which outputs its spikes or firing rate depending on SimulationMode.

