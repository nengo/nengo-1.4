.. java:import:: java.io File

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io Serializable

.. java:import:: java.lang.reflect Method

.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StepListener

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef.impl DecodableEnsembleImpl

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.sim Simulator

.. java:import:: ca.nengo.sim.impl LocalSimulator

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util TaskSpawner

.. java:import:: ca.nengo.util ThreadTask

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl ProbeTask

.. java:import:: ca.nengo.util.impl ScriptGenerator

NetworkImpl.OriginWrapper
=========================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class OriginWrapper implements Origin
   :outertype: NetworkImpl

   Wraps an Origin with a new name (for exposing outside Network).

   :author: Bryan Tripp

Constructors
------------
OriginWrapper
^^^^^^^^^^^^^

.. java:constructor:: public OriginWrapper(Node node, Origin wrapped, String name)
   :outertype: NetworkImpl.OriginWrapper

   :param node: Parent node
   :param wrapped: Warpped Origin
   :param name: Name of new origin

OriginWrapper
^^^^^^^^^^^^^

.. java:constructor:: public OriginWrapper()
   :outertype: NetworkImpl.OriginWrapper

   Default constructor TODO: Is this necessary?

Methods
-------
clone
^^^^^

.. java:method:: @Override public Origin clone() throws CloneNotSupportedException
   :outertype: NetworkImpl.OriginWrapper

clone
^^^^^

.. java:method:: public Origin clone(Node node) throws CloneNotSupportedException
   :outertype: NetworkImpl.OriginWrapper

getBaseOrigin
^^^^^^^^^^^^^

.. java:method:: public Origin getBaseOrigin()
   :outertype: NetworkImpl.OriginWrapper

   Unwraps Origin until it finds one that isn't wrapped

   :return: Base origin if there are multiple levels of wrapping

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: NetworkImpl.OriginWrapper

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: NetworkImpl.OriginWrapper

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: NetworkImpl.OriginWrapper

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: NetworkImpl.OriginWrapper

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues() throws SimulationException
   :outertype: NetworkImpl.OriginWrapper

getWrappedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin getWrappedOrigin()
   :outertype: NetworkImpl.OriginWrapper

   :return: The underlying wrapped Origin

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: NetworkImpl.OriginWrapper

   :param name: Name

setNode
^^^^^^^

.. java:method:: public void setNode(Node node)
   :outertype: NetworkImpl.OriginWrapper

   :param node: Parent node

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: NetworkImpl.OriginWrapper

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values)
   :outertype: NetworkImpl.OriginWrapper

setWrappedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public void setWrappedOrigin(Origin wrapped)
   :outertype: NetworkImpl.OriginWrapper

   :param wrapped: Set the underlying wrapped Origin

