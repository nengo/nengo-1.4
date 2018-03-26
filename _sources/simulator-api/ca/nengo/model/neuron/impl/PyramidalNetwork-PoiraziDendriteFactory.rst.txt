.. java:import:: java.util Random

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl DefaultFunctionInterpreter

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl EnsembleImpl

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleFactoryImpl

.. java:import:: ca.nengo.model.nef.impl NEFEnsembleImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.model.neuron.impl RateFunctionSpikeGenerator.PoiraziDendriteSigmoidFactory

.. java:import:: ca.nengo.util MU

PyramidalNetwork.PoiraziDendriteFactory
=======================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class PoiraziDendriteFactory implements NodeFactory
   :outertype: PyramidalNetwork

   Creates neurons which are meant to model the dendrites of pyramidal cells Code is a modified version of NodeFactory written by Bryann Tripp

Constructors
------------
PoiraziDendriteFactory
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PoiraziDendriteFactory()
   :outertype: PyramidalNetwork.PoiraziDendriteFactory

   Default constructor Sets spikegenerator factory to a PoiraziDendriteSigmoidFactory

Methods
-------
changeRange
^^^^^^^^^^^

.. java:method:: public void changeRange(int rb)
   :outertype: PyramidalNetwork.PoiraziDendriteFactory

   Changes the range from which the random number generator r is allowed to choose from when scaling individual dendrites

   :param rb: new range to choose from

getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: PyramidalNetwork.PoiraziDendriteFactory

   Returns type of node

make
^^^^

.. java:method:: public Node make(String name) throws StructuralException
   :outertype: PyramidalNetwork.PoiraziDendriteFactory

   Makes a "Dendrite" Node

   :param name: Name of the node in ensemble

