.. java:import:: java.util ArrayList

.. java:import:: java.util Date

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model.impl SocketUDPNode

.. java:import:: ca.nengo.util ThreadTask

NodeThread
==========

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class NodeThread extends Thread

   A thread for running projections, nodes and tasks in. Projections are all runs before nodes, nodes before tasks.

   :author: Eric Crawford

Constructors
------------
NodeThread
^^^^^^^^^^

.. java:constructor:: public NodeThread(NodeThreadPool nodePool, Node[] nodes, int startIndexInNodes, int endIndexInNodes, Projection[] projections, int startIndexInProjections, int endIndexInProjections, ThreadTask[] tasks, int startIndexInTasks, int endIndexInTasks)
   :outertype: NodeThread

Methods
-------
finished
^^^^^^^^

.. java:method:: public void finished()
   :outertype: NodeThread

getMyAverageTimeOnNodesPerStep
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMyAverageTimeOnNodesPerStep()
   :outertype: NodeThread

getMyAverageTimeOnProjectionsPerStep
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMyAverageTimeOnProjectionsPerStep()
   :outertype: NodeThread

getMyAverageTimeOnTasksPerStep
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getMyAverageTimeOnTasksPerStep()
   :outertype: NodeThread

kill
^^^^

.. java:method:: protected void kill()
   :outertype: NodeThread

run
^^^

.. java:method:: public void run()
   :outertype: NodeThread

runNodes
^^^^^^^^

.. java:method:: protected void runNodes(float startTime, float endTime) throws SimulationException
   :outertype: NodeThread

runProjections
^^^^^^^^^^^^^^

.. java:method:: protected void runProjections(float startTime, float endTime) throws SimulationException
   :outertype: NodeThread

runTasks
^^^^^^^^

.. java:method:: protected void runTasks(float startTime, float endTime) throws SimulationException
   :outertype: NodeThread

setCollectTimings
^^^^^^^^^^^^^^^^^

.. java:method:: public void setCollectTimings(boolean myCollectTimings)
   :outertype: NodeThread

waitForPool
^^^^^^^^^^^

.. java:method:: public void waitForPool()
   :outertype: NodeThread

