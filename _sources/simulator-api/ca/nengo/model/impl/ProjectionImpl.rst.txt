.. java:import:: java.util HashMap

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.math.impl IdentityFunction

.. java:import:: ca.nengo.math.impl PostfixFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model.impl NetworkImpl.OriginWrapper

.. java:import:: ca.nengo.model.impl NetworkImpl.TerminationWrapper

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl NetworkArrayImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl BiasOrigin

.. java:import:: ca.nengo.model.nef.impl BiasTermination

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

ProjectionImpl
==============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class ProjectionImpl implements Projection

   Default implementation of \ ``Projection``\ . TODO: unit tests

   :author: Bryan Tripp

Constructors
------------
ProjectionImpl
^^^^^^^^^^^^^^

.. java:constructor:: public ProjectionImpl(Origin origin, Termination termination, Network network)
   :outertype: ProjectionImpl

   :param origin: The Origin at the start of this Projection
   :param termination: The Termination at the end of this Projection
   :param network: The Network of which this Projection is a part

Methods
-------
addBias
^^^^^^^

.. java:method:: public void addBias(int numInterneurons, float tauInterneurons, float tauBias, boolean excitatory, boolean optimize) throws StructuralException
   :outertype: ProjectionImpl

   :throws StructuralException: if the origin and termination are not decoded

   **See also:** :java:ref:`ca.nengo.model.Projection.addBias(int,float,float,boolean,boolean)`

addFunctionScript
^^^^^^^^^^^^^^^^^

.. java:method::  String addFunctionScript(StringBuilder py, DecodedOrigin dOrigin) throws ScriptGenException
   :outertype: ProjectionImpl

biasIsEnabled
^^^^^^^^^^^^^

.. java:method:: public boolean biasIsEnabled()
   :outertype: ProjectionImpl

   **See also:** :java:ref:`ca.nengo.model.Projection.biasIsEnabled()`

enableBias
^^^^^^^^^^

.. java:method:: public void enableBias(boolean enable)
   :outertype: ProjectionImpl

   **See also:** :java:ref:`ca.nengo.model.Projection.enableBias(boolean)`

getNetwork
^^^^^^^^^^

.. java:method:: public Network getNetwork()
   :outertype: ProjectionImpl

   **See also:** :java:ref:`ca.nengo.model.Projection.getNetwork()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin()
   :outertype: ProjectionImpl

   **See also:** :java:ref:`ca.nengo.model.Projection.getOrigin()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination()
   :outertype: ProjectionImpl

   **See also:** :java:ref:`ca.nengo.model.Projection.getTermination()`

getTransformScript
^^^^^^^^^^^^^^^^^^

.. java:method::  String getTransformScript(DecodedTermination dTermination, int offset) throws ScriptGenException
   :outertype: ProjectionImpl

getWeights
^^^^^^^^^^

.. java:method:: public float[][] getWeights()
   :outertype: ProjectionImpl

   **See also:** :java:ref:`ca.nengo.model.Projection.getWeights()`

removeBias
^^^^^^^^^^

.. java:method:: public void removeBias()
   :outertype: ProjectionImpl

   **See also:** :java:ref:`ca.nengo.model.Projection.removeBias()`

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: ProjectionImpl

