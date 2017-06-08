.. java:import:: ca.nengo.util SpikePattern

Ensemble
========

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface Ensemble extends Node

   A group of Nodes with largely overlapping inputs and outputs.

   There are no strict rules for how to group Nodes into Ensembles, but here are some things to consider:

   ..

   * A group of Nodes that together 'represent' something through a population code should be modelled as an Ensemble. (Also consider using NEFEnsemble to make such representation explicit.)
   * Making ensembles that correspond to physical structures (e.g. nuclei) and naming them appropriately will make the model clearer.
   * Outputs from an Ensemble are grouped together and passed to other Ensembles during a simulation, and practical issues may arise from this. For example, putting all your Nodes in a single large ensemble could result in a very large matrix of synaptic weights, which would impair performance.

   The membership of an Ensemble is fixed once the Ensemble is created. This means that the Ensemble model doesn't deal explicitly with growth and death of components during simulation (although you can set input/output weights to zero to mimic this). It also means that an Ensemble isn't a good model of a functional "assembly".

   :author: Bryan Tripp

Methods
-------
collectSpikes
^^^^^^^^^^^^^

.. java:method:: public void collectSpikes(boolean collect)
   :outertype: Ensemble

   :param collect: If true, the spike pattern is recorded in subsequent runs and is available through getSpikePattern() (defaults to false)

getNodes
^^^^^^^^

.. java:method:: public Node[] getNodes()
   :outertype: Ensemble

   :return: Nodes that make up the Ensemble

getSpikePattern
^^^^^^^^^^^^^^^

.. java:method:: public SpikePattern getSpikePattern()
   :outertype: Ensemble

   This method provides a means of efficiently storing the output of an Ensemble if the component Nodes have Origins that produce SpikeOutput.

   :return: A SpikePattern containing a record of spikes, provided collectSpikes(boolean) has been set to true

isCollectingSpikes
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isCollectingSpikes()
   :outertype: Ensemble

   :return: true if the spike pattern will be recorded in subsequent runs

redefineNodes
^^^^^^^^^^^^^

.. java:method:: public void redefineNodes(Node[] nodes)
   :outertype: Ensemble

   Replaces the set of nodes inside the Ensemble

   :param nodes: New nodes to use

stopProbing
^^^^^^^^^^^

.. java:method:: public void stopProbing(String stateName)
   :outertype: Ensemble

