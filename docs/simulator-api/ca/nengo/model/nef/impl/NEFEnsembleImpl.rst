.. java:import:: java.util Arrays

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: java.util Random

.. java:import:: java.lang StringBuilder

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics LinearSystem

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.dynamics.impl SimpleLTISystem

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl WeightedCostApproximator

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model PlasticNodeTermination

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef NEFNode

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron.impl LIFNeuronFactory

.. java:import:: ca.nengo.model.neuron.impl LIFSpikeGenerator

.. java:import:: ca.nengo.model.neuron.impl SpikeGeneratorOrigin

.. java:import:: ca.nengo.model.neuron.impl SpikingNeuron

.. java:import:: ca.nengo.model.plasticity.impl BCMTermination

.. java:import:: ca.nengo.model.plasticity.impl PESTermination

.. java:import:: ca.nengo.model.plasticity.impl PlasticEnsembleTermination

.. java:import:: ca.nengo.model.plasticity.impl PreLearnTermination

.. java:import:: ca.nengo.model.plasticity.impl hPESTermination

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl LearningTask

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

NEFEnsembleImpl
===============

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class NEFEnsembleImpl extends DecodableEnsembleImpl implements NEFEnsemble

   Default implementation of NEFEnsemble. TODO: links to NEF documentation TODO: test

   :author: Bryan Tripp

Fields
------
BIAS_SUFFIX
^^^^^^^^^^^

.. java:field:: public static String BIAS_SUFFIX
   :outertype: NEFEnsembleImpl

   Append to bias termination names

INTERNEURON_SUFFIX
^^^^^^^^^^^^^^^^^^

.. java:field:: public static String INTERNEURON_SUFFIX
   :outertype: NEFEnsembleImpl

   Append to interneuron names

Constructors
------------
NEFEnsembleImpl
^^^^^^^^^^^^^^^

.. java:constructor:: public NEFEnsembleImpl(String name, NEFNode[] nodes, float[][] encoders, ApproximatorFactory factory, float[][] evalPoints, float[] radii) throws StructuralException
   :outertype: NEFEnsembleImpl

   :param name: Unique name of Ensemble
   :param nodes: Nodes that make up the Ensemble
   :param encoders: List of encoding vectors (one for each node). All must have same length
   :param factory: Source of LinearApproximators to use in decoding output
   :param evalPoints: Vector inputs at which output is found to produce DecodedOrigins
   :param radii: Radius for each dimension
   :throws StructuralException: if there are a different number of Nodes than encoding vectors or if not all encoders have the same length

Methods
-------
addBCMTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public synchronized Termination addBCMTermination(String name, float[][] weights, float tauPSC, boolean modulatory, float[] theta) throws StructuralException
   :outertype: NEFEnsembleImpl

   :param name: Unique name for the Termination (in the scope of this Node)
   :param weights: Each row is used as a 1 by m matrix of weights in a new termination on the nth expandable node
   :param tauPSC: Time constant with which incoming signals are filtered. (All Terminations have this property, but it may have slightly different interpretations per implementation.)
   :param modulatory: If true, inputs to the Termination are not summed with other inputs (they only have modulatory effects, eg on plasticity, which must be defined elsewhere).
   :throws StructuralException: if weight matrix dimensionality is incorrect
   :return: Termination that was added

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.addTermination(java.lang.String,float[][],float,boolean)`

addBiasOrigin
^^^^^^^^^^^^^

.. java:method:: public BiasOrigin addBiasOrigin(Origin existing, int numInterneurons, String name, boolean excitatory) throws StructuralException
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.addBiasOrigin(ca.nengo.model.Origin,int,java.lang.String,boolean)`

addBiasTerminations
^^^^^^^^^^^^^^^^^^^

.. java:method:: public BiasTermination[] addBiasTerminations(DecodedTermination baseTermination, float interneuronTauPSC, float[][] biasDecoders, float[][] functionDecoders) throws StructuralException
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.addBiasTerminations(ca.nengo.model.nef.impl.DecodedTermination,float,float[][],float[][])`

addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin) throws StructuralException
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.addDecodedOrigin(java.lang.String,Function[],String)`

addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(DecodedOrigin o)
   :outertype: NEFEnsembleImpl

   Adds the given DecodedOrigin to this ensemble.

   :param o: the origin to be added
   :return: the new origin

