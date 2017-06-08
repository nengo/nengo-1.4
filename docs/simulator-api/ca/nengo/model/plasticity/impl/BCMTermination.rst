.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

BCMTermination
==============

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public class BCMTermination extends PlasticEnsembleTermination

   BCM rule

   :author: Trevor Bekolay

Constructors
------------
BCMTermination
^^^^^^^^^^^^^^

.. java:constructor:: public BCMTermination(Node node, String name, PlasticNodeTermination[] nodeTerminations, float[] initialTheta) throws StructuralException
   :outertype: BCMTermination

Methods
-------
clone
^^^^^

.. java:method:: @Override public PlasticEnsembleTermination clone() throws CloneNotSupportedException
   :outertype: BCMTermination

getTheta
^^^^^^^^

.. java:method:: public float[] getTheta()
   :outertype: BCMTermination

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: BCMTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

updateTransform
^^^^^^^^^^^^^^^

.. java:method:: public void updateTransform(float time, int start, int end) throws StructuralException
   :outertype: BCMTermination

