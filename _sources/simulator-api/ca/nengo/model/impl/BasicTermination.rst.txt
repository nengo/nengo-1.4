.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl CanonicalModel

.. java:import:: ca.nengo.dynamics.impl LTISystem

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

BasicTermination
================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class BasicTermination implements Termination, Resettable

   A basic implementation of Termination with configurable dynamics and no special integrative features.

   :author: Bryan Tripp

Constructors
------------
BasicTermination
^^^^^^^^^^^^^^^^

.. java:constructor:: public BasicTermination(Node node, DynamicalSystem dynamics, Integrator integrator, String name)
   :outertype: BasicTermination

   :param node: Node that owns this termination
   :param dynamics: Dynamical System that defines the dynamics
   :param integrator: Integrator for the DS
   :param name: Name of the termination

Methods
-------
clone
^^^^^

.. java:method:: @Override public BasicTermination clone() throws CloneNotSupportedException
   :outertype: BasicTermination

clone
^^^^^

.. java:method:: public BasicTermination clone(Node node) throws CloneNotSupportedException
   :outertype: BasicTermination

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getDimensions()`

getInput
^^^^^^^^

.. java:method:: public InstantaneousOutput getInput()
   :outertype: BasicTermination

   :return: Extract the input to the termination.

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getModulatory()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getNode()`

getOutput
^^^^^^^^^

.. java:method:: public TimeSeries getOutput()
   :outertype: BasicTermination

   Note: typically called by the Node to which the Termination belongs.

   :return: The most recent input multiplied

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getTau()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: BasicTermination

   Runs the Termination, making a TimeSeries of output from this Termination available from getOutput().

   :param startTime: simulation time at which running starts (s)
   :param endTime: simulation time at which running ends (s)
   :throws SimulationException: if a problem is encountered while trying to run

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setModulatory(boolean)`

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setTau(float)`

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: BasicTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setValues(ca.nengo.model.InstantaneousOutput)`

