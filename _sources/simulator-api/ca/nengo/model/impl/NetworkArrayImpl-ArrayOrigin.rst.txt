.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model PreciseSpikeOutput

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.plasticity.impl PESTermination

.. java:import:: ca.nengo.model.plasticity.impl PlasticEnsembleImpl

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

NetworkArrayImpl.ArrayOrigin
============================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class ArrayOrigin extends BasicOrigin
   :outertype: NetworkArrayImpl

   Origin representing the concatenation of origins on each of the ensembles within the network array.

Constructors
------------
ArrayOrigin
^^^^^^^^^^^

.. java:constructor:: public ArrayOrigin(NetworkArrayImpl parent, String name, Origin[] origins)
   :outertype: NetworkArrayImpl.ArrayOrigin

Methods
-------
clone
^^^^^

.. java:method:: public ArrayOrigin clone() throws CloneNotSupportedException
   :outertype: NetworkArrayImpl.ArrayOrigin

clone
^^^^^

.. java:method:: public ArrayOrigin clone(Node node) throws CloneNotSupportedException
   :outertype: NetworkArrayImpl.ArrayOrigin

getDecoders
^^^^^^^^^^^

.. java:method:: public float[][] getDecoders()
   :outertype: NetworkArrayImpl.ArrayOrigin

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: NetworkArrayImpl.ArrayOrigin

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: NetworkArrayImpl.ArrayOrigin

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: NetworkArrayImpl.ArrayOrigin

getNodeOrigins
^^^^^^^^^^^^^^

.. java:method:: public Origin[] getNodeOrigins()
   :outertype: NetworkArrayImpl.ArrayOrigin

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: NetworkArrayImpl.ArrayOrigin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues() throws SimulationException
   :outertype: NetworkArrayImpl.ArrayOrigin

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean req)
   :outertype: NetworkArrayImpl.ArrayOrigin

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values)
   :outertype: NetworkArrayImpl.ArrayOrigin

