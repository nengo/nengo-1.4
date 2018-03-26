.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.util.impl ProbeTask

Probe
=====

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface Probe

   Reads state variables from Probeable objects (eg membrane potential from a Neuron). Collected data can be displayed during a simluation or kept for plotting afterwards.

   :author: Bryan Tripp

Methods
-------
collect
^^^^^^^

.. java:method:: public void collect(float time)
   :outertype: Probe

   Processes new data. To be called after every Network time step.

connect
^^^^^^^

.. java:method:: public void connect(String ensembleName, Probeable target, String stateName, boolean record) throws SimulationException
   :outertype: Probe

   :param ensembleName: Name of the Ensemble the target object belongs to. Null, if the target is a top-level node.
   :param target: The object about which state history is to be collected
   :param stateName: The name of the state variable to collect
   :param record: If true, getData() returns history since last connect() or reset(), otherwise getData() returns most recent sample
   :throws SimulationException: if the given target does not have the given state

connect
^^^^^^^

.. java:method:: public void connect(Probeable target, String stateName, boolean record) throws SimulationException
   :outertype: Probe

   :param target: The object about which state history is to be collected
   :param stateName: The name of the state variable to collect
   :param record: If true, getData() returns history since last connect() or reset(), otherwise getData() returns most recent sample
   :throws SimulationException: if the given target does not have the given state

getData
^^^^^^^

.. java:method:: public TimeSeries getData()
   :outertype: Probe

   :return: All collected data since last reset()

getEnsembleName
^^^^^^^^^^^^^^^

.. java:method:: public String getEnsembleName()
   :outertype: Probe

   :return: The name of the Ensemble the target the Probe is attached to is in. Null if it's not in one

getProbeTask
^^^^^^^^^^^^

.. java:method:: public ProbeTask getProbeTask()
   :outertype: Probe

   :return: The probe task that is runs this probe.

getStateName
^^^^^^^^^^^^

.. java:method:: public String getStateName()
   :outertype: Probe

   :return: The name of the state variable to collect

getTarget
^^^^^^^^^

.. java:method:: public Probeable getTarget()
   :outertype: Probe

   :return: The object about which state history is to be collected

isInEnsemble
^^^^^^^^^^^^

.. java:method:: public boolean isInEnsemble()
   :outertype: Probe

   :return: Whether the target the node is attached to is inside an Ensemble

reset
^^^^^

.. java:method:: public void reset()
   :outertype: Probe

   Clears collected data.

setSamplingRate
^^^^^^^^^^^^^^^

.. java:method:: public void setSamplingRate(float rate)
   :outertype: Probe

   :param rate: Rate in samples per second. The default is one sample per network time step, and it is not possible to sample faster than this (specifying a higher sampling rate has no effect).

