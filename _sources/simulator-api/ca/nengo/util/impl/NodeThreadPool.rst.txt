.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util Date

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model.impl NetworkArrayImpl

.. java:import:: ca.nengo.util TaskSpawner

.. java:import:: ca.nengo.util ThreadTask

NodeThreadPool
==============

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class NodeThreadPool

   A pool of threads for running nodes in. All interaction with the threads is done through this class.

   :author: Eric Crawford

Fields
------
defaultNumJavaThreads
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected static final int defaultNumJavaThreads
   :outertype: NodeThreadPool

maxNumJavaThreads
^^^^^^^^^^^^^^^^^

.. java:field:: protected static final int maxNumJavaThreads
   :outertype: NodeThreadPool

myAverageTimePerStep
^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected double myAverageTimePerStep
   :outertype: NodeThreadPool

myCollectTimings
^^^^^^^^^^^^^^^^

.. java:field:: protected static boolean myCollectTimings
   :outertype: NodeThreadPool

myCurrentNumJavaThreads
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected int myCurrentNumJavaThreads
   :outertype: NodeThreadPool

myEndTime
^^^^^^^^^

.. java:field:: protected float myEndTime
   :outertype: NodeThreadPool

myLock
^^^^^^

.. java:field:: protected Object myLock
   :outertype: NodeThreadPool

myNodes
^^^^^^^

.. java:field:: protected Node[] myNodes
   :outertype: NodeThreadPool

myNumJavaThreads
^^^^^^^^^^^^^^^^

.. java:field:: protected static int myNumJavaThreads
   :outertype: NodeThreadPool

myNumSteps
^^^^^^^^^^

.. java:field:: protected int myNumSteps
   :outertype: NodeThreadPool

myNumThreads
^^^^^^^^^^^^

.. java:field:: protected int myNumThreads
   :outertype: NodeThreadPool

myProjections
^^^^^^^^^^^^^

.. java:field:: protected Projection[] myProjections
   :outertype: NodeThreadPool

myRunStartTime
^^^^^^^^^^^^^^

.. java:field:: protected long myRunStartTime
   :outertype: NodeThreadPool

myStartTime
^^^^^^^^^^^

.. java:field:: protected float myStartTime
   :outertype: NodeThreadPool

myTasks
^^^^^^^

.. java:field:: protected ThreadTask[] myTasks
   :outertype: NodeThreadPool

myThreads
^^^^^^^^^

.. java:field:: protected NodeThread[] myThreads
   :outertype: NodeThreadPool

numThreadsComplete
^^^^^^^^^^^^^^^^^^

.. java:field:: protected volatile int numThreadsComplete
   :outertype: NodeThreadPool

numThreadsWaiting
^^^^^^^^^^^^^^^^^

.. java:field:: protected volatile int numThreadsWaiting
   :outertype: NodeThreadPool

runFinished
^^^^^^^^^^^

.. java:field:: protected volatile boolean runFinished
   :outertype: NodeThreadPool

threadsRunning
^^^^^^^^^^^^^^

.. java:field:: protected volatile boolean threadsRunning
   :outertype: NodeThreadPool

Constructors
------------
NodeThreadPool
^^^^^^^^^^^^^^

.. java:constructor:: protected NodeThreadPool()
   :outertype: NodeThreadPool

NodeThreadPool
^^^^^^^^^^^^^^

.. java:constructor:: public NodeThreadPool(Network network, List<ThreadTask> threadTasks, boolean interactive)
   :outertype: NodeThreadPool

Methods
-------
collectNodes
^^^^^^^^^^^^

