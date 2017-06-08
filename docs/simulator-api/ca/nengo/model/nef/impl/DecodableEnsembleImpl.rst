.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util Map

.. java:import:: java.util Properties

.. java:import:: org.apache.log4j Logger

.. java:import:: Jama Matrix

.. java:import:: ca.nengo.dynamics LinearSystem

.. java:import:: ca.nengo.dynamics.impl CanonicalModel

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.dynamics.impl LTISystem

.. java:import:: ca.nengo.dynamics.impl SimpleLTISystem

.. java:import:: ca.nengo.math ApproximatorFactory

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math LinearApproximator

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.math.impl TimeSeriesFunction

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.nef DecodableEnsemble

.. java:import:: ca.nengo.model.plasticity.impl PlasticEnsembleImpl

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

DecodableEnsembleImpl
=====================

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class DecodableEnsembleImpl extends PlasticEnsembleImpl implements DecodableEnsemble

   Default implementation of DecodableEnsemble.

   :author: Bryan Tripp

Fields
------
myDecodedOrigins
^^^^^^^^^^^^^^^^

.. java:field:: protected Map<String, DecodedOrigin> myDecodedOrigins
   :outertype: DecodableEnsembleImpl

myDecodedTerminations
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: protected Map<String, DecodedTermination> myDecodedTerminations
   :outertype: DecodableEnsembleImpl

Constructors
------------
DecodableEnsembleImpl
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DecodableEnsembleImpl(String name, Node[] nodes, ApproximatorFactory factory) throws StructuralException
   :outertype: DecodableEnsembleImpl

   :param name: Name of the Ensemble
   :param nodes: Nodes that make up the Ensemble
   :param factory: Source of LinearApproximators to use in decoding output
   :throws StructuralException: if super constructor fails

Methods
-------
addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, Network environment, Probe probe, float startTime, float endTime) throws StructuralException, SimulationException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.DecodableEnsemble.addDecodedOrigin(java.lang.String,ca.nengo.math.Function[],java.lang.String,ca.nengo.model.Network,ca.nengo.util.Probe,float,float)`

addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, Network environment, Probe probe, Termination termination, float[][] evalPoints, float transientTime) throws StructuralException, SimulationException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.DecodableEnsemble.addDecodedOrigin(java.lang.String,ca.nengo.math.Function[],java.lang.String,ca.nengo.model.Network,ca.nengo.util.Probe,ca.nengo.model.Termination,float[][],float)`

addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, Network environment, Probe probe, Probe state, float startTime, float endTime, float tau) throws StructuralException, SimulationException
   :outertype: DecodableEnsembleImpl

   Lloyd Elliot's decodable origin for decoding band-limited noise using a psc optimized decoder

   :param name: Name of decoding
   :param functions: 1D Functions of time which represent the meaning of the Ensemble output when it runs in the Network provided (see environment arg)
   :param nodeOrigin: The name of the Node-level Origin to decode
   :param environment: A Network in which the Ensemble runs (may include inputs, feedback, etc)
   :param probe: A Probe that is connected to the named Node-level Origin
   :param state: Another probe?
   :param startTime: Simulation time at which to start
   :param endTime: Simulation time at which to finish
   :param tau: Time constant
   :throws SimulationException: if environment can't run
   :throws StructuralException: if origin name is taken
   :return: The added Origin

addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addDecodedTermination(String name, float[][] matrix, float tauPSC, boolean isModulatory) throws StructuralException
   :outertype: DecodableEnsembleImpl

   :param name: Unique name for this Termination (in the scope of this Ensemble)
   :param matrix: Transformation matrix which defines a linear map on incoming information, onto the space of vectors that can be represented by this NEFEnsemble. The first dimension is taken as matrix rows, and must have the same length as the Origin that will be connected to this Termination. The second dimension is taken as matrix columns, and must have the same length as the encoders of this NEFEnsemble. TODO: this is transposed?
   :param tauPSC: Time constant of post-synaptic current decay (all Terminations have this property but it may have slightly different interpretations depending other properties of the Termination).
   :param isModulatory: If true, inputs to this Termination do not drive Nodes in the Ensemble directly but may have modulatory influences (eg related to plasticity). If false, the transformation matrix output dimension must match the dimension of this Ensemble.
   :throws StructuralException: if termination name is taken
   :return: Added Termination

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.addDecodedTermination(java.lang.String,float[][],float,boolean)`

addDecodedTermination
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Termination addDecodedTermination(String name, float[][] matrix, float[] tfNumerator, float[] tfDenominator, float passthrough, boolean isModulatory) throws StructuralException
   :outertype: DecodableEnsembleImpl

   :param name: Unique name for this Termination (in the scope of this Ensemble)
   :param matrix: Transformation matrix which defines a linear map on incoming information, onto the space of vectors that can be represented by this NEFEnsemble. The first dimension is taken as matrix rows, and must have the same length as the Origin that will be connected to this Termination. The second dimension is taken as matrix columns, and must have the same length as the encoders of this NEFEnsemble. TODO: this is transposed?
   :param tfNumerator: Coefficients of transfer function numerator (see CanonicalModel.getRealization(...) for details)
   :param tfDenominator: Coefficients of transfer function denominator
   :param passthrough: How much should pass through?
   :param isModulatory: Is the termination modulatory?
   :throws StructuralException: if termination name is taken
   :return: The added Termination

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.addDecodedTermination(java.lang.String,float[][],float[],float[],float,boolean)`

clone
^^^^^

.. java:method:: @Override public DecodableEnsembleImpl clone() throws CloneNotSupportedException
   :outertype: DecodableEnsembleImpl

doneOrigins
^^^^^^^^^^^

.. java:method:: public void doneOrigins()
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.DecodableEnsemble.doneOrigins()`

getApproximatorFactory
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public ApproximatorFactory getApproximatorFactory()
   :outertype: DecodableEnsembleImpl

   :return: The source of LinearApproximators for this ensemble (used to find linear decoding vectors).

getDecodedOrigins
^^^^^^^^^^^^^^^^^

.. java:method:: public DecodedOrigin[] getDecodedOrigins()
   :outertype: DecodableEnsembleImpl

   Used to get decoded origins to give to GPU.

   :return: All DecodedOrigins

getDecodedTerminations
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public DecodedTermination[] getDecodedTerminations()
   :outertype: DecodableEnsembleImpl

   Used to get decoded terminations to give to GPU.

   :return: all DecodedTerminations

getHistory
^^^^^^^^^^

.. java:method:: @Override public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getOrigin
^^^^^^^^^

.. java:method:: @Override public Origin getOrigin(String name) throws StructuralException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: @Override public Origin[] getOrigins()
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getOrigins()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: @Override public Termination getTermination(String name) throws StructuralException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: @Override public Termination[] getTerminations()
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Ensemble.getTerminations()`

listStates
^^^^^^^^^^

.. java:method:: @Override public Properties listStates()
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

removeDecodedOrigin
^^^^^^^^^^^^^^^^^^^

.. java:method:: public DecodedOrigin removeDecodedOrigin(String name) throws StructuralException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.removeDecodedTermination(java.lang.String)`

removeDecodedTermination
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public DecodedTermination removeDecodedTermination(String name) throws StructuralException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.nef.NEFEnsemble.removeDecodedTermination(java.lang.String)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: DecodableEnsembleImpl

run
^^^

.. java:method:: @Override public void run(float startTime, float endTime) throws SimulationException
   :outertype: DecodableEnsembleImpl

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setTime
^^^^^^^

.. java:method:: public void setTime(float time)
   :outertype: DecodableEnsembleImpl

   Allows subclasses to set the simulation time, which is used to support Probeable. This is normally set in the run() method. Subclasses that override run() without calling it should set the time.

   :param time: Simulation time

stopProbing
^^^^^^^^^^^

.. java:method:: public void stopProbing(String stateName)
   :outertype: DecodableEnsembleImpl
