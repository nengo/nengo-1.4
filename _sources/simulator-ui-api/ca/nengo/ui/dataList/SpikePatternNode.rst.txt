.. java:import:: java.util Collection

.. java:import:: javax.swing.tree DefaultMutableTreeNode

.. java:import:: ca.nengo.ui.actions PlotSpikePattern

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

SpikePatternNode
================

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type::  class SpikePatternNode extends DataTreeNode

   Node containing a spike pattern

   :author: Shu WU

Constructors
------------
SpikePatternNode
^^^^^^^^^^^^^^^^

.. java:constructor:: public SpikePatternNode(SpikePattern spikePattern)
   :outertype: SpikePatternNode

Methods
-------
constructPopupMenu
^^^^^^^^^^^^^^^^^^

.. java:method:: public void constructPopupMenu(PopupMenuBuilder menu, SimulatorDataModel dataModel)
   :outertype: SpikePatternNode

getDefaultAction
^^^^^^^^^^^^^^^^

.. java:method:: @Override public StandardAction getDefaultAction()
   :outertype: SpikePatternNode

getUserObject
^^^^^^^^^^^^^

.. java:method:: @Override public SpikePattern getUserObject()
   :outertype: SpikePatternNode

includeInExport
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean includeInExport()
   :outertype: SpikePatternNode

toString
^^^^^^^^

.. java:method:: public String toString()
   :outertype: SpikePatternNode

