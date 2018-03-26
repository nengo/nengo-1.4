.. java:import:: java.util Collection

.. java:import:: javax.swing.tree DefaultMutableTreeNode

.. java:import:: ca.nengo.ui.actions PlotSpikePattern

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

ProbeDataNode
=============

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type::  class ProbeDataNode extends TimeSeriesNode

   Node containing probe data

   :author: Shu Wu

Constructors
------------
ProbeDataNode
^^^^^^^^^^^^^

.. java:constructor:: public ProbeDataNode(TimeSeries userObject, String stateName, boolean applyFilterByDefault)
   :outertype: ProbeDataNode

Methods
-------
includeInExport
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean includeInExport()
   :outertype: ProbeDataNode

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: ProbeDataNode

