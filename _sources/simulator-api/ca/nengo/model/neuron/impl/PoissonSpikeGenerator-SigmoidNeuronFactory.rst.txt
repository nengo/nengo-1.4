.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math PDFTools

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl LinearFunction

.. java:import:: ca.nengo.math.impl PoissonPDF

.. java:import:: ca.nengo.math.impl SigmoidFunction

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl NodeFactory

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.model.neuron SynapticIntegrator

.. java:import:: ca.nengo.util MU

PoissonSpikeGenerator.SigmoidNeuronFactory
==========================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class SigmoidNeuronFactory implements NodeFactory
   :outertype: PoissonSpikeGenerator

   A factory for neurons with sigmoid response functions.

   :author: Bryan Tripp

Constructors
------------
SigmoidNeuronFactory
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SigmoidNeuronFactory(PDF slope, PDF inflection, PDF maxRate)
   :outertype: PoissonSpikeGenerator.SigmoidNeuronFactory

   Neurons from this factory will have Poisson firing rates that are sigmoidal functions of current. The constructor arguments parameterize the sigmoid function.

   :param slope: Distribution of slopes of the sigmoid functions that describe current-firing rate relationships, before scaling to maxRate (slope at inflection point = slope*maxRate)
   :param inflection: Distribution of inflection points of the sigmoid functions that describe current-firing rate relationships
   :param maxRate: Distribution of maximum firing rates

Methods
-------
getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: PoissonSpikeGenerator.SigmoidNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.getTypeDescription()`

make
^^^^

.. java:method:: public Neuron make(String name) throws StructuralException
   :outertype: PoissonSpikeGenerator.SigmoidNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.make(java.lang.String)`

