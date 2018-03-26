.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math FunctionInterpreter

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.math.impl DefaultFunctionInterpreter

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef NEFEnsembleFactory

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.sim Simulator

FuzzyLogicExample
=================

.. java:package:: ca.nengo.ui.dev
   :noindex:

.. java:type:: public class FuzzyLogicExample

   Fuzzification is implemented as a function transformation. Inference is done with multidimensional ensembles from which norms and conorms are decoded. Composition is done by projecting high-dimensional fuzzy consequents additively onto the same ensemble, from which the mode is selected by lateral inhibition.

   :author: Bryan Tripp

Methods
-------
createNetwork
^^^^^^^^^^^^^

.. java:method:: public static Network createNetwork() throws StructuralException
   :outertype: FuzzyLogicExample

