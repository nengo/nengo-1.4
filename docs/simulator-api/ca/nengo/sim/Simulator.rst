.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util VisiblyMutable

Simulator
=========

.. java:package:: ca.nengo.sim
   :noindex:

.. java:type:: public interface Simulator extends VisiblyMutable, Cloneable

   Runs simulations of a Network.

   :author: Bryan Tripp

Methods
-------
addProbe
^^^^^^^^

.. java:method:: public Probe addProbe(String nodeName, String state, boolean record) throws SimulationException
   :outertype: Simulator

   :param nodeName: Name of a Probeable Node from which state is to be probed
   :param state: The name of the state variable to probe
   :param record: Probe retains history if true
   :throws SimulationException: if the referenced Node can not be found, or is not Probeable, or does not have the specified state variable
   :return: A Probe connected to the specified Node

addProbe
^^^^^^^^

.. java:method:: public Probe addProbe(String ensembleName, int neuronIndex, String state, boolean record) throws SimulationException
   :outertype: Simulator

   :param ensembleName: Name of Ensemble containing a Probeable Neuron from which state is to be probed
   :param neuronIndex: Index of the Neuron (from 0) within the specified Ensemble
   :param state: The name of the state variable to probe
   :param record: Probe retains history if true
   :throws SimulationException: if the referenced Neuron can not be found, or is not Probeable, or does not have the specified state variable
   :return: A Probe connected to the specified Neuron

addProbe
^^^^^^^^

.. java:method:: public Probe addProbe(String ensembleName, Probeable target, String state, boolean record) throws SimulationException
   :outertype: Simulator

   :param ensembleName: Name of Ensemble the target belongs to. Null, if the target is a top-level node
   :param target: Probeable target
   :param state: The name of the state variable to probe
   :param record: Probe retains history if true
   :throws SimulationException: if the referenced Neuron can not be found, or is not Probeable, or does not have the specified state variable
   :return: A Probe connected to the specified Neuron

addSimulatorListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addSimulatorListener(SimulatorListener listener)
   :outertype: Simulator

   :param listener: A Simulator listener to be added

clone
^^^^^

.. java:method:: public Simulator clone() throws CloneNotSupportedException
   :outertype: Simulator

   :throws CloneNotSupportedException:
   :return: An independent copy of the Simulator. The copy has the same type and parameters, but doesn't reference any Network, contain any Probes, or have any SimulatorListeners

getProbes
^^^^^^^^^

.. java:method:: public Probe[] getProbes()
   :outertype: Simulator

   :return: List of Probes that have been added to this Simulator.

initialize
^^^^^^^^^^

.. java:method:: public void initialize(Network network)
   :outertype: Simulator

   Initializes the Simulator with a given Network, after which changes to the Network MAY OR MAY NOT BE IGNORED. This is because the Simulator is free to either run the given Neurons/Ensembles, or to make copies of them and run the copies. (The latter is likely in a clustered implementation.) If you make changes to the Network after initializing a Simulator with it, initialize again. If you want the Network to change somehow mid-simulation (e.g. you want to remove some neurons from an Ensemble to test robustness), these changes should be performed by the Ensembles or Neurons themselves, i.e. they should be an explicit part of the model.

   :param network: Network to set up for simulation

removeProbe
^^^^^^^^^^^

.. java:method:: public void removeProbe(Probe probe) throws SimulationException
   :outertype: Simulator

   :param probe: Probe to be removed
   :throws SimulationException: if the referenced probe cannot be removed

removeSimulatorListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeSimulatorListener(SimulatorListener listener)
   :outertype: Simulator

   :param listener: A Simulator listener to be removed

resetNetwork
^^^^^^^^^^^^

.. java:method:: public void resetNetwork(boolean randomize, boolean saveWeights)
   :outertype: Simulator

   Resets all Nodes in the simulated Network.

   :param randomize: True indicates reset to random initial condition (see Resettable.reset(boolean)).
   :param saveWeights: True indicates that the weights on LinearExponentialTerminations should be saved rather than reset

resetProbes
^^^^^^^^^^^

.. java:method:: public void resetProbes()
   :outertype: Simulator

   Resets all probes in the network, recursively including subnetworks.

run
^^^

.. java:method:: public void run(float startTime, float endTime, float stepSize) throws SimulationException
   :outertype: Simulator

   Runs the Network for the given time range. The states of all components of the Network are assumed to be consistent with the given start time. So, you could reset to the t=0 state, and then immediately start running from t=100, but the results may not make sense.

   :param startTime: Simulation time at which running starts
   :param endTime: Simulation time at which running stops
   :param stepSize: Length of time step at which the Network is run. This determines the frequency with which outputs are passed between Ensembles, but individual Neurons may run with different and/or variable time steps.
   :throws SimulationException: if a problem is encountered while trying to run

run
^^^

.. java:method:: public void run(float startTime, float endTime, float stepSize, boolean topLevel) throws SimulationException
   :outertype: Simulator

   Runs the Network for the given time range. The states of all components of the Network are assumed to be consistent with the given start time. So, you could reset to the t=0 state, and then immediately start running from t=100, but the results may not make sense.

   :param startTime: Simulation time at which running starts
   :param endTime: Simulation time at which running stops
   :param stepSize: Length of time step at which the Network is run. This determines the frequency with which outputs are passed between Ensembles, but individual Neurons may run with different and/or variable time steps.
   :param topLevel: true if the network being run is the top level network, false if it is a subnetwork
   :throws SimulationException: if a problem is encountered while trying to run

