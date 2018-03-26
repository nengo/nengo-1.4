.. java:import:: java.io Serializable

Origin
======

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface Origin extends Serializable, Cloneable

   An source of information in a circuit model. Origins arise from Ensembles, ExternalInputs, and individual Neurons (although the latter Origins are mainly used internally within Ensembles, ie an Ensemble typically combines Neuron Origins into Ensemble Origins).

   An Origin object will often correspond loosely to the anatomical origin of a neural projection in the brain. However, there is not a strict correspondance. In particular, an Origin object may relate specifically to a particular decoding of activity in an Ensemble. For example, suppose a bundle of axons bifurcates and terminates in two places. This would be modelled with two Origin objects if the postsynaptic Ensembles received different functions of the variables represented by the presynaptic Ensemble. So, an Origin is best thought about as a source of information in a certain form, rather than an anatomical source of axons.

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public Origin clone() throws CloneNotSupportedException
   :outertype: Origin

   :throws CloneNotSupportedException: if clone cannot be made
   :return: Valid clone

clone
^^^^^

.. java:method:: public Origin clone(Node node) throws CloneNotSupportedException
   :outertype: Origin

   Clone method that changes necessary parameters to point to a new parent, for use in cloning ensembles, etc.

   :param e: New parent ensemble
   :throws CloneNotSupportedException: if clone cannot be made
   :return: A clone of the origin for the new parent ensemble

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: Origin

   :return: Dimensionality of information coming from this Origin (eg number of axons, or dimension of decoded function of variables represented by the Ensemble)

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: Origin

   :return: Name of this Origin (unique in the scope of a source of Origins, eg a Neuron or Ensemble)

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: Origin

   :return: The Node to which the Origin belongs

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: Origin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues() throws SimulationException
   :outertype: Origin

   :throws SimulationException: if there is any problem retrieving values
   :return: Instantaneous output from this Origin.

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: Origin

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput val)
   :outertype: Origin

   :param Instantaneous: output from this Origin.

