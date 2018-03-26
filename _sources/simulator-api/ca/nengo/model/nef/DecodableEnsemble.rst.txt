.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.nef.impl DecodedTermination

.. java:import:: ca.nengo.model.plasticity PlasticEnsemble

.. java:import:: ca.nengo.util Probe

DecodableEnsemble
=================

.. java:package:: ca.nengo.model.nef
   :noindex:

.. java:type:: public interface DecodableEnsemble extends PlasticEnsemble, Probeable

   An Ensemble that produces output signals that mean something when taken together. This meaning can be decoded, as a scalar or vector, through linear combination of the outputs.

   Note that NEFEnsemble is a paricularly powerful and efficient special case of DecodableEnsemble. However NEFEnsemble makes some assumptions that can be relaxed by using DecodableEnsemble instead:

   ..

   #. It assumes that its Nodes can run in the SimulationMode CONSTANT_RATE
   #. It assumes that activity arises from cosine-tuning to preferred input vectors (all Nodes must be NEFNodes for this reason).

   :author: Bryan Tripp

Methods
-------
addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, Network environment, Probe probe, float startTime, float endTime) throws StructuralException, SimulationException
   :outertype: DecodableEnsemble

   Adds an Origin that corresponds to a decoding of the activities of Nodes in this Ensemble. The decoding is found by running the Ensemble within a Network, and using its output to approximate a vector function of time.

   :param name: Name of decoding
   :param functions: 1D Functions of time which represent the meaning of the Ensemble output when it runs in the Network provided (see environment arg)
   :param nodeOrigin: The name of the Node-level Origin to decode
   :param environment: A Network in which the Ensemble runs (may include inputs, feedback, etc)
   :param probe: A Probe that is connected to the named Node-level Origin
   :param startTime: Simulation time at which to start
   :param endTime: Simulation time at which to finish
   :throws SimulationException: If there is a problem running the simulation
   :throws StructuralException: May arise in instantiating the Origin
   :return: An Origin that approximates the given Functions as a linear combination of output from the given nodeOrigin

addDecodedOrigin
^^^^^^^^^^^^^^^^

.. java:method:: public Origin addDecodedOrigin(String name, Function[] functions, String nodeOrigin, Network environment, Probe probe, Termination termination, float[][] evalPoints, float transientTime) throws StructuralException, SimulationException
   :outertype: DecodableEnsemble

   Adds an Origin that corresponds to a decoding of the activities of Nodes in this Ensemble. The decoding is found by running the Ensemble repeatedly with different inputs, and using the steady-state output for each input to approximate a vector function of the input. Input is applied to a caller-defined Termination which may or may not be directly onto the Ensemble.

   :param name: Name of decoding
   :param functions: Functions of input that represent the meaning of Ensemble output when it runs in the Network provided (see environment arg)
   :param nodeOrigin: The name of the Node-level Origin to decode
   :param environment: A Network in which the Ensemble runs (may include inputs, feedback, etc)
   :param probe: A Probe that is connected to the named Node-level Origin
   :param termination: The Termination through which input is to be applied to the Ensemble
   :param evalPoints: The set of vector inputs that are to be applied at the above Termination
   :param transientTime: The amount of time the Network is to run with each input, so that transients die away (output is averaged over the last 10% of each simulation)
   :throws SimulationException: If there is a problem running the simulations
   :throws StructuralException: May arise in instantiating the Origin
   :return: An Origin that approximates the given Functions as a linear combination of output from the given nodeOrigin

doneOrigins
^^^^^^^^^^^

.. java:method:: public void doneOrigins()
   :outertype: DecodableEnsemble

   This method can optionally be called after all decoded Origins have been added, in order to free resources that are needed for adding new decodings.

removeDecodedOrigin
^^^^^^^^^^^^^^^^^^^

.. java:method:: public DecodedOrigin removeDecodedOrigin(String name) throws StructuralException
   :outertype: DecodableEnsemble

   :param name: Name of an existing decoding to remove
   :throws StructuralException: if DecodedOrigin doesn't exist
   :return: The removed DecodedOrigin

removeDecodedTermination
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public DecodedTermination removeDecodedTermination(String name) throws StructuralException
   :outertype: DecodableEnsemble

   :param name: Name of an existing termination to remove
   :throws StructuralException: if DecodedTermination doesn't exist
   :return: The removed DecodedTermination

