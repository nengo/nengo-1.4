.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.util MU

hPESTermination
===============

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public class hPESTermination extends PESTermination

   A termination whose transformation evolves according to the PES rule. The learning rate is defined by an AbstractRealLearningFunction (see its declaration for the inputs it receives). This learning rate function is applied to each In each case, the presynaptic-variable input to the function is the corresponding dimension of input to the Termination. The postsynaptic variable is taken as the corresponding dimension of the Origin NEFEnsemble.X. This implementation supports only a single separate modulatory variable, though it can be multi-dimensional. This is also user-defined, as some other Termination onto the same NEFEnsemble. TODO: test

   :author: Bryan Tripp, Jonathan Lai, Trevor Bekolay

Constructors
------------
hPESTermination
^^^^^^^^^^^^^^^

.. java:constructor:: public hPESTermination(NEFEnsemble ensemble, String name, PlasticNodeTermination[] nodeTerminations, float[] initialTheta) throws StructuralException
   :outertype: hPESTermination

   :param ensemble: The ensemble this termination belongs to
   :param name: Name of this Termination
   :param nodeTerminations: Node-level Terminations that make up this Termination. Must be all LinearExponentialTerminations
   :throws StructuralException: If dimensions of different terminations are not all the same

Methods
-------
clone
^^^^^

.. java:method:: @Override public hPESTermination clone(Node node) throws CloneNotSupportedException
   :outertype: hPESTermination

deltaOmega
^^^^^^^^^^

.. java:method:: protected float[][] deltaOmega(int start, int end)
   :outertype: hPESTermination

getSupervisionRatio
^^^^^^^^^^^^^^^^^^^

.. java:method:: public float getSupervisionRatio()
   :outertype: hPESTermination

   :return: How heavily weighted towards supervision

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: hPESTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setSupervisionRatio
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setSupervisionRatio(float supervisionRatio)
   :outertype: hPESTermination

   :param mySupervisionRatio: How heavily weighted towards supervision; between 0.0 (all unsupervised) and 1.0 (all supervised).

updateTransform
^^^^^^^^^^^^^^^

.. java:method:: @Override public void updateTransform(float time, int start, int end) throws StructuralException
   :outertype: hPESTermination

   **See also:** :java:ref:`ca.nengo.model.plasticity.impl.PlasticEnsembleTermination.updateTransform(float,int,int)`

