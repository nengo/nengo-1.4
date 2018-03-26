.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util Map

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl EnsembleImpl

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.util TaskSpawner

.. java:import:: ca.nengo.util ThreadTask

.. java:import:: ca.nengo.util.impl LearningTask

PlasticEnsembleImpl
===================

.. java:package:: ca.nengo.model.plasticity.impl
   :noindex:

.. java:type:: public class PlasticEnsembleImpl extends EnsembleImpl implements TaskSpawner

   An extension of the default ensemble; connection weights can be modified by a plasticity rule.

   TODO: test

   :author: Trevor Bekolay

Fields
------
myPlasticEnsembleTerminations
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected Map<String, PlasticEnsembleTermination> myPlasticEnsembleTerminations
   :outertype: PlasticEnsembleImpl

Constructors
------------
PlasticEnsembleImpl
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PlasticEnsembleImpl(String name, Node[] nodes) throws StructuralException
   :outertype: PlasticEnsembleImpl

   :param name: Name of Ensemble
   :param nodes: Nodes that make up the Ensemble
   :throws StructuralException: if the given Nodes contain Terminations with the same name but different dimensions

PlasticEnsembleImpl
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PlasticEnsembleImpl(String name, NodeFactory factory, int n) throws StructuralException
   :outertype: PlasticEnsembleImpl

Methods
-------
addTasks
^^^^^^^^

.. java:method:: public void addTasks(ThreadTask[] tasks)
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.util.TaskSpawner.addTasks`

clone
^^^^^

.. java:method:: @Override public PlasticEnsembleImpl clone() throws CloneNotSupportedException
   :outertype: PlasticEnsembleImpl

getLearning
^^^^^^^^^^^

.. java:method:: public boolean getLearning()
   :outertype: PlasticEnsembleImpl

getPlasticityInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public float getPlasticityInterval()
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.plasticity.PlasticEnsemble.getPlasticityInterval()`

getTasks
^^^^^^^^

.. java:method:: public ThreadTask[] getTasks()
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.util.TaskSpawner.getTasks`

getTermination
^^^^^^^^^^^^^^

.. java:method:: @Override public Termination getTermination(String name) throws StructuralException
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: @Override public Termination[] getTerminations()
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getTerminations()`

isPopulationPlastic
^^^^^^^^^^^^^^^^^^^

.. java:method:: protected static boolean isPopulationPlastic(Termination[] terminations)
   :outertype: PlasticEnsembleImpl

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: @Override public void run(float startTime, float endTime) throws SimulationException
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Ensemble.run(float,float)`

setLearning
^^^^^^^^^^^

.. java:method:: public void setLearning(boolean learning)
   :outertype: PlasticEnsembleImpl

setPlasticityInterval
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setPlasticityInterval(float time)
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.plasticity.PlasticEnsemble.setPlasticityInterval(float)`

setStates
^^^^^^^^^

.. java:method:: public void setStates(float endTime) throws SimulationException
   :outertype: PlasticEnsembleImpl

setTasks
^^^^^^^^

.. java:method:: public void setTasks(ThreadTask[] tasks)
   :outertype: PlasticEnsembleImpl

   **See also:** :java:ref:`ca.nengo.util.TaskSpawner.setTasks`

