.. java:import:: java.util Collection

.. java:import:: javax.swing.tree DefaultMutableTreeNode

.. java:import:: ca.nengo.ui.actions PlotSpikePattern

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

TimeSeriesNode.ExtractDimenmsions
=================================

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type::  class ExtractDimenmsions extends StandardAction
   :outertype: TimeSeriesNode

Constructors
------------
ExtractDimenmsions
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ExtractDimenmsions(SimulatorDataModel dataModel)
   :outertype: TimeSeriesNode.ExtractDimenmsions

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: TimeSeriesNode.ExtractDimenmsions

extractDimensions
^^^^^^^^^^^^^^^^^

.. java:method:: public void extractDimensions()
   :outertype: TimeSeriesNode.ExtractDimenmsions