addDecodedSignalOrigin
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedSignalOrigin(String name, TimeSeries targetSignal, TimeSeries[] evalSignals, String nodeOrigin) throws StructuralException
   :outertype: NEFEnsembleImpl

   Similar to addDecodedOrigin, but uses a target signal and evaluation signals (over time) rather than a target function and evaluation points.

   :param name: Name of origin
   :param targetSignal: signal that the origin should produce
   :param evalSignals: evaluation signals used to calculate decoders
   :param nodeOrigin: origin from which to draw output from each node
   :return: the new DecodedOrigin created

addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Termination addDecodedTermination(String name, float[][] matrix, float tauPSC, boolean isModulatory) throws StructuralException
   :outertype: NEFEnsembleImpl

addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Termination addDecodedTermination(String name, float[][] matrix, float[] tfNumerator, float[] tfDenominator, float passthrough, boolean isModulatory) throws StructuralException
   :outertype: NEFEnsembleImpl

addHPESTermination
^^^^^^^^^^^^^^^^^^

.. java:method:: public synchronized Termination addHPESTermination(String name, float[][] weights, float tauPSC, boolean modulatory, float[] theta) throws StructuralException
   :outertype: NEFEnsembleImpl

   :param name: Unique name for the Termination (in the scope of this Node)
   :param weights: Each row is used as a 1 by m matrix of weights in a new termination on the nth expandable node
   :param tauPSC: Time constant with which incoming signals are filtered. (All Terminations have this property, but it may have slightly different interpretations per implementation.)
   :param modulatory: If true, inputs to the Termination are not summed with other inputs (they only have modulatory effects, eg on plasticity, which must be defined elsewhere).
   :throws StructuralException: if weight matrix dimensionality is incorrect
   :return: Termination that was added

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.addTermination(java.lang.String,float[][],float,boolean)`

addPESTermination
^^^^^^^^^^^^^^^^^

.. java:method:: public synchronized Termination addPESTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: NEFEnsembleImpl

   :param name: Unique name for the Termination (in the scope of this Node)
   :param weights: Each row is used as a 1 by m matrix of weights in a new termination on the nth expandable node
   :param tauPSC: Time constant with which incoming signals are filtered. (All Terminations have this property, but it may have slightly different interpretations per implementation.)
   :param modulatory: If true, inputs to the Termination are not summed with other inputs (they only have modulatory effects, eg on plasticity, which must be defined elsewhere).
   :throws StructuralException: if weight matrix dimensionality is incorrect
   :return: Termination that was added

   **See also:** :java:ref:`ca.nengo.model.ExpandableNode.addTermination(java.lang.String,float[][],float,boolean)`

addPreLearnTermination
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public synchronized Termination addPreLearnTermination(String name, float[][] weights, float tauPSC, boolean modulatory) throws StructuralException
   :outertype: NEFEnsembleImpl

clone
^^^^^

.. java:method:: @Override public NEFEnsembleImpl clone() throws CloneNotSupportedException
   :outertype: NEFEnsembleImpl

fixMode
^^^^^^^

.. java:method:: public void fixMode()
   :outertype: NEFEnsembleImpl

   When this method is called, the mode of this node is fixed and cannot be changed by subsequent setMode(...) calls.

fixMode
^^^^^^^

.. java:method:: public void fixMode(SimulationMode[] modes)
   :outertype: NEFEnsembleImpl

   Set the allowed simulation modes.

getConstantOutput
^^^^^^^^^^^^^^^^^

.. java:method:: protected float[] getConstantOutput(int nodeIndex, float[][] evalPoints, String origin) throws StructuralException, SimulationException
   :outertype: NEFEnsembleImpl

   :param nodeIndex: Index of Node for which to find output at various inputs
   :param evalPoints: Vector points at which to find output (each one must have same dimension as encoder)
   :param origin: Name of Origin from which to collect output
   :throws SimulationException: If the Node does not have an Origin with the given name
   :throws StructuralException: If CONSTANT_RATE is not supported by the given Node
   :return: Output of indexed Node at each evaluation point

getConstantOutputs
^^^^^^^^^^^^^^^^^^

.. java:method:: protected float[][] getConstantOutputs(float[][] evalPoints, String origin) throws StructuralException
   :outertype: NEFEnsembleImpl

   :param evalPoints: Vector points at which to find output (each one must have same dimension as encoder)
   :param origin: Name of Origin from which to collect output for each Node
   :throws StructuralException: If CONSTANT_RATE is not supported by any Node
   :return: Output of each Node at each evaluation point (1st dimension corresponds to Node)

getDecodingApproximator
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public LinearApproximator getDecodingApproximator(String nodeName)
   :outertype: NEFEnsembleImpl

getDimension
^^^^^^^^^^^^

.. java:method:: @Override public int getDimension()
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.getDimension()`

