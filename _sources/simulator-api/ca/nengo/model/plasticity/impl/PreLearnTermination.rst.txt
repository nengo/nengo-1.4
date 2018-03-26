.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

PreLearnTermination
===================

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public class PreLearnTermination extends ModulatedPlasticEnsembleTermination

   A termination that learns only on presynaptic spikes.

   :author: Trevor Bekolay

Constructors
------------
PreLearnTermination
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PreLearnTermination(NEFEnsemble ensemble, String name, PlasticNodeTermination[] nodeTerminations) throws StructuralException
   :outertype: PreLearnTermination

   :param ensemble: The ensemble this termination belongs to
   :param name: Name of this Termination
   :param nodeTerminations: Node-level Terminations that make up this Termination. Must be all LinearExponentialTerminations
   :throws StructuralException: If dimensions of different terminations are not all the same

Methods
-------
clone
^^^^^

.. java:method:: @Override public PreLearnTermination clone() throws CloneNotSupportedException
   :outertype: PreLearnTermination

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: PreLearnTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

updateTransform
^^^^^^^^^^^^^^^

.. java:method:: @Override public void updateTransform(float time, int start, int end) throws StructuralException
   :outertype: PreLearnTermination

   **See also:** :java:ref:`ca.nengo.model.plasticity.impl.PlasticEnsembleTermination.updateTransform(float,int,int)`

