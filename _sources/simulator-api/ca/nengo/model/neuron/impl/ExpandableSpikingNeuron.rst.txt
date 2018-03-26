.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model ExpandableNode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.neuron ExpandableSynapticIntegrator

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

ExpandableSpikingNeuron
=======================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class ExpandableSpikingNeuron extends SpikingNeuron implements ExpandableNode

   A SpikingNeuron with an ExpandableSynapticIntegrator.

   :author: Bryan Tripp

Constructors
------------
ExpandableSpikingNeuron
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ExpandableSpikingNeuron(SynapticIntegrator integrator, SpikeGenerator generator, float scale, float bias, String name)
   :outertype: ExpandableSpikingNeuron

   Note: current = scale * (weighted sum of inputs at each termination) * (radial input) + bias.

   :param integrator: SynapticIntegrator used to model dendritic/somatic integration of inputs to this Neuron \ **(must be Plastic)**\
   :param generator: SpikeGenerator used to model spike generation at the axon hillock of this Neuron
   :param scale: A coefficient that scales summed input
   :param bias: A bias current that models unaccounted-for inputs and/or intrinsic currents
   :param name: A unique name for this neuron in the context of the Network or Ensemble to which it belongs

Methods
-------
addDelayedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addDelayedTermination(String name, float[][] weights, float tauPSC, float delay, boolean modulatory) throws StructuralException
   :outertype: ExpandableSpikingNeuron

addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: ExpandableSpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.addTermination(java.lang.String,float[][],float,boolean)`

clone
^^^^^

.. java:method:: public ExpandableSpikingNeuron clone() throws CloneNotSupportedException
   :outertype: ExpandableSpikingNeuron

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: ExpandableSpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.getDimension()`

getSynapticIntegrator
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public ExpandableSynapticIntegrator getSynapticIntegrator()
   :outertype: ExpandableSpikingNeuron

   :return: SynapticIntegrator for this neuron

removeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public Termination removeTermination(String name) throws StructuralException
   :outertype: ExpandableSpikingNeuron

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.removeTermination(java.lang.String)`