.. java:method:: public static List<Node> collectNodes(Node[] startingNodes, boolean breakDownNetworkArrays)
   :outertype: NodeThreadPool

   Return all the nodes in the network except subnetworks. Essentially returns a "flattened" version of the network. The breakDownNetworkArrays param lets the caller choose whether to include Network Arrays in the returned list (=false) or to return the NEFEnsembles in the network array (=true). This facility is provided because sometimes Network Arrays should be treated like Networks, which is what they are as far as java is concerned (they extend the NetworkImpl class), and sometimes it is better to treat them like NEFEnsembles, which they are designed to emulate (they're supposed to be an easier-to-build version of large NEFEnsembles).

   :author: Eric Crawford

collectProjections
^^^^^^^^^^^^^^^^^^

.. java:method:: public static List<Projection> collectProjections(Node[] startingNodes, Projection[] startingProjections)
   :outertype: NodeThreadPool

   Returns all the projections that would be in a "flattened" version of the network.

   :author: Eric Crawford

collectTasks
^^^^^^^^^^^^

.. java:method:: public static List<ThreadTask> collectTasks(Node[] startingNodes)
   :outertype: NodeThreadPool

   Returns all the tasks that would be in a "flattened" version of the network.

   :author: Eric Crawford

getEndTime
^^^^^^^^^^

.. java:method:: public float getEndTime()
   :outertype: NodeThreadPool

getMaxNumJavaThreads
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static int getMaxNumJavaThreads()
   :outertype: NodeThreadPool

getNumJavaThreads
^^^^^^^^^^^^^^^^^

.. java:method:: public static int getNumJavaThreads()
   :outertype: NodeThreadPool

getRunFinished
^^^^^^^^^^^^^^

.. java:method:: public boolean getRunFinished()
   :outertype: NodeThreadPool

getStartTime
^^^^^^^^^^^^

.. java:method:: public float getStartTime()
   :outertype: NodeThreadPool

initialize
^^^^^^^^^^

.. java:method:: protected void initialize(Network network, List<ThreadTask> threadTasks, boolean interactive)
   :outertype: NodeThreadPool

   1. Checks whether the GPU is to be used for the simulation. If it is, creates a GPU Thread, passes this thread the nodes and projections which are to be run on the GPU, and calls the initialization function of the gpu thread's NEFGPUInterface. Starts the GPU thread. 2. Creates the appropriate number of java threads and assigns to each a fair number of projections, nodes and tasks from those that remain after the GPU data has been dealt with. Starts the Java threads. 3. Initializes synchronization primitives and variables for collecting timing data if applicable.

   :author: Eric Crawford

isCollectingTimings
^^^^^^^^^^^^^^^^^^^

.. java:method:: public static boolean isCollectingTimings()
   :outertype: NodeThreadPool

isMultithreading
^^^^^^^^^^^^^^^^

.. java:method:: public static boolean isMultithreading()
   :outertype: NodeThreadPool

kill
^^^^

.. java:method:: public void kill()
   :outertype: NodeThreadPool

   Kill the threads in the pool by interrupting them. Each thread will handle the interrupt signal by ending its run method, which kills it.

   :author: Eric Crawford

setCollectTimings
^^^^^^^^^^^^^^^^^

.. java:method:: public static void setCollectTimings(boolean collectTimings)
   :outertype: NodeThreadPool

setNumJavaThreads
^^^^^^^^^^^^^^^^^

.. java:method:: public static void setNumJavaThreads(int value)
   :outertype: NodeThreadPool

step
^^^^

.. java:method:: public void step(float startTime, float endTime) throws SimulationException
   :outertype: NodeThreadPool

   Tell the threads in the current thread pool to take a step. The step consists of three phases: projections, nodes, tasks. All threads must complete a stage before any thread begins the next stage, so, for example, all threads must finish processing all of their projections before any thread starts processing its nodes.

   :author: Eric Crawford

threadFinished
^^^^^^^^^^^^^^

.. java:method:: public void threadFinished() throws InterruptedException
   :outertype: NodeThreadPool

   Called by the threads in this pool to signal that they are done a phase.

   :author: Eric Crawford

threadWait
^^^^^^^^^^

.. java:method:: public void threadWait() throws InterruptedException
   :outertype: NodeThreadPool

   Called by the threads in this node pool. Called once they finish a phase (projections, nodes or tasks). Forces them to wait on myLock.

   :author: Eric Crawford

turnOffMultithreading
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void turnOffMultithreading()
   :outertype: NodeThreadPool

