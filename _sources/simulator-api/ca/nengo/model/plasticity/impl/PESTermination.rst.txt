.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: ca.nengo.util MU

PESTermination
==============

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public class PESTermination extends ModulatedPlasticEnsembleTermination

   A termination whose transformation evolves according to the PES rule. The learning rate is defined by an AbstractRealLearningFunction (see its declaration for the inputs it receives). This learning rate function is applied to each In each case, the presynaptic-variable input to the function is the corresponding dimension of input to the Termination. The postsynaptic variable is taken as the corresponding dimension of the Origin NEFEnsemble.X. This implementation supports only a single separate modulatory variable, though it can be multi-dimensional. This is also user-defined, as some other Termination onto the same NEFEnsemble. TODO: test

   :author: Bryan Tripp, Jonathan Lai, Trevor Bekolay

Fields
------
myGain
^^^^^^

.. java:field:: protected float[] myGain
   :outertype: PESTermination

Constructors
------------
PESTermination
^^^^^^^^^^^^^^

.. java:constructor:: public PESTermination(NEFEnsemble ensemble, String name, PlasticNodeTermination[] nodeTerminations) throws StructuralException
   :outertype: PESTermination

   :param ensemble: The ensemble this termination belongs to
   :param name: Name of this Termination
   :param nodeTerminations: Node-level Terminations that make up this Termination. Must be all LinearExponentialTerminations
   :throws StructuralException: If dimensions of different terminations are not all the same

Methods
-------
clone
^^^^^

.. java:method:: @Override public PESTermination clone(Node node) throws CloneNotSupportedException
   :outertype: PESTermination

deltaOmega
^^^^^^^^^^

.. java:method:: protected float[][] deltaOmega(int start, int end)
   :outertype: PESTermination

getOja
^^^^^^

.. java:method:: public boolean getOja()
   :outertype: PESTermination

   :return: Name of Origin from which post-synaptic activity is drawn

setLearningRate
^^^^^^^^^^^^^^^

.. java:method:: public void setLearningRate(float learningRate)
   :outertype: PESTermination

   :param learningRate: Learning rate of the termination

setOja
^^^^^^

.. java:method:: public void setOja(boolean oja)
   :outertype: PESTermination

   :param oja: Should this termination use Oja smoothing?

updateTransform
^^^^^^^^^^^^^^^

.. java:method:: @Override public void updateTransform(float time, int start, int end) throws StructuralException
   :outertype: PESTermination

   **See also:** :java:ref:`ca.nengo.model.plasticity.impl.PlasticEnsembleTermination.updateTransform(float,int,int)`

