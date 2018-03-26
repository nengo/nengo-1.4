.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.util ThreadTask

.. java:import:: ca.nengo.util.impl NEFGPUInterface

GPUThread
=========

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class GPUThread extends NodeThread

   A thread which uses an NEFGPUInterface to run GPU nodes and projections.

   :author: Eric Crawford

Fields
------
myNEFGPUInterface
^^^^^^^^^^^^^^^^^

.. java:field::  NEFGPUInterface myNEFGPUInterface
   :outertype: GPUThread

Constructors
------------
GPUThread
^^^^^^^^^

.. java:constructor:: public GPUThread(NodeThreadPool nodePool, boolean interactive)
   :outertype: GPUThread

GPUThread
^^^^^^^^^

.. java:constructor:: public GPUThread(NodeThreadPool nodePool)
   :outertype: GPUThread

Methods
-------
getNEFGPUInterface
^^^^^^^^^^^^^^^^^^

.. java:method:: public NEFGPUInterface getNEFGPUInterface()
   :outertype: GPUThread

kill
^^^^

.. java:method:: protected void kill()
   :outertype: GPUThread

runNodes
^^^^^^^^

.. java:method:: protected void runNodes(float startTime, float endTime) throws SimulationException
   :outertype: GPUThread

