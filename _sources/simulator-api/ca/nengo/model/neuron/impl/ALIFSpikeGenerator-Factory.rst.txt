.. java:import:: java.util Properties

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDF

.. java:import:: ca.nengo.math RootFinder

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.math.impl IndicatorPDF

.. java:import:: ca.nengo.math.impl NewtonRootFinder

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

ALIFSpikeGenerator.Factory
==========================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class Factory implements SpikeGeneratorFactory
   :outertype: ALIFSpikeGenerator

   Creates ALIFSpikeGenerators.

   :author: Bryan Tripp

Constructors
------------
Factory
^^^^^^^

.. java:constructor:: public Factory()
   :outertype: ALIFSpikeGenerator.Factory

   Sets reasonable defaults

Methods
-------
getIncN
^^^^^^^

.. java:method:: public PDF getIncN()
   :outertype: ALIFSpikeGenerator.Factory

   :return: PDF of increments of the adaptation variable

getTauN
^^^^^^^

.. java:method:: public PDF getTauN()
   :outertype: ALIFSpikeGenerator.Factory

   :return: PDF of time constants of the adaptation variable (s)

getTauRC
^^^^^^^^

.. java:method:: public PDF getTauRC()
   :outertype: ALIFSpikeGenerator.Factory

   :return: PDF of membrane time constants (s)

getTauRef
^^^^^^^^^

.. java:method:: public PDF getTauRef()
   :outertype: ALIFSpikeGenerator.Factory

   :return: PDF of refractory periods (s)

make
^^^^

.. java:method:: public SpikeGenerator make()
   :outertype: ALIFSpikeGenerator.Factory

   **See also:** :java:ref:`ca.nengo.model.neuron.impl.SpikeGeneratorFactory.make()`

setIncN
^^^^^^^

.. java:method:: public void setIncN(PDF incN)
   :outertype: ALIFSpikeGenerator.Factory

   :param incN: PDF of increments of the adaptation variable

setTauN
^^^^^^^

.. java:method:: public void setTauN(PDF tauN)
   :outertype: ALIFSpikeGenerator.Factory

   :param tauN: PDF of time constants of the adaptation variable (s)

setTauRC
^^^^^^^^

.. java:method:: public void setTauRC(PDF tauRC)
   :outertype: ALIFSpikeGenerator.Factory

   :param tauRC: PDF of membrane time constants (s)

setTauRef
^^^^^^^^^

.. java:method:: public void setTauRef(PDF tauRef)
   :outertype: ALIFSpikeGenerator.Factory

   :param tauRef: PDF of refractory periods (s)

