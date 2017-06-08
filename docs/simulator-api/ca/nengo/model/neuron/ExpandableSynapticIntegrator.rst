.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

ExpandableSynapticIntegrator
============================

.. java:package:: ca.nengo.model.neuron
   :noindex:

.. java:type:: public interface ExpandableSynapticIntegrator extends SynapticIntegrator

   A SynapticIntegrator to which Terminations can be added after construction, in a standard way. This facilitates circuit building. However, this may not be possible with a sophisticated dendritic model, with which more involved setup is probably needed (e.g. constructing individual synapse models; specifying spatial confuguration of synapses). In this case, the synpases should be defined first, before assembling the circuit, and the SynapticIntegrator might not be expandable in the standard manner defined here.

   :author: Bryan Tripp

Methods
-------
addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: ExpandableSynapticIntegrator

   :param name: Name of Termination
   :param weights: Synaptic weights associated with this Termination
   :param tauPSC: Time constant of post-synaptic current decay (all Terminations have this property but it may have slightly different interpretations depending on the SynapticIntegrator or other properties of the Termination).
   :param modulatory: If true, inputs to the Termination are not summed with other inputs (they only have modulatory effects, eg on plasticity, which must be defined elsewhere).
   :throws StructuralException: if there is already a Termination of the same name on this SynapticIntegrator
   :return: resulting Termination

clone
^^^^^

.. java:method:: public ExpandableSynapticIntegrator clone() throws CloneNotSupportedException
   :outertype: ExpandableSynapticIntegrator

removeTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public Termination removeTermination(String name) throws StructuralException
   :outertype: ExpandableSynapticIntegrator

   :param name: Name of Termination to remove.
   :throws StructuralException: if there is no Termination of the given name on this SynapticIntegrator
   :return: The removed Termination

