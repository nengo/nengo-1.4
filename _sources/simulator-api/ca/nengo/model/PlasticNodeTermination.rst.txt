PlasticNodeTermination
======================

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface PlasticNodeTermination extends Termination

   Plastic terminations can be ensemble level or node level. This interface describes the methods that a plastic node termination must implement.

   :author: Trevor Bekolay

Methods
-------
clone
^^^^^

.. java:method:: public PlasticNodeTermination clone() throws CloneNotSupportedException
   :outertype: PlasticNodeTermination

clone
^^^^^

.. java:method:: public PlasticNodeTermination clone(Node node) throws CloneNotSupportedException
   :outertype: PlasticNodeTermination

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: PlasticNodeTermination

   :return: The most recent input to the Termination

getOutput
^^^^^^^^^

.. java:method:: public float getOutput()
   :outertype: PlasticNodeTermination

   :return: The most recent output of the Termination (after summation and dynamics)

getWeights
^^^^^^^^^^

.. java:method:: public float[] getWeights()
   :outertype: PlasticNodeTermination

   :return: List of synaptic weights for each input channel

modifyWeights
^^^^^^^^^^^^^

.. java:method:: public void modifyWeights(float[] change, boolean save)
   :outertype: PlasticNodeTermination

   :param change: The change in the synaptic weights for each input channel
   :param save: Should the weights be saved for resetting purposes?

saveWeights
^^^^^^^^^^^

.. java:method:: public void saveWeights()
   :outertype: PlasticNodeTermination

   Save the current state of the weights so it can be reset there

setWeights
^^^^^^^^^^

.. java:method:: public void setWeights(float[] weights, boolean save)
   :outertype: PlasticNodeTermination

   :param weights: The new synaptic weights for each input channel
   :param save: Should the weights be saved for resetting purposes?