getDirectModeDynamics
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public DynamicalSystem getDirectModeDynamics()
   :outertype: NEFEnsembleImpl

   :return: Dynamics that apply in direct mode

getDirectModeIntegrator
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Integrator getDirectModeIntegrator()
   :outertype: NEFEnsembleImpl

   :return: Integrator used in direct mode

getEncoders
^^^^^^^^^^^

.. java:method:: public float[][] getEncoders()
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.getEncoders()`

getEnsembleFactory
^^^^^^^^^^^^^^^^^^

.. java:method:: public NEFEnsembleFactory getEnsembleFactory()
   :outertype: NEFEnsembleImpl

getEvalPoints
^^^^^^^^^^^^^

.. java:method:: public float[][] getEvalPoints()
   :outertype: NEFEnsembleImpl

   :return: a copy of the evaluation points

getNeuronCount
^^^^^^^^^^^^^^

.. java:method:: public int getNeuronCount()
   :outertype: NEFEnsembleImpl

   :return: number of neurons (same as getNodeCount)

getNeurons
^^^^^^^^^^

.. java:method:: public int getNeurons()
   :outertype: NEFEnsembleImpl

   :return: number of neurons

getNodeCount
^^^^^^^^^^^^

.. java:method:: public int getNodeCount()
   :outertype: NEFEnsembleImpl

getRadialInput
^^^^^^^^^^^^^^

.. java:method:: public float getRadialInput(float[] state, int node)
   :outertype: NEFEnsembleImpl

   :param state: State vector
   :param node: Node number
   :return: Radial input to the given node

getRadii
^^^^^^^^

.. java:method:: public float[] getRadii()
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.getRadii()`

getReuseApproximators
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean getReuseApproximators()
   :outertype: NEFEnsembleImpl

   :return: True if LinearApproximators for a Node Origin are re-used for decoding multiple decoded Origins.

getSignalOutput
^^^^^^^^^^^^^^^

.. java:method:: protected float[][] getSignalOutput(int nodeIndex, TimeSeries[] evalSignals, String origin) throws StructuralException, SimulationException
   :outertype: NEFEnsembleImpl

   Similar to getConstantOutput, but uses a time series as input to each neuron rather than a single point.

   :param nodeIndex: Index of Node for which to find output at various inputs
   :param evalPoints: Signals over which to evaluate outputs. Each signal can have dimension equal to the number of nodes in the population (each dimension is the input to one node), or dimension equal to the dimension of this population (a single input for the whole population).
   :param origin: Name of Origin from which to collect output
   :throws SimulationException: If the Node does not have an Origin with the given name
   :throws StructuralException: If RATE is not supported by the given Node
   :return: Output of indexed Node over each evaluation signal.

getSignalOutputs
^^^^^^^^^^^^^^^^

.. java:method:: protected float[][][] getSignalOutputs(TimeSeries[] evalSignals, String origin) throws StructuralException
   :outertype: NEFEnsembleImpl

   Similar to getConstantOutputs, but uses a time series as input to each neuron rather than a single point.

   :param evalPoints: Signals over which to evaluate outputs. Each signal can have dimension equal to the number of nodes in the population (each dimension is the input to one node), or dimension equal to the dimension of this population (a single input for the whole population).
   :param origin: Name of Origin from which to collect output for each Node
   :throws StructuralException: If RATE is not supported by any Node
   :return: Output of each Node over each evaluation signal (1st dimension corresponds to Node, 2nd to signal, 3rd to time)

getStaticNeuronData
^^^^^^^^^^^^^^^^^^^

