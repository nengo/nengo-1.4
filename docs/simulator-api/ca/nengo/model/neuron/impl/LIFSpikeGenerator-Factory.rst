.. java:import:: java.util Properties

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl PreciseSpikeOutputImpl

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

LIFSpikeGenerator.Factory
=========================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class Factory implements SpikeGeneratorFactory
   :outertype: LIFSpikeGenerator

   Creates LIFSpikeGenerators.

   :author: Bryan Tripp

Constructors
------------
Factory
^^^^^^^

.. java:constructor:: public Factory()
   :outertype: LIFSpikeGenerator.Factory

   Set reasonable defaults

Methods
-------
getTauRC
^^^^^^^^

.. java:method:: public PDF getTauRC()
   :outertype: LIFSpikeGenerator.Factory

   :return: PDF of membrane time constants (s)

getTauRef
^^^^^^^^^

.. java:method:: public PDF getTauRef()
   :outertype: LIFSpikeGenerator.Factory

   :return: PDF of refractory periods (s)

make
^^^^

.. java:method:: public SpikeGenerator make()
   :outertype: LIFSpikeGenerator.Factory

   **See also:** :java:ref:`ca.nengo.model.neuron.impl.SpikeGeneratorFactory.make()`

setTauRC
^^^^^^^^

.. java:method:: public void setTauRC(PDF tauRC)
   :outertype: LIFSpikeGenerator.Factory

   :param tauRC: PDF of membrane time constants (s)

setTauRef
^^^^^^^^^

.. java:method:: public void setTauRef(PDF tauRef)
   :outertype: LIFSpikeGenerator.Factory

   :param tauRef: PDF of refractory periods (s)

