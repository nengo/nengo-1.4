.. java:import:: ca.nengo.model SimulationException

ExpressModel
============

.. java:package:: ca.nengo.model.nef
   :noindex:

.. java:type:: public interface ExpressModel

   A computationally efficient model of the difference between a NEFEnsemble's DecodedOrigin output in DIRECT mode and DEFAULT mode (at the level of state variables). This is used in EXPRESS SimulationMode.

   :author: Bryan Tripp

Methods
-------
getOutput
^^^^^^^^^

.. java:method:: public float[] getOutput(float startTime, float[] state, float[] directOutput)
   :outertype: ExpressModel

   :param startTime: Start of simulation time step.
   :param state: The value represented by the associated NEFEnsemble
   :param directOutput: DIRECT mode output values of an Origin
   :return: Modified values that incorporate a high-level model of the effects of spiking neurons.

update
^^^^^^

.. java:method:: public void update() throws SimulationException
   :outertype: ExpressModel

   To be called after a change in radii or decoders.

