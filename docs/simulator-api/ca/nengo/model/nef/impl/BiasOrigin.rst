.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.math.impl GradientDescentApproximator

.. java:import:: ca.nengo.math.impl GradientDescentApproximator.Constraints

.. java:import:: ca.nengo.math.impl IdentityFunction

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron.impl LIFNeuronFactory

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

.. java:import:: ca.nengo.util.impl Rectifier

BiasOrigin
==========

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class BiasOrigin extends DecodedOrigin

   Part of a projection in which each of the Nodes making up an Ensemble is a source of only excitatory or inhibitory connections.

   The theory is presented in Parisien, Anderson & Eliasmith (2007).

   Such a projection includes a "base" DecodedOrigin and DecodedTermination (a projection between these may have weights of mixed sign). The projection is expanded with a BiasOrigin a pair of BiasTerminations, and a new NEFEnsemble of interneurons. The make weight signs uniform, a projection is established between the BiasOrigin and BiasTermination, in parallel with the original projection. The effective synaptic weights that arise from the combination of these two projections are of uniform sign. However, the post-synaptic Ensemble receives extra bias current as a result. This bias current is cancelled by a projection from the BiasOrigin through the interneurons, to a second BiasTermination.

   TODO: account for transformations in the Termination, which can change sign and magnitude of weights

   :author: Bryan Tripp

Constructors
------------
BiasOrigin
^^^^^^^^^^

.. java:constructor:: public BiasOrigin(NEFEnsemble ensemble, String name, Node[] nodes, String nodeOrigin, float[][] constantOutputs, int numInterneurons, boolean excitatory) throws StructuralException
   :outertype: BiasOrigin

   :param ensemble: Parent ensemble
   :param name: Origin name
   :param nodes: Nodes in ensemble?
   :param nodeOrigin: Name of origin to use for bias origin
   :param constantOutputs: ?
   :param numInterneurons: Number of interneurons to create
   :param excitatory: Excitatory or inhibitory?
   :throws StructuralException: if DecodedOrigin can't be created

Methods
-------
getInterneurons
^^^^^^^^^^^^^^^

.. java:method:: public NEFEnsemble getInterneurons()
   :outertype: BiasOrigin

   :return: An ensemble of interneurons through which this Origin must project (in parallel with its direct projection) to compensate for the bias introduced by making all weights the same sign.

getRange
^^^^^^^^

.. java:method:: public float[] getRange()
   :outertype: BiasOrigin

   :return: Vector of mininum and maximum output of this origin, ie {min, max}

optimizeDecoders
^^^^^^^^^^^^^^^^

.. java:method:: public void optimizeDecoders(float[][] baseWeights, float[] biasEncoders, boolean excitatory)
   :outertype: BiasOrigin

   This method adjusts bias decoders so that the bias function is as flat as possible, without changing the bias encoders on the post-synaptic ensemble. Distortion can be minimized by calling this method and then calling optimizeInterneuronDomain().

   :param baseWeights: Matrix of synaptic weights in the unbiased projection (ie the weights of mixed sign)
   :param biasEncoders: Encoders of the bias dimension on the post-synaptic ensemble
   :param excitatory: If true, weights are to be kept positive (otherwise negative)

optimizeInterneuronDomain
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void optimizeInterneuronDomain(DecodedTermination interneuronTermination, DecodedTermination biasTermination)
   :outertype: BiasOrigin

   This method adjusts the interneuron channel so that the interneurons are tuned to the range of values that is output by the bias function.

   :param interneuronTermination: The Termination on getInterneurons() that recieves input from this Origin
   :param biasTermination: The BiasTermination to which the interneurons project (not the one to which this Origin projects directly)

