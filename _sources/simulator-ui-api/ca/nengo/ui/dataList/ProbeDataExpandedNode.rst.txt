.. java:import:: java.util Collection

.. java:import:: javax.swing.tree DefaultMutableTreeNode

.. java:import:: ca.nengo.ui.actions PlotSpikePattern

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

ProbeDataExpandedNode
=====================

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type::  class ProbeDataExpandedNode extends TimeSeriesNode

   Contains one-dimensional expanded data from a Probe

   :author: Shu Wu

Constructors
------------
ProbeDataExpandedNode
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ProbeDataExpandedNode(TimeSeries userObject, int dim, boolean applyFilterByDefault)
   :outertype: ProbeDataExpandedNode

Methods
-------
includeInExport
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean includeInExport()
   :outertype: ProbeDataExpandedNode

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: ProbeDataExpandedNode

