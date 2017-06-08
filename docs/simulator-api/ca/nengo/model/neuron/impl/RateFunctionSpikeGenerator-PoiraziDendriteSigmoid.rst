.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math PDFTools

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util MU

RateFunctionSpikeGenerator.PoiraziDendriteSigmoid
=================================================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static class PoiraziDendriteSigmoid extends AbstractFunction
   :outertype: RateFunctionSpikeGenerator

   Function from Poirazi et al.,2003

Constructors
------------
PoiraziDendriteSigmoid
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PoiraziDendriteSigmoid()
   :outertype: RateFunctionSpikeGenerator.PoiraziDendriteSigmoid

   1D function

Methods
-------
map
^^^

.. java:method:: @Override public float map(float[] from)
   :outertype: RateFunctionSpikeGenerator.PoiraziDendriteSigmoid

