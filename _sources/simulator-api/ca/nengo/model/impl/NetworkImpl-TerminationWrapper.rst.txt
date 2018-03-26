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

NetworkImpl.TerminationWrapper
==============================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class TerminationWrapper implements Termination
   :outertype: NetworkImpl

   Wraps a Termination with a new name (for exposing outside Network).

   :author: Bryan Tripp

Constructors
------------
TerminationWrapper
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TerminationWrapper(Node node, Termination wrapped, String name)
   :outertype: NetworkImpl.TerminationWrapper

   :param node: Parent node
   :param wrapped: Termination being wrapped
   :param name: New name

Methods
-------
clone
^^^^^

.. java:method:: @Override public TerminationWrapper clone() throws CloneNotSupportedException
   :outertype: NetworkImpl.TerminationWrapper

clone
^^^^^

.. java:method:: public TerminationWrapper clone(Node node) throws CloneNotSupportedException
   :outertype: NetworkImpl.TerminationWrapper

getBaseTermination
^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination getBaseTermination()
   :outertype: NetworkImpl.TerminationWrapper

   Unwraps terminations until it finds one that isn't wrapped

   :return: Underlying Termination, not wrapped

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: NetworkImpl.TerminationWrapper

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: NetworkImpl.TerminationWrapper

   :return: Extract the input to the termination.

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: NetworkImpl.TerminationWrapper

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: NetworkImpl.TerminationWrapper

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: NetworkImpl.TerminationWrapper

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: NetworkImpl.TerminationWrapper

getWrappedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination getWrappedTermination()
   :outertype: NetworkImpl.TerminationWrapper

   :return: Wrapped Termination

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: NetworkImpl.TerminationWrapper

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: NetworkImpl.TerminationWrapper

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: NetworkImpl.TerminationWrapper

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: NetworkImpl.TerminationWrapper