.. java:method:: public float[] getStaticNeuronData()
   :outertype: NEFEnsembleImpl

   Used to get static neuron data (data that doesn't change each step) and give it to the GPU. Data is returned in an array. neuronData[0] = numNeurons neuronData[1] = tauRC neuronData[2] = tauRef neuronData[3] = tauPSC neuronData[4] = maxTimeStep neuronData[5 ... 4 + numNeurons] = bias for each neuron neuronData[5 + numNeurons ... 4 + 2 * numNeurons] = scale for each neuron

   :return: [numNeurons, tauRC, taurRef, tauPSC, maxTimeStep, bias*, scale*]

getUseGPU
^^^^^^^^^

.. java:method:: public boolean getUseGPU()
   :outertype: NEFEnsembleImpl

   :return: Using GPU?

killNeurons
^^^^^^^^^^^

.. java:method:: public void killNeurons(float killrate, boolean saveRelays)
   :outertype: NEFEnsembleImpl

   Stops a given percentage of neurons in this population from firing.

   :param killrate: the percentage of neurons to stop firing
   :param saveRelays: if true, do nothing if there is only one node in this population

listStates
^^^^^^^^^^

.. java:method:: @Override public Properties listStates()
   :outertype: NEFEnsembleImpl

releaseMemory
^^^^^^^^^^^^^

.. java:method:: public void releaseMemory()
   :outertype: NEFEnsembleImpl

   Releases any memory that can be freed. Should be called after all origins are created for this ensemble

reset
^^^^^

.. java:method:: @Override public void reset(boolean randomize)
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: @Override public void run(float startTime, float endTime) throws SimulationException
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Ensemble.run(float,float)`

setDirectModeDynamics
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDirectModeDynamics(DynamicalSystem dynamics)
   :outertype: NEFEnsembleImpl

   :param dynamics: DynamicalSystem that models internal neuron dynamics at the ensemble level, when the ensemble runs in direct mode. The input and output dimensions must equal the dimension of the ensemble.

setDirectModeIntegrator
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDirectModeIntegrator(Integrator integrator)
   :outertype: NEFEnsembleImpl

   :param integrator: Integrator to use in direct mode

setEncoders
^^^^^^^^^^^

.. java:method:: public void setEncoders(float[][] encoders)
   :outertype: NEFEnsembleImpl

   :param encoders: New encoding vectors (row per Node)

setEnsembleFactory
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setEnsembleFactory(NEFEnsembleFactory factory)
   :outertype: NEFEnsembleImpl

setEvalPoints
^^^^^^^^^^^^^

.. java:method:: public void setEvalPoints(float[][] evalPoints)
   :outertype: NEFEnsembleImpl

   Note: by-products of decoding are sometimes cached, so if these are changed it may be necessary to call setReuseApproximators(false) for the change to take effect.

   :param evalPoints: Points in the encoded space at which node outputs are evaluated for establishing new DecodedOrigins.

setMode
^^^^^^^

.. java:method:: @Override public void setMode(SimulationMode mode)
   :outertype: NEFEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Ensemble.setMode(ca.nengo.model.SimulationMode)`

setNeurons
^^^^^^^^^^

.. java:method:: public void setNeurons(int count) throws StructuralException
   :outertype: NEFEnsembleImpl

   TODO: figure out why I have to add these so that it will show up in the Configure menu (nodeCount doens't appear for some reason)

   :param count: number of desired neurons
   :throws StructuralException: if factory doesn't exist or can't add that many

setNodeCount
^^^^^^^^^^^^

.. java:method:: public synchronized void setNodeCount(int n) throws StructuralException
   :outertype: NEFEnsembleImpl

setRadii
^^^^^^^^

.. java:method:: public void setRadii(float[] radii) throws StructuralException
   :outertype: NEFEnsembleImpl

   :param radii: A list of radii of encoded area along each dimension; uniform radius along each dimension can be specified with a list of length 1
   :throws StructuralException: if getConstantOutputs throws exception

setReuseApproximators
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setReuseApproximators(boolean reuse)
   :outertype: NEFEnsembleImpl

   :param reuse: True if LinearApproximators for a Node Origin are re-used for decoding multiple decoded Origins.

setUseGPU
^^^^^^^^^

.. java:method:: public void setUseGPU(boolean use)
   :outertype: NEFEnsembleImpl

   :param use: Use GPU?

toScript
^^^^^^^^

.. java:method:: @Override public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: NEFEnsembleImpl

