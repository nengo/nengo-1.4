.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFNode

.. java:import:: ca.nengo.util MU

NEFUtil
=======

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class NEFUtil

   Utility methods for related to Neural Engineering Framework.

   :author: Bryan Tripp

Methods
-------
getOutput
^^^^^^^^^

.. java:method:: public static float[][] getOutput(DecodedOrigin origin, float[][] input, SimulationMode mode)
   :outertype: NEFUtil

   Calculates an input-output mapping for an ensemble.

   :param origin: The origin from which to take the output (must belong to an NEFEnsemble)
   :param input: Set of inputs directly into the ensemble (not through termination mapping/dynamics)
   :param mode: SimulationMode in which to calculate the mapping. If DIRECT or CONSTANT_RATE, each input is treated separately and causes an independent output. Otherwise inputs are applied at 1ms time steps in a simulation, and neuron states are maintained across steps.
   :return: Outputs from the given Origin for given inputs

