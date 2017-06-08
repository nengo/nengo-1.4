.. java:import:: java.io Serializable

Termination
===========

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface Termination extends Serializable, Resettable, Cloneable

   A destination for information in a circuit model. A Termination is normally associated with a neural Ensemble or an individual Neuron, although other terminations could be modelled (eg muscles).

   Terminations onto neural Ensembles can be modelled in two ways. First, a Termination can model a set of axons that end at an Ensemble. In this case the dimension of the Termination equals the number of axons. Associated with each Neuron in the Ensemble will be synaptic weights (possibly zero) corresponding to each axon (i.e. each dimension of the Termination).

   Alternatively, in a connection between two NEFEnsembles, a termination may have a smaller number of dimensions that summarize activity in all the axons. In this case, each dimension of the termination corresponds to a dimension of a represented vector or function. Synaptic weights are not stored anywhere explicitly. Synaptic weights are instead decomposed into decoding vectors, a transformation matrix, and encoding vectors. The decoding vectors are associated with the sending Ensemble. The encoding vectors are associated with the receiving ensemble. The transformation matrix is a property of the projection, but it happens that we keep it with the receiving Ensemble, for various reasons. See Eliasmith & Anderson, 2003 for related theory.

   Note that in each case, a corresponding Origin and Termination have the same dimensionality, and that this is the dimensionality associated with the Origin. The receiving Ensemble is responsible for the weight matrix in the first case, and for the transformation matrix in the second case, which transform inputs into dimensions that the receiving Ensemble can use.

   Note also that the second method is more efficient when the number of neurons in each ensemble is much larger than the number of dimensions in represented variables, as is typical.

   TODO: should probably extract properties-related methods into another interface (Configurable?) possibly supporting types

   :author: Bryan Tripp

Methods
-------
clone
^^^^^

.. java:method:: public Termination clone() throws CloneNotSupportedException
   :outertype: Termination

   :throws CloneNotSupportedException: if clone can't be made
   :return: Valid clone

clone
^^^^^

.. java:method:: public Termination clone(Node node) throws CloneNotSupportedException
   :outertype: Termination

   Clone method that changes necessary parameters to point to a new parent, for use in cloning ensembles, etc.

   :param node: New parent node
   :throws CloneNotSupportedException: if clone cannot be made
   :return: A clone of the termination for the new parent ensemble

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: Termination

   :return: Dimensionality of information entering this Termination (eg number of axons, or dimension of decoded function of variables represented by sending Ensemble)

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: Termination

   :return: Latest input to the termination.

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: Termination

   :return: Whether the Termination is modulatory, in the sense of neuromodulation, ie true if input via this Termination is not summed to drive a node, but influences node activity in some other way

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: Termination

   :return: Name of this Termination (unique in the scope of the object the which the Termination is connected, eg the Neuron or Ensemble).

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: Termination

   :return: The Node to which this Termination belongs

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: Termination

   :return: Time constant of dominant dynamics

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: Termination

   :param modulatory: True if the Termination is to be modulatory

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: Termination

   :param tau: Time constant of dominant dynamics
   :throws StructuralException: if the time constant cannot be changed

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: Termination

   :param values: InstantaneousOutput (eg from another Ensemble) to apply to this Termination.
   :throws SimulationException: if the given values have the wrong dimension

