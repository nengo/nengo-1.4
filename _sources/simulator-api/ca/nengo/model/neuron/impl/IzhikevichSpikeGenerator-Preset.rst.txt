.. java:import:: java.util Properties

.. java:import:: ca.nengo.math.impl InterpolatedFunction

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron SpikeGenerator

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util TimeSeries1D

.. java:import:: ca.nengo.util.impl TimeSeries1DImpl

IzhikevichSpikeGenerator.Preset
===============================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public static enum Preset
   :outertype: IzhikevichSpikeGenerator

   Preset parameter values corresponding to different cell types.

   :author: Bryan Tripp

Enum Constants
--------------
CHATTERING
^^^^^^^^^^

.. java:field:: public static final IzhikevichSpikeGenerator.Preset CHATTERING
   :outertype: IzhikevichSpikeGenerator.Preset

   Parameter set for "chattering" behaviour

CUSTOM
^^^^^^

.. java:field:: public static final IzhikevichSpikeGenerator.Preset CUSTOM
   :outertype: IzhikevichSpikeGenerator.Preset

   Custom parameter set

DEFAULT
^^^^^^^

.. java:field:: public static final IzhikevichSpikeGenerator.Preset DEFAULT
   :outertype: IzhikevichSpikeGenerator.Preset

   Default parameter set

FAST_SPIKING
^^^^^^^^^^^^

.. java:field:: public static final IzhikevichSpikeGenerator.Preset FAST_SPIKING
   :outertype: IzhikevichSpikeGenerator.Preset

   Parameter set for fast spiking

INTRINSICALLY_BURSTING
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final IzhikevichSpikeGenerator.Preset INTRINSICALLY_BURSTING
   :outertype: IzhikevichSpikeGenerator.Preset

   Parameter set for burst firing

REGULAR_SPIKING
^^^^^^^^^^^^^^^

.. java:field:: public static final IzhikevichSpikeGenerator.Preset REGULAR_SPIKING
   :outertype: IzhikevichSpikeGenerator.Preset

   Parameter set for tonic firing

RESONATOR
^^^^^^^^^

.. java:field:: public static final IzhikevichSpikeGenerator.Preset RESONATOR
   :outertype: IzhikevichSpikeGenerator.Preset

   Parameter set for a resonator

