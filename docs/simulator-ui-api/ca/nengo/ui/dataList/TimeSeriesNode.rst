.. java:import:: java.util Collection

.. java:import:: javax.swing.tree DefaultMutableTreeNode

.. java:import:: ca.nengo.ui.actions PlotSpikePattern

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

TimeSeriesNode
==============

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type:: abstract class TimeSeriesNode extends DataTreeNode

   Node containing time series data

   :author: Shu Wu

Fields
------
name
^^^^

.. java:field:: protected String name
   :outertype: TimeSeriesNode

Constructors
------------
TimeSeriesNode
^^^^^^^^^^^^^^

.. java:constructor:: public TimeSeriesNode(TimeSeries userObject, String name, boolean applyFilterByDefault)
   :outertype: TimeSeriesNode

Methods
-------
constructPopupMenu
^^^^^^^^^^^^^^^^^^

.. java:method:: public void constructPopupMenu(PopupMenuBuilder menu, SimulatorDataModel dataModel)
   :outertype: TimeSeriesNode

getDefaultAction
^^^^^^^^^^^^^^^^

.. java:method:: @Override public StandardAction getDefaultAction()
   :outertype: TimeSeriesNode

getUserObject
^^^^^^^^^^^^^

.. java:method:: @Override public TimeSeries getUserObject()
   :outertype: TimeSeriesNode

isApplyFilterByDefault
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isApplyFilterByDefault()
   :outertype: TimeSeriesNode

toString
^^^^^^^^

.. java:method:: @Override public abstract String toString()
   :outertype: TimeSeriesNode

