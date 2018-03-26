.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.muscle LinkSegmentModel

.. java:import:: ca.nengo.model.muscle SkeletalMuscle

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

LinkSegmentModelImpl
====================

.. java:package:: ca.nengo.model.muscle.impl
   :noindex:

.. java:type:: public class LinkSegmentModelImpl implements LinkSegmentModel

   Default implementation of LinkSegmentModel.

   :author: Bryan Tripp

Constructors
------------
LinkSegmentModelImpl
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public LinkSegmentModelImpl(String name, DynamicalSystem dynamics, float timeStep)
   :outertype: LinkSegmentModelImpl

   :param name: Segment name
   :param dynamics: Dynamical system governing function
   :param timeStep: dt

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public LinkSegmentModel clone() throws CloneNotSupportedException
   :outertype: LinkSegmentModelImpl

defineJoint
^^^^^^^^^^^

.. java:method:: public void defineJoint(String name, Function[] definition)
   :outertype: LinkSegmentModelImpl

   :param name: Name of joint
   :param definition: 2 or 3 Functions of generalized coordinates, corresponding to (x,y) position of the joint or (x,y,z) position of the joint

defineMuscle
^^^^^^^^^^^^

.. java:method:: public void defineMuscle(int input, SkeletalMuscle muscle, Function length, Function momentArm)
   :outertype: LinkSegmentModelImpl

   :param input: Which of the n inputs are we defining?
   :param muscle: Muscle being defined
   :param length: Function defining length?
   :param momentArm: Function defining momentum?

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: LinkSegmentModelImpl

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getJointNames
^^^^^^^^^^^^^

.. java:method:: public String[] getJointNames()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.muscle.LinkSegmentModel.getJointNames()`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getMode()`

getMuscles
^^^^^^^^^^

.. java:method:: public SkeletalMuscle[] getMuscles()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.muscle.LinkSegmentModel.getMuscles()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: LinkSegmentModelImpl

   **See also:** :java:ref:`ca.nengo.model.Node.setName(java.lang.String)`

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: LinkSegmentModelImpl

