.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util List

.. java:import:: ca.nengo.math.impl MultiLevelKLNetworkPartitioner

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl EnsembleTermination

.. java:import:: ca.nengo.model.impl NetworkArrayImpl

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.impl NetworkImpl.OriginWrapper

.. java:import:: ca.nengo.model.impl NetworkImpl.TerminationWrapper

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.neuron.impl LIFSpikeGenerator

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

NEFGPUInterface
===============

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class NEFGPUInterface

   Allows running NEFEnsembles on the GPU. Passes the ensemble data to the GPU through a native function. Passes input to the GPU each step and stores the output from the GPU in the appropriate locations.

   :author: Eric Crawford

Fields
------
inputOnGPU
^^^^^^^^^^

.. java:field::  boolean[][] inputOnGPU
   :outertype: NEFGPUInterface

myEndTime
^^^^^^^^^

.. java:field:: protected float myEndTime
   :outertype: NEFGPUInterface

myGPUEnsembles
^^^^^^^^^^^^^^

.. java:field:: protected NEFEnsembleImpl[] myGPUEnsembles
   :outertype: NEFGPUInterface

myGPUNetworkArrays
^^^^^^^^^^^^^^^^^^

.. java:field:: protected Node[] myGPUNetworkArrays
   :outertype: NEFGPUInterface

myGPUProjections
^^^^^^^^^^^^^^^^

.. java:field:: protected Projection[] myGPUProjections
   :outertype: NEFGPUInterface

myNodes
^^^^^^^

.. java:field:: protected Node[] myNodes
   :outertype: NEFGPUInterface

myProjections
^^^^^^^^^^^^^

.. java:field:: protected Projection[] myProjections
   :outertype: NEFGPUInterface

myRequireAllOutputsOnCPU
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected boolean myRequireAllOutputsOnCPU
   :outertype: NEFGPUInterface

myStartTime
^^^^^^^^^^^

.. java:field:: protected float myStartTime
   :outertype: NEFGPUInterface

nonGPUProjections
^^^^^^^^^^^^^^^^^

.. java:field:: protected Projection[] nonGPUProjections
   :outertype: NEFGPUInterface

representedInputValues
^^^^^^^^^^^^^^^^^^^^^^

.. java:field::  float[][][] representedInputValues
   :outertype: NEFGPUInterface

representedOutputValues
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field::  float[][][] representedOutputValues
   :outertype: NEFGPUInterface

spikeOutput
^^^^^^^^^^^

.. java:field::  float[][] spikeOutput
   :outertype: NEFGPUInterface

Constructors
------------
NEFGPUInterface
^^^^^^^^^^^^^^^

.. java:constructor:: public NEFGPUInterface(boolean interactive)
   :outertype: NEFGPUInterface

Methods
-------
findOptimalNodeAssignments
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static int[] findOptimalNodeAssignments(Node[] nodes, Projection[] projections, int numPartitions)
   :outertype: NEFGPUInterface

   Used when there are multiple GPU's running a simulation. Finds a distribution of nodes to GPU's that minimizes communication between GPU's while also ensuring the number of neurons running on each GPU is relatively balanced. Note that this problem (a variant of the min bisection problem) is NP-Complete, so a heuristic is employed.

   :author: Eric Crawford
   :return: an array of integers where the value in the i'th entry denotes the partition number of the i'th node ... in the "nodes" input array

getErrorMessage
^^^^^^^^^^^^^^^

.. java:method:: public static String getErrorMessage()
   :outertype: NEFGPUInterface

getNumDetectedDevices
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static int getNumDetectedDevices()
   :outertype: NEFGPUInterface

getNumDevices
^^^^^^^^^^^^^

.. java:method:: public static int getNumDevices()
   :outertype: NEFGPUInterface

getUseGPU
^^^^^^^^^

.. java:method:: public static boolean getUseGPU()
   :outertype: NEFGPUInterface

hideGPUTiming
^^^^^^^^^^^^^

.. java:method:: public static void hideGPUTiming()
   :outertype: NEFGPUInterface

initialize
^^^^^^^^^^

.. java:method:: public void initialize()
   :outertype: NEFGPUInterface

   Gets all the necessary data from the nodes and projections which are assigned to run on GPUss and puts it in a form appropriate for passing to the native setup function. The native setup function will create a thread for each GPU in use, process the data further until its in a form suitable for running on the GPU, and finally move all the data to the GPU. The GPU threads will be waiting for a call to nativeStep which will tell them to take a step.

   :author: Eric Crawford

kill
^^^^

.. java:method:: public void kill()
   :outertype: NEFGPUInterface

nativeGetNumDevices
^^^^^^^^^^^^^^^^^^^

.. java:method:: static native int nativeGetNumDevices()
   :outertype: NEFGPUInterface

nativeKill
^^^^^^^^^^

.. java:method:: static native void nativeKill()
   :outertype: NEFGPUInterface

nativeSetupRun
^^^^^^^^^^^^^^

.. java:method:: static native void nativeSetupRun(float[][][][] terminationTransforms, int[][] isDecodedTermination, float[][] terminationTau, float[][][] encoders, float[][][][] decoders, float[][] neuronData, int[][] projections, int[][] networkArrayData, int[][] ensembleData, int[] isSpikingEnsemble, int[] collectSpikes, int[][] outputRequiredOnCPU, float maxTimeStep, int[] deviceForNetworkArrays, int numDevicesRequested)
   :outertype: NEFGPUInterface

nativeStep
^^^^^^^^^^

.. java:method:: static native void nativeStep(float[][][] representedInput, float[][][] representedOutput, float[][] spikes, float startTime, float endTime)
   :outertype: NEFGPUInterface

setNumDevices
^^^^^^^^^^^^^

.. java:method:: public static void setNumDevices(int value)
   :outertype: NEFGPUInterface

setRequireAllOutputsOnCPU
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setRequireAllOutputsOnCPU(boolean require)
   :outertype: NEFGPUInterface

showGPUTiming
^^^^^^^^^^^^^

.. java:method:: public static void showGPUTiming()
   :outertype: NEFGPUInterface

step
^^^^

.. java:method:: public void step(float startTime, float endTime)
   :outertype: NEFGPUInterface

   1. Load data from terminations into "representedInputValues". 2. Call nativeStep which will run the GPU's for one step and return the results in "representedOutputValues". 3. Put the data from "representedOutputValues" into the appropriate origins.

   :author: Eric Crawford

takeGPUNodes
^^^^^^^^^^^^

.. java:method:: public Node[] takeGPUNodes(Node[] nodes)
   :outertype: NEFGPUInterface

   Finds all nodes in the given array which are supposed to execute on the GPU. Stores those nodes in myGPUNetworkArrays and returns the rest.

   :author: Eric Crawford

takeGPUProjections
^^^^^^^^^^^^^^^^^^

.. java:method:: public Projection[] takeGPUProjections(Projection[] projections)
   :outertype: NEFGPUInterface

   Finds all projections in the given array which are supposed to execute on the GPU. Stores those projections in myGPUProjections and returns the rest. takeGPUNodes should be called before this is called, since the nodes which run on the GPU determine which projections run on the GPU. (ie a projection runs on the GPU only if both its target and source run on the GPU).

   :author: Eric Crawford

