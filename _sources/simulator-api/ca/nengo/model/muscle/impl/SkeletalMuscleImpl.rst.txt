.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Properties

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.dynamics.impl RK45Integrator

.. java:import:: ca.nengo.dynamics.impl SimpleLTISystem

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl BasicTermination

.. java:import:: ca.nengo.model.muscle SkeletalMuscle

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

SkeletalMuscleImpl
==================

.. java:package:: ca.nengo.model.muscle.impl
   :noindex:

.. java:type:: public class SkeletalMuscleImpl implements SkeletalMuscle

   Basic SkeletalMuscle implementation with unspecified activation-force dynamics. TODO: origins (need spindle and GTO implementations)

   :author: Bryan Tripp

Constructors
------------
SkeletalMuscleImpl
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SkeletalMuscleImpl(String name, DynamicalSystem dynamics) throws StructuralException
   :outertype: SkeletalMuscleImpl

   :param name: Muscle name
   :param dynamics: Dynamics for the muscle
   :throws StructuralException: if dimensionality isn't 2 in, 1 out

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public SkeletalMuscle clone() throws CloneNotSupportedException
   :outertype: SkeletalMuscleImpl

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: SkeletalMuscleImpl

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getForce
^^^^^^^^

.. java:method:: public float getForce()
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.muscle.SkeletalMuscle.getForce()`

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setLength
^^^^^^^^^

.. java:method:: public void setLength(float length)
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.muscle.SkeletalMuscle.setLength(float)`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: SkeletalMuscleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: SkeletalMuscleImpl

   :param name: The new name (must be unique within any networks of which this Node will be a part)

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: SkeletalMuscleImpl

