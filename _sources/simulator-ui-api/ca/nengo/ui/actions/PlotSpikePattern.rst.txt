.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.util SpikePattern

PlotSpikePattern
================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class PlotSpikePattern extends StandardAction

   Action for Plotting the Spike Pattern

   :author: Shu Wu

Fields
------
spikePattern
^^^^^^^^^^^^

.. java:field::  SpikePattern spikePattern
   :outertype: PlotSpikePattern

Constructors
------------
PlotSpikePattern
^^^^^^^^^^^^^^^^

.. java:constructor:: public PlotSpikePattern(SpikePattern spikePattern)
   :outertype: PlotSpikePattern

   :param spikePattern: TODO

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: PlotSpikePattern

