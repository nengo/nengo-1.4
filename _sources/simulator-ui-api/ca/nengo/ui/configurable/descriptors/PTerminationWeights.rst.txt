.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.panels TerminationWeightsInputPanel

PTerminationWeights
===================

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public class PTerminationWeights extends Property

   Config Descriptor for Termination Weights Matrix

   :author: Shu Wu

Constructors
------------
PTerminationWeights
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PTerminationWeights(String name, int dimensions)
   :outertype: PTerminationWeights

   :param name: TODO
   :param dimensions: TODO

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected TerminationWeightsInputPanel createInputPanel()
   :outertype: PTerminationWeights

getEnsembleDimensions
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public int getEnsembleDimensions()
   :outertype: PTerminationWeights

   :return: Dimensions of the Ensemble the termination weights belong to

getTypeClass
^^^^^^^^^^^^

.. java:method:: @Override public Class<float[][]> getTypeClass()
   :outertype: PTerminationWeights

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: PTerminationWeights

