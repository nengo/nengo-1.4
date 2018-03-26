.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.impl LinearExponentialTermination

.. java:import:: ca.nengo.model.neuron Neuron

STDPTermination
===============

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public class STDPTermination extends PlasticEnsembleTermination

   A PlasticTermination implementing a PlasticityRule that accepts spiking input.

   Spiking input must be dealt with in order to run learning rules in a spiking SimulationMode. Spiking input is also the only way to simulate spike-timing-dependent plasticity.

   :author: Bryan Tripp, Jonathan Lai

Constructors
------------
STDPTermination
^^^^^^^^^^^^^^^

.. java:constructor:: public STDPTermination(Node node, String name, LinearExponentialTermination[] nodeTerminations) throws StructuralException
   :outertype: STDPTermination

   :param node: The parent Node
   :param name: Name of this Termination
   :param nodeTerminations: Node-level Terminations that make up this Termination. Must be all LinearExponentialTerminations
   :throws StructuralException: If dimensions of different terminations are not all the same

Methods
-------
clone
^^^^^

.. java:method:: @Override public PlasticEnsembleTermination clone() throws CloneNotSupportedException
   :outertype: STDPTermination

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: STDPTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setOriginState
^^^^^^^^^^^^^^

.. java:method:: @Override public void setOriginState(String name, InstantaneousOutput state, float time) throws StructuralException
   :outertype: STDPTermination

updateTransform
^^^^^^^^^^^^^^^

.. java:method:: public void updateTransform(float time, int start, int end) throws StructuralException
   :outertype: STDPTermination

