.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl RealOutputImpl

.. java:import:: ca.nengo.model.impl SpikeOutputImpl

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.model.neuron SpikeGenerator

SpikeGeneratorOrigin
====================

.. java:package:: ca.nengo.model.neuron.impl
   :noindex:

.. java:type:: public class SpikeGeneratorOrigin implements Origin

   An Origin that obtains output from an underlying SpikeGenerator. This is a good Origin to use as the main (axonal) output of a spiking neuron. This Origin may produce SpikeOutput or RealOutput depending on whether it is running in DEFAULT or CONSTANT_RATE SimulationMode.

   :author: Bryan Tripp

Constructors
------------
SpikeGeneratorOrigin
^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SpikeGeneratorOrigin(Node node, SpikeGenerator generator)
   :outertype: SpikeGeneratorOrigin

   :param node: The parent Node
   :param generator: The SpikeGenerator from which this Origin is to obtain output.

Methods
-------
clone
^^^^^

.. java:method:: @Override public SpikeGeneratorOrigin clone() throws CloneNotSupportedException
   :outertype: SpikeGeneratorOrigin

clone
^^^^^

.. java:method:: public SpikeGeneratorOrigin clone(Node node) throws CloneNotSupportedException
   :outertype: SpikeGeneratorOrigin

getDimensions
^^^^^^^^^^^^^

.. java:method:: public int getDimensions()
   :outertype: SpikeGeneratorOrigin

   :return: 1

   **See also:** :java:ref:`ca.nengo.model.Origin.getDimensions()`

getGenerator
^^^^^^^^^^^^

.. java:method:: public SpikeGenerator getGenerator()
   :outertype: SpikeGeneratorOrigin

   :return: Spike generator

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: SpikeGeneratorOrigin

   :return: Neuron.AXON

   **See also:** :java:ref:`ca.nengo.model.Origin.getName()`

getNode
^^^^^^^

.. java:method:: public Node getNode()
   :outertype: SpikeGeneratorOrigin

   **See also:** :java:ref:`ca.nengo.model.Origin.getNode()`

getRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public boolean getRequiredOnCPU()
   :outertype: SpikeGeneratorOrigin

getValues
^^^^^^^^^

.. java:method:: public InstantaneousOutput getValues()
   :outertype: SpikeGeneratorOrigin

   Returns spike values or real-valued spike rate values, depending on whether the mode is SimulationMode.DEFAULT or SimulationMode.CONSTANT_RATE.

   **See also:** :java:ref:`ca.nengo.model.Origin.getValues()`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: SpikeGeneratorOrigin

run
^^^

.. java:method:: public void run(float[] times, float[] current) throws SimulationException
   :outertype: SpikeGeneratorOrigin

   :param times: Passed on to the run() or runConstantRate() method of the wrapped SpikeGenerator depending on whether the SimulationMode is DEFAULT or CONSTANT_RATE (in the latter case only the first value is used).
   :param current: Passed on like the times argument.
   :throws SimulationException: Arising From the underlying SpikeGenerator, or if the given times or values arrays have length 0 when in CONSTANT_RATE mode (the latter because the first entries must be extracted).

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: SpikeGeneratorOrigin

   Need this to fix bug where the generator's mode is changed, but myOutput is still of the type of the old mode

   :param mode: Target simulation mode

   **See also:** :java:ref:`ca.nengo.model.neuron.Neuron.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name)
   :outertype: SpikeGeneratorOrigin

setRequiredOnCPU
^^^^^^^^^^^^^^^^

.. java:method:: public void setRequiredOnCPU(boolean val)
   :outertype: SpikeGeneratorOrigin

setValues
^^^^^^^^^

.. java:method:: public void setValues(InstantaneousOutput val)
   :outertype: SpikeGeneratorOrigin

