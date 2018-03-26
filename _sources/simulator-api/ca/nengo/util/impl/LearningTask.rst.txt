.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model.plasticity.impl PlasticEnsembleImpl

.. java:import:: ca.nengo.model.plasticity.impl PlasticEnsembleTermination

.. java:import:: ca.nengo.util ThreadTask

LearningTask
============

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class LearningTask implements ThreadTask

   Implementation of a ThreadTask to multithread learning in a plastic ensemble. This task will seperate the learning calculations such as getDerivative into indepdent threadable tasks.

   :author: Jonathan Lai

Constructors
------------
LearningTask
^^^^^^^^^^^^

.. java:constructor:: public LearningTask(PlasticEnsembleImpl parent, PlasticEnsembleTermination termination, int start, int end)
   :outertype: LearningTask

   :param parent: Parent PlasticEnsemble of this task
   :param termination: PlasticEnsembleTermination that this task will learn on
   :param start: Starting index for the set of terminations to learn on
   :param end: Ending index for the set of terminations to learn on

LearningTask
^^^^^^^^^^^^

.. java:constructor:: public LearningTask(LearningTask copy, int start, int end)
   :outertype: LearningTask

   :param copy: LearningTask to copy the parent and termination values from
   :param start: Starting index for the set of terminations to learn on
   :param end: Ending index for the set of terminations to learn on

Methods
-------
clone
^^^^^

.. java:method:: @Override public LearningTask clone() throws CloneNotSupportedException
   :outertype: LearningTask

clone
^^^^^

.. java:method:: public LearningTask clone(PlasticEnsembleImpl parent) throws CloneNotSupportedException
   :outertype: LearningTask

clone
^^^^^

.. java:method:: public LearningTask clone(PlasticEnsembleImpl parent, PlasticEnsembleTermination term) throws CloneNotSupportedException
   :outertype: LearningTask

getParent
^^^^^^^^^

.. java:method:: public PlasticEnsembleImpl getParent()
   :outertype: LearningTask

   **See also:** :java:ref:`ca.nengo.util.ThreadTask.getParent()`

isFinished
^^^^^^^^^^

.. java:method:: public boolean isFinished()
   :outertype: LearningTask

   **See also:** :java:ref:`ca.nengo.util.ThreadTask.isFinished()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: LearningTask

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: LearningTask

   **See also:** :java:ref:`ca.nengo.util.ThreadTask.run(float,float)`

