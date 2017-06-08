.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model SimulationException

ThreadTask
==========

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface ThreadTask extends Resettable, Cloneable

   Any task in a Network that can be run independently but belongs to a specific part of the Network. Provides a way for objects in a network that normally run on one thread to run specific parts in multiple threads (eg a Non-Decoded Termination adjusting the weight for every neuron)

   :author: Jonathan Lai

Methods
-------
clone
^^^^^

.. java:method:: public ThreadTask clone() throws CloneNotSupportedException
   :outertype: ThreadTask

   :throws CloneNotSupportedException: if the superclass does not support cloning
   :return: An independent copy of the Task

isFinished
^^^^^^^^^^

.. java:method:: public boolean isFinished()
   :outertype: ThreadTask

   :return: If the task has finished running

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: ThreadTask

   Runs the Task, updating the parent Node as needed

   :param startTime: simulation time at which running starts (s)
   :param endTime: simulation time at which running ends (s)
   :throws SimulationException: if a problem is encountered while trying to run

