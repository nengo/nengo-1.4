SimulationMode
==============

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public enum SimulationMode

   A SimulationMode is a way in which a Neuron or Ensemble can be simulated. Different modes trade off between performance and realism. All Neurons and Ensembles must be able to run in DEFAULT mode. For other modes, there is a chain of fallback choices so that if the requested mode is not supported, the Neuron/Ensemble must run in the closest mode that it does support (which may be the DEFAULT mode).

   :author: Bryan Tripp

Enum Constants
--------------
APPROXIMATE
^^^^^^^^^^^

.. java:field:: public static final SimulationMode APPROXIMATE
   :outertype: SimulationMode

   A spiking mode in which some precision is sacrificed for improved performance. For example, a conductance model in APPROXIMATE mode might continue to use the full model for subthreshold operation, but switch to a stereotyped template for spike generation, to avoid the shorter time steps that are typically needed to model spiking.

CONSTANT_RATE
^^^^^^^^^^^^^

.. java:field:: public static final SimulationMode CONSTANT_RATE
   :outertype: SimulationMode

   Outputs that spike by default are expressed as rates that are constant for a given constant input. This mode is useful for fast approximate simulations and also for calculating decoders (see NEFEnsemble). If a Neuron can not run in this mode, then in order to find decoders, simulations must be performed to see how the Neuron responds to various inputs.

DEFAULT
^^^^^^^

.. java:field:: public static final SimulationMode DEFAULT
   :outertype: SimulationMode

   The normal level of detail at which a Neuron/Ensemble runs (all Neuron/Ensembles must support this mode).

DIRECT
^^^^^^

.. java:field:: public static final SimulationMode DIRECT
   :outertype: SimulationMode

   Neurons are not used. Ensembles process represented variables directly rather than approximations based on neural activity.

EXPRESS
^^^^^^^

.. java:field:: public static final SimulationMode EXPRESS
   :outertype: SimulationMode

   Neurons are not used. The latent variables in population activity are simulated. This is the same as DIRECT mode, except that the large-scale effects of the neurons are also simulated by modelling noise and static distortion. This is meant as a computationally inexpensive mode with network behaviour that is as similar as possible to a spiking simulation.

PRECISE
^^^^^^^

.. java:field:: public static final SimulationMode PRECISE
   :outertype: SimulationMode

   A higher level of precision than DEFAULT. The default level should be accurate for most purposes, but this higher level of accuracy can serve as a way to verify that numerical issues are not impacting results (eg error tolerance in a Runge-Kutta integration may be tightened beyond what is deemed necessary). Another way to increase precision, independently of using PRECISE mode, is to simulate with a shorter network time step.

RATE
^^^^

.. java:field:: public static final SimulationMode RATE
   :outertype: SimulationMode

   Outputs that spike by default are instead expressed in terms of firing rates.

