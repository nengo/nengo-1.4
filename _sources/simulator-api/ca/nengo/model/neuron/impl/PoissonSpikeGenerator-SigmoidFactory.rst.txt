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

PoissonSpikeGenerator.SigmoidFactory
====================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class SigmoidFactory implements SpikeGeneratorFactory
   :outertype: PoissonSpikeGenerator

   Creates sigmoid neurons (I guess rate-mode Poisson neurons?)

Constructors
------------
SigmoidFactory
^^^^^^^^^^^^^^

.. java:constructor:: public SigmoidFactory()
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   Set reasonable defaults

Methods
-------
getInflection
^^^^^^^^^^^^^

.. java:method:: public PDF getInflection()
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   :return: Distribution of inflection points of the sigmoid functions that describe current-firing rate relationships

getMaxRate
^^^^^^^^^^

.. java:method:: public PDF getMaxRate()
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   :return: Distribution of maximum firing rates

getSlope
^^^^^^^^

.. java:method:: public PDF getSlope()
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   :return: Distribution of slopes of the sigmoid functions that describe current-firing rate relationships before scaling to maxRate (slope at inflection point = slope*maxRate)

make
^^^^

.. java:method:: public SpikeGenerator make()
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   **See also:** :java:ref:`ca.nengo.model.neuron.impl.SpikeGeneratorFactory.make()`

setInflection
^^^^^^^^^^^^^

.. java:method:: public void setInflection(PDF inflection)
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   :param inflection: Distribution of inflection points of the sigmoid functions that describe current-firing rate relationships

setMaxRate
^^^^^^^^^^

.. java:method:: public void setMaxRate(PDF maxRate)
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   :param maxRate: Distribution of maximum firing rates

setSlope
^^^^^^^^

.. java:method:: public void setSlope(PDF slope)
   :outertype: PoissonSpikeGenerator.SigmoidFactory

   :param slope: Distribution of slopes of the sigmoid functions that describe current-firing rate relationships before scaling to maxRate (slope at inflection point = slope*maxRate)

