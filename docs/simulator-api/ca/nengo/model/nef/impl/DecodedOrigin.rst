.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.config ConfigUtil

.. java:import:: ca.nengo.config Configurable

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.config.impl ConfigurationImpl

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.math.impl FixedSignalFunction

.. java:import:: ca.nengo.math.impl WeightedCostApproximator

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Noise

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.nef DecodableEnsemble

.. java:import:: ca.nengo.model.nef ExpressModel

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.plasticity ShortTermPlastic

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util VectorGenerator

.. java:import:: ca.nengo.util.impl RandomHypersphereVG

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

DecodedOrigin
=============

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class DecodedOrigin implements Origin, Resettable, SimulationMode.ModeConfigurable, Noise.Noisy, Configurable, ShortTermPlastic

   An Origin of functions of the state variables of an NEFEnsemble. TODO: how do units fit in. define in constructor? ignore? TODO: select nodes make up decoded origin

   :author: Bryan Tripp

Constructors
------------
DecodedOrigin
^^^^^^^^^^^^^

.. java:constructor:: public DecodedOrigin(Node node, String name, Node[] nodes, String nodeOrigin, Function[] functions, LinearApproximator approximator) throws StructuralException
   :outertype: DecodedOrigin

   With this constructor, decoding vectors are generated using default settings.

   :param node: The parent Node
   :param name: Name of this Origin
   :param nodes: Nodes that belong to the NEFEnsemble from which this Origin arises
   :param nodeOrigin: Name of the Origin on each given node from which output is to be decoded
   :param functions: Output Functions on the vector that is represented by the NEFEnsemble (one Function per dimension of output). For example if the Origin is to output x1*x2, where the ensemble represents [x1 x1], then one 2D function would be needed in this list. The input dimension of each function must be the same as the dimension of the state vector represented by this ensemble.
   :param approximator: A LinearApproximator that can be used to approximate new functions as a weighted sum of the node outputs.
   :throws StructuralException: if functions do not all have the same input dimension (we don't check against the state dimension at this point)

DecodedOrigin
^^^^^^^^^^^^^

.. java:constructor:: public DecodedOrigin(Node node, String name, Node[] nodes, String nodeOrigin, Function[] functions, float[][] decoders, int dummy) throws StructuralException
   :outertype: DecodedOrigin

   With this constructor decoding vectors are specified by the caller.

   :param node: The parent Node
   :param name: As in other constructor
   :param nodes: As in other constructor
   :param nodeOrigin: Name of the Origin on each given node from which output is to be decoded
   :param functions: As in other constructor
   :param decoders: Decoding vectors which are scaled by the main output of each Node, and then summed, to estimate the same function of the ensembles state vector that is defined by the 'functions' arg. The 'functions' arg is still needed, because in DIRECT SimulationMode, these functions are used directly. The 'decoders' arg allows the caller to provide decoders that are generated with non-default methods or parameters (eg an unusual number of singular values). Must be a matrix with one row per Node and one column per function.
   :throws StructuralException: If dimensions.length != neurons.length, decoders is not a matrix (ie all elements with same length), or if the number of columns in decoders is not equal to the number of functions

DecodedOrigin
^^^^^^^^^^^^^

.. java:constructor:: public DecodedOrigin(Node node, String name, Node[] nodes, String nodeOrigin, TimeSeries targetSignal, LinearApproximator approximator) throws StructuralException
   :outertype: DecodedOrigin

   With this constructor the target is a signal over time rather than a function.

   :param node: The parent Node
   :param name: As in other constructor
   :param nodes: As in other constructor
   :param nodeOrigin: Name of the Origin on each given node from which output is to be decoded
   :param targetSignal: Signal over time that this origin should produce.
   :param approximator: A LinearApproximator that can be used to approximate new signals as a weighted sum of the node outputs.

Methods
-------
clone
^^^^^

.. java:method:: @Override public DecodedOrigin clone() throws CloneNotSupportedException
   :outertype: DecodedOrigin

clone
^^^^^

.. java:method:: public DecodedOrigin clone(Node node) throws CloneNotSupportedException
   :outertype: DecodedOrigin

getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.config.Configurable.getConfiguration()`

getDecoders
^^^^^^^^^^^

.. java:method:: public float[][] getDecoders()
   :outertype: DecodedOrigin

   :return: Decoding vectors for each Node

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getDimensions()`

getError
^^^^^^^^

.. java:method:: public float[] getError()
   :outertype: DecodedOrigin

   :return: Mean-squared error of this origin over 500 randomly selected points

getError
^^^^^^^^

.. java:method:: public float[] getError(int samples)
   :outertype: DecodedOrigin

   :param samples: The number of input vectors the error is sampled over
   :return: Mean-squared error of this origin over randomly selected points

getExpressModel
^^^^^^^^^^^^^^^

.. java:method:: public ExpressModel getExpressModel()
   :outertype: DecodedOrigin

   :return: Simplified model of deviations from DIRECT mode that are associated with spiking simulations

getFunctions
^^^^^^^^^^^^

.. java:method:: public Function[] getFunctions()
   :outertype: DecodedOrigin

   :return: List of Functions approximated by this DecodedOrigin

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: DecodedOrigin

   :return: The mode in which the Ensemble is currently running.

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getNode()`

getNodeOrigin
^^^^^^^^^^^^^

.. java:method:: protected String getNodeOrigin()
   :outertype: DecodedOrigin

   :return: Name of Node-level Origin on which this DecodedOrigin is based

getNoise
^^^^^^^^

.. java:method:: public Noise getNoise()
   :outertype: DecodedOrigin

   :return: Noise with which output of this Origin is corrupted

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: DecodedOrigin

getSTPDynamics
^^^^^^^^^^^^^^

.. java:method:: public DynamicalSystem getSTPDynamics()
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.plasticity.ShortTermPlastic.getSTPDynamics()`

getSTPDynamics
^^^^^^^^^^^^^^

.. java:method:: public DynamicalSystem getSTPDynamics(int i)
   :outertype: DecodedOrigin

   Provides access to copy of dynamics for an individual node, to allow node-by-node parameterization.

   :param i: Node number
   :return: Dynamics of short-term plasticity for the specified node

getSTPHistory
^^^^^^^^^^^^^

.. java:method:: protected TimeSeries getSTPHistory()
   :outertype: DecodedOrigin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues() throws SimulationException
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getValues()`

rebuildDecoder
^^^^^^^^^^^^^^

.. java:method:: public void rebuildDecoder(LinearApproximator approximator)
   :outertype: DecodedOrigin

   Recalculates the decoders

   :param approximator: approximator?

redefineNodes
^^^^^^^^^^^^^

.. java:method:: public void redefineNodes(Node[] nodes, LinearApproximator approximator)
   :outertype: DecodedOrigin

   Changes the set of nodes and recalculates the decoders

   :param nodes: Nodes to replace existing nodes
   :param approximator: approximator?

rescaleDecoders
^^^^^^^^^^^^^^^

.. java:method:: public void rescaleDecoders(float[] scale)
   :outertype: DecodedOrigin

   Rescales the decoders. Useful if the radius changes but you don't want to regenerate the decoders.

   :param scale: vector to multiply each decoder by

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float[] state, float startTime, float endTime) throws SimulationException
   :outertype: DecodedOrigin

   Must be called at each time step after Nodes are run and before getValues().

   :param state: Idealized state (as defined by inputs) which can be fed into (idealized) functions that make up the Origin, when it is running in DIRECT mode. This is not used in other modes, and can be null.
   :param startTime: simulation time of timestep onset
   :param endTime: simulation time of timestep end
   :throws SimulationException: If the given state is not of the expected dimension (ie the input dimension of the functions provided in the constructor)

setDecoders
^^^^^^^^^^^

.. java:method:: public void setDecoders(float[][] decoders)
   :outertype: DecodedOrigin

   :param decoders: New decoding vectors (row per Node)

setExpressModel
^^^^^^^^^^^^^^^

.. java:method:: public void setExpressModel(ExpressModel em)
   :outertype: DecodedOrigin

   :param em: Simplified model of deviations from DIRECT mode that are associated with spiking simulations

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: DecodedOrigin

   :param mode: Requested simulation mode

setNoise
^^^^^^^^

.. java:method:: public void setNoise(Noise noise)
   :outertype: DecodedOrigin

   :param noise: New output noise model (defaults to no noise)

setNoises
^^^^^^^^^

.. java:method:: public void setNoises(Noise[] noises) throws SimulationException
   :outertype: DecodedOrigin

   :param noises: New output noise model for each dimension of output
   :throws SimulationException:

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: DecodedOrigin

setSTPDynamics
^^^^^^^^^^^^^^

.. java:method:: public void setSTPDynamics(DynamicalSystem dynamics)
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.plasticity.ShortTermPlastic.setSTPDynamics(ca.nengo.dynamics.DynamicalSystem)`

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput val)
   :outertype: DecodedOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.setValues()`

setValues
^^^^^^^^^

.. java:method:: public void setValues(RealOutput ro)
   :outertype: DecodedOrigin

   :param ro: Values to be set

