.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util TimeSeries

ProbeImpl
=========

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class ProbeImpl implements Probe, java.io.Serializable

   Collects information from \ ``Probeable``\  objects.

   :author: Bryan Tripp

Methods
-------
collect
^^^^^^^

.. java:method:: public void collect(float time)
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.collect(float)`

connect
^^^^^^^

.. java:method:: public void connect(String ensembleName, Probeable target, String stateName, boolean record) throws SimulationException
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.connect(java.lang.String,ca.nengo.model.Probeable,java.lang.String,boolean)`

connect
^^^^^^^

.. java:method:: public void connect(Probeable target, String stateName, boolean record) throws SimulationException
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.connect(Probeable,String,boolean)`

getData
^^^^^^^

.. java:method:: public TimeSeries getData()
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.getData()`

getEnsembleName
^^^^^^^^^^^^^^^

.. java:method:: public String getEnsembleName()
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.getEnsembleName()`

getProbeTask
^^^^^^^^^^^^

.. java:method:: public ProbeTask getProbeTask()
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.getProbeTask()`

getStateName
^^^^^^^^^^^^

.. java:method:: public String getStateName()
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.getStateName()`

getTarget
^^^^^^^^^

.. java:method:: public Probeable getTarget()
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.getTarget()`

isInEnsemble
^^^^^^^^^^^^

.. java:method:: public boolean isInEnsemble()
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.isInEnsemble()`

reset
^^^^^

.. java:method:: public void reset()
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.reset()`

setSamplingRate
^^^^^^^^^^^^^^^

.. java:method:: public void setSamplingRate(float rate)
   :outertype: ProbeImpl

   **See also:** :java:ref:`ca.nengo.util.Probe.setSamplingRate(float)`

