.. java:import:: java.util Properties

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics LinearSystem

.. java:import:: ca.nengo.dynamics.impl CanonicalModel

.. java:import:: ca.nengo.dynamics.impl LTISystem

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

DecodedTermination
==================

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class DecodedTermination implements Termination, Resettable, Probeable

   A Termination of decoded state vectors onto an NEFEnsemble. A DecodedTermination performs a linear transformation on incoming vectors, mapping them into the space of the NEFEnsemble to which this Termination belongs. A DecodedTermination also applies linear PSC dynamics (typically exponential decay) to the resulting vector.

   Non-linear dynamics are not allowed at this level. This is because the vector input to an NEFEnsemble only has meaning in terms of the decomposition of synaptic weights into decoding vectors, transformation matrix, and encoding vectors. Linear PSC dynamics actually apply to currents, but if everything is linear we can re-order the dynamics and the encoders for convenience (so that the dynamics seem to operate on the state vectors). In contrast, non-linear dynamics must be modeled within each Neuron, because all inputs to a non-linear dynamical process must be taken into account before the effect of any single input is known.

   :author: Bryan Tripp

Fields
------
OUTPUT
^^^^^^

.. java:field:: public static final String OUTPUT
   :outertype: DecodedTermination

   Name of Probeable output state.

Constructors
------------
DecodedTermination
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DecodedTermination(Node node, String name, float[][] transform, LinearSystem dynamics, Integrator integrator) throws StructuralException
   :outertype: DecodedTermination

   :param node: The parent Node
   :param name: The name of this Termination
   :param transform: A matrix that maps input (which has the dimension of this Termination) onto the state space represented by the NEFEnsemble to which the Termination belongs
   :param dynamics: Post-synaptic current dynamics (single-input single-output). Time-varying dynamics are OK, but non-linear dynamics don't make sense here, because other Terminations may input onto the same neurons.
   :param integrator: Numerical integrator with which to solve dynamics
   :throws StructuralException: If dynamics are not SISO or given transform is not a matrix

Methods
-------
clone
^^^^^

.. java:method:: @Override public DecodedTermination clone() throws CloneNotSupportedException
   :outertype: DecodedTermination

clone
^^^^^

.. java:method:: public DecodedTermination clone(Node node) throws CloneNotSupportedException
   :outertype: DecodedTermination

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getDimensions()`

getDynamics
^^^^^^^^^^^

.. java:method:: public LinearSystem getDynamics()
   :outertype: DecodedTermination

   :return: The dynamics that govern each dimension of this Termination. Changing the properties of the return value will change dynamics of all dimensions, effective next run time.

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getInitialState
^^^^^^^^^^^^^^^

.. java:method:: public float[][] getInitialState()
   :outertype: DecodedTermination

   :return: Initial states of dynamics (one row per output dimension)

getInput
^^^^^^^^

.. java:method:: public RealOutput getInput()
   :outertype: DecodedTermination

   :return: Latest input to Termination (pre transform and dynamics)

getModulatory
^^^^^^^^^^^^^

.. java:method:: public boolean getModulatory()
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getModulatory()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.getNode()`

getOutput
^^^^^^^^^

.. java:method:: public float[] getOutput()
   :outertype: DecodedTermination

   This method should be called after run(...).

   :return: Output of dynamical system -- of interest at end of run(...)

getScaling
^^^^^^^^^^

.. java:method:: public DecodedTermination getScaling()
   :outertype: DecodedTermination

   :return: Termination used for scaling?

getStaticBias
^^^^^^^^^^^^^

.. java:method:: public float[] getStaticBias()
   :outertype: DecodedTermination

   :return: Static bias vector (a copy)

getTau
^^^^^^

.. java:method:: public float getTau()
   :outertype: DecodedTermination

   :return: Slowest time constant of dynamics, if dynamics are LTI, otherwise 0

getTransform
^^^^^^^^^^^^

.. java:method:: public float[][] getTransform()
   :outertype: DecodedTermination

   :return: The matrix that maps input (which has the dimension of this Termination) onto the state space represented by the NEFEnsemble to which the Termination belongs

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: DecodedTermination

   :param startTime: Simulation time at which running is to start
   :param endTime: Simulation time at which running is to end

setDynamics
^^^^^^^^^^^

.. java:method:: public void setDynamics(LinearSystem dynamics)
   :outertype: DecodedTermination

   :param dynamics: New dynamics for each dimension of this Termination (effective immediately). This method uses a clone of the given dynamics.

setInitialState
^^^^^^^^^^^^^^^

.. java:method:: public void setInitialState(float[][] state)
   :outertype: DecodedTermination

   :param state: Initial state of dynamics (dimension of termination output X dimension of dynamics state)

setModulatory
^^^^^^^^^^^^^

.. java:method:: public void setModulatory(boolean modulatory)
   :outertype: DecodedTermination

   **See also:** :java:ref:`ca.nengo.model.Termination.setModulatory(boolean)`

setNode
^^^^^^^

.. java:method:: protected void setNode(Node node)
   :outertype: DecodedTermination

setScaling
^^^^^^^^^^

.. java:method:: public void setScaling(DecodedTermination t)
   :outertype: DecodedTermination

   :param t: Termination to use for scaling?

setStaticBias
^^^^^^^^^^^^^

.. java:method:: public void setStaticBias(float[] bias)
   :outertype: DecodedTermination

   :param bias: Intrinsic bias that is added to inputs to this termination

setTau
^^^^^^

.. java:method:: public void setTau(float tau) throws StructuralException
   :outertype: DecodedTermination

   :param tau: New time constant to replace current slowest time constant of dynamics
   :throws StructuralException: if the dynamics of this Termination are not LTI in controllable canonical form

setTransform
^^^^^^^^^^^^

.. java:method:: public void setTransform(float[][] transform) throws StructuralException
   :outertype: DecodedTermination

   :param transform: New transform
   :throws StructuralException: If the transform is not a matrix or has the wrong size

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: DecodedTermination

   :param values: Only RealOutput is accepted.

   **See also:** :java:ref:`ca.nengo.model.Termination.setValues(ca.nengo.model.InstantaneousOutput)`

