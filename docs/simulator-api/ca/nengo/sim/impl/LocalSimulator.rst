.. java:import:: java.lang System

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.impl SocketUDPNode

.. java:import:: ca.nengo.model.plasticity.impl PlasticEnsembleTermination

.. java:import:: ca.nengo.sim Simulator

.. java:import:: ca.nengo.sim SimulatorEvent

.. java:import:: ca.nengo.sim SimulatorListener

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util ThreadTask

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl NodeThreadPool

.. java:import:: ca.nengo.util.impl ProbeImpl

LocalSimulator
==============

.. java:package:: ca.nengo.sim.impl
   :noindex:

.. java:type:: public class LocalSimulator implements Simulator, java.io.Serializable

   A Simulator that runs locally (ie in the Java Virtual Machine in which it is called). TODO: test

   :author: Bryan Tripp

Constructors
------------
LocalSimulator
^^^^^^^^^^^^^^

.. java:constructor:: public LocalSimulator()
   :outertype: LocalSimulator

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

addProbe
^^^^^^^^

.. java:method:: public Probe addProbe(String nodeName, String state, boolean record) throws SimulationException
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.addProbe(java.lang.String,java.lang.String,
   boolean)`

addProbe
^^^^^^^^

.. java:method:: public Probe addProbe(String ensembleName, int neuronIndex, String state, boolean record) throws SimulationException
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.addProbe(java.lang.String,int,
   java.lang.String,boolean)`

addProbe
^^^^^^^^

.. java:method:: public Probe addProbe(String ensembleName, Probeable target, String state, boolean record) throws SimulationException
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.addProbe(java.lang.String,int,
   java.lang.String,boolean)`

addSimulatorListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addSimulatorListener(SimulatorListener listener)
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.addSimulatorListener(ca.nengo.sim.SimulatorListener)`

clone
^^^^^

.. java:method:: @Override public Simulator clone() throws CloneNotSupportedException
   :outertype: LocalSimulator

endRun
^^^^^^

.. java:method:: public void endRun() throws SimulationException
   :outertype: LocalSimulator

fireSimulatorEvent
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void fireSimulatorEvent(SimulatorEvent event)
   :outertype: LocalSimulator

   :param event:

getNodeThreadPool
^^^^^^^^^^^^^^^^^

.. java:method:: public NodeThreadPool getNodeThreadPool()
   :outertype: LocalSimulator

getProbes
^^^^^^^^^

.. java:method:: public Probe[] getProbes()
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.getProbes()`

initRun
^^^^^^^

.. java:method:: public void initRun(boolean interactive) throws SimulationException
   :outertype: LocalSimulator

   Setup the run. Interactive specifies whether it is an interactive run or not.

initialize
^^^^^^^^^^

.. java:method:: public synchronized void initialize(Network network)
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.initialize(ca.nengo.model.Network)`

makeNodeThreadPool
^^^^^^^^^^^^^^^^^^

.. java:method:: public void makeNodeThreadPool(boolean interactive)
   :outertype: LocalSimulator

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

removeProbe
^^^^^^^^^^^

.. java:method:: public void removeProbe(Probe probe) throws SimulationException
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.removeProbe(ca.nengo.util.Probe)`

removeSimulatorListener
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeSimulatorListener(SimulatorListener listener)
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.removeSimulatorListener(ca.nengo.sim.SimulatorListener)`

resetNetwork
^^^^^^^^^^^^

.. java:method:: public synchronized void resetNetwork(boolean randomize, boolean saveWeights)
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.resetNetwork(boolean,boolean)`

resetProbes
^^^^^^^^^^^

.. java:method:: public void resetProbes()
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.resetProbes()`

run
^^^

.. java:method:: public synchronized void run(float startTime, float endTime, float stepSize) throws SimulationException
   :outertype: LocalSimulator

   **See also:** :java:ref:`ca.nengo.sim.Simulator.run(float,float,float)`

run
^^^

.. java:method:: public synchronized void run(float startTime, float endTime, float stepSize, boolean topLevel) throws SimulationException
   :outertype: LocalSimulator

   Run function with option to display (or not) the progress in the console

setDisplayProgress
^^^^^^^^^^^^^^^^^^

.. java:method:: public void setDisplayProgress(boolean display)
   :outertype: LocalSimulator

step
^^^^

.. java:method:: public void step(float startTime, float endTime) throws SimulationException
   :outertype: LocalSimulator

