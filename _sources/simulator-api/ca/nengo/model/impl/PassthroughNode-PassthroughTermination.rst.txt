.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

PassthroughNode.PassthroughTermination
======================================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public static class PassthroughTermination implements Termination
   :outertype: PassthroughNode

   Termination that receives input unaltered.

Constructors
------------
PassthroughTermination
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PassthroughTermination(Node node, String name, int dimension)
   :outertype: PassthroughNode.PassthroughTermination

   :param node: Parent node
   :param name: Termination name
   :param dimension: Dimensionality of input

PassthroughTermination
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PassthroughTermination(Node node, String name, int dimension, float[][] transform)
   :outertype: PassthroughNode.PassthroughTermination

   :param node: Parent node
   :param name: Termination name
   :param dimension: Dimensionality of input
   :param transform: Transformation matrix

PassthroughTermination
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PassthroughTermination(Node node, String name, float[][] transform)
   :outertype: PassthroughNode.PassthroughTermination

   :param node: Parent node
   :param name: Termination name
   :param transform: Transformation matrix

Methods
-------
clone
^^^^^

.. java:method:: @Override public PassthroughTermination clone() throws CloneNotSupportedException
   :outertype: PassthroughNode.PassthroughTermination

clone
^^^^^

.. java:method:: public PassthroughTermination clone(Node node) throws CloneNotSupportedException
   :outertype: PassthroughNode.PassthroughTermination

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: PassthroughNode.PassthroughTermination

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: PassthroughNode.PassthroughTermination

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: PassthroughNode.PassthroughTermination

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: PassthroughNode.PassthroughTermination

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: PassthroughNode.PassthroughTermination

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: PassthroughNode.PassthroughTermination

getTransform
^^^^^^^^^^^^

.. java:method:: public float[][] getTransform()
   :outertype: PassthroughNode.PassthroughTermination

   :return: Transformation matrix

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues()
   :outertype: PassthroughNode.PassthroughTermination

   :return: Values currently stored in termination

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: PassthroughNode.PassthroughTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: PassthroughNode.PassthroughTermination

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: PassthroughNode.PassthroughTermination

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: PassthroughNode.PassthroughTermination

