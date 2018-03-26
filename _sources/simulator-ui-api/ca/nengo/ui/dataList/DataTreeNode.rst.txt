.. java:import:: java.util Collection

.. java:import:: javax.swing.tree DefaultMutableTreeNode

.. java:import:: ca.nengo.ui.actions PlotSpikePattern

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.util DataUtils

.. java:import:: ca.nengo.util SpikePattern

.. java:import:: ca.nengo.util TimeSeries

DataTreeNode
============

.. java:package:: ca.nengo.ui.dataList
   :noindex:

.. java:type:: public abstract class DataTreeNode extends SortableMutableTreeNode

   Tree Node with NEO Data

   :author: Shu Wu

Constructors
------------
DataTreeNode
^^^^^^^^^^^^

.. java:constructor:: public DataTreeNode(Object userObject)
   :outertype: DataTreeNode

   :param userObject: TODO

Methods
-------
constructPopupMenu
^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract void constructPopupMenu(PopupMenuBuilder menu, SimulatorDataModel dataModel)
   :outertype: DataTreeNode

   :param menu: TODO
   :param dataModel: TODO

getDefaultAction
^^^^^^^^^^^^^^^^

.. java:method:: public abstract StandardAction getDefaultAction()
   :outertype: DataTreeNode

   :return: TODO

includeInExport
^^^^^^^^^^^^^^^

.. java:method:: public abstract boolean includeInExport()
   :outertype: DataTreeNode

   :return: TODO

toString
^^^^^^^^

.. java:method:: public abstract String toString()
   :outertype: DataTreeNode

