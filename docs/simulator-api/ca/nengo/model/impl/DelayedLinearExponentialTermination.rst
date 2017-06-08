.. java:import:: java.util ArrayList

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model Units

DelayedLinearExponentialTermination
===================================

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class DelayedLinearExponentialTermination extends LinearExponentialTermination

   A LinearExponentialTermination where all inputs are delayed by a fixed number of timesteps.

   :author: Daniel Rasmussen

Constructors
------------
DelayedLinearExponentialTermination
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DelayedLinearExponentialTermination(Node node, String name, float[] weights, float tauPSC, int delay)
   :outertype: DelayedLinearExponentialTermination

   :param delay: delay in timesteps between when input arrives at this termination and when it will be processed

   **See also:** :java:ref:`LinearExponentialTermination.LinearExponentialTermination(Node,String,float[],float)`

Methods
-------
setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput values) throws SimulationException
   :outertype: DelayedLinearExponentialTermination

   Adds a value to this termination's queue. That value will not actually be processed until myDelay timesteps have called (we are assuming this function will be called once per timestep).

   **See also:** :java:ref:`LinearExponentialTermination.setValues(InstantaneousOutput)`

