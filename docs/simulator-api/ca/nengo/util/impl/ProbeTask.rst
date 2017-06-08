.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util ThreadTask

ProbeTask
=========

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class ProbeTask implements ThreadTask

   Implementation of a ThreadTask to multithread collection of data by probes.

   :author: Eric Crawford

Constructors
------------
ProbeTask
^^^^^^^^^

.. java:constructor:: public ProbeTask(Probeable parent, Probe probe)
   :outertype: ProbeTask

Methods
-------
clone
^^^^^

.. java:method:: @Override public ProbeTask clone() throws CloneNotSupportedException
   :outertype: ProbeTask

getParent
^^^^^^^^^

.. java:method:: public Probeable getParent()
   :outertype: ProbeTask

isFinished
^^^^^^^^^^

.. java:method:: public boolean isFinished()
   :outertype: ProbeTask

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: ProbeTask

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: ProbeTask

