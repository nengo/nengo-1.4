.. java:import:: java.io Serializable

.. java:import:: java.util HashMap

.. java:import:: ca.nengo.util ScriptGenException

Projection
==========

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public interface Projection extends Serializable

   A connection between an Origin and a Termination.

   :author: Bryan Tripp

Methods
-------
addBias
^^^^^^^

.. java:method:: public void addBias(int numInterneurons, float tauInterneurons, float tauBias, boolean excitatory, boolean optimize) throws StructuralException
   :outertype: Projection

   Makes all the synaptic weights in the projection either positive or negative, so that the projection accords with Dale's principle. This introduces a bias current postsynaptically, which is a function of presynaptic activity. This bias is removed by projecting the same function through an ensemble of interneurons. See Parisien, Anderson & Eliasmith, 2007, Neural Computation for more detail.

   :param numInterneurons: Number of interneurons through which bias function is projected
   :param tauInterneurons: Time constant of post-synaptic current in projection from presynaptic ensemble to interneurons (typically short)
   :param tauBias: Time constant of post-synaptic current in projection from interneurons to postsynaptic ensemble
   :param excitatory: If true, synapses in main projection are made excitatory; if false, inhibitory
   :param optimize: If true, performs optimizations to minimize distortion in the parallel projection through interneurons
   :throws StructuralException: if bias can't be added

biasIsEnabled
^^^^^^^^^^^^^

.. java:method:: public boolean biasIsEnabled()
   :outertype: Projection

   :return: true if bias is enabled

enableBias
^^^^^^^^^^

.. java:method:: public void enableBias(boolean enable)
   :outertype: Projection

   :param enable: If true, and initializeBias(...) has been called, then bias is enabled; if false it is disabled (default true)

getNetwork
^^^^^^^^^^

.. java:method:: public Network getNetwork()
   :outertype: Projection

   :return: The Network to which this Projection belongs

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin()
   :outertype: Projection

   :return: Origin of this Projection (where information comes from)

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination()
   :outertype: Projection

   :return: Termination of this Projection (where information goes)

getWeights
^^^^^^^^^^

.. java:method:: public float[][] getWeights()
   :outertype: Projection

   :return: Matrix of weights in this Projection (if there are neurons on each end, then these are synaptic weights)

removeBias
^^^^^^^^^^

.. java:method:: public void removeBias()
   :outertype: Projection

   Deletes bias-related interneurons, projections, origins, and terminations.

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: Projection

