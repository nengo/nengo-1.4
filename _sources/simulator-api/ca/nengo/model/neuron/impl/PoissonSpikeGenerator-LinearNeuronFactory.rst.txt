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

PoissonSpikeGenerator.LinearNeuronFactory
=========================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class LinearNeuronFactory implements NodeFactory
   :outertype: PoissonSpikeGenerator

   A factory for neurons with linear or rectified linear response functions.

   :author: bryan

Constructors
------------
LinearNeuronFactory
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public LinearNeuronFactory(PDF maxRate, PDF intercept, boolean rectified)
   :outertype: PoissonSpikeGenerator.LinearNeuronFactory

   :param maxRate: PDF for maximum spike rate
   :param intercept: PDF for x-intercept
   :param rectified: Rectify the curve?

Methods
-------
getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: PoissonSpikeGenerator.LinearNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.getTypeDescription()`

make
^^^^

.. java:method:: public Node make(String name) throws StructuralException
   :outertype: PoissonSpikeGenerator.LinearNeuronFactory

   **See also:** :java:ref:`ca.nengo.model.impl.NodeFactory.make(java.lang.String)`

