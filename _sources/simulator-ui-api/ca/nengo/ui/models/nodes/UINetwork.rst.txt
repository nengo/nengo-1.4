.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model.impl NetworkImpl

.. java:import:: ca.nengo.sim Simulator

.. java:import:: ca.nengo.ui.actions RunInteractivePlotsAction

.. java:import:: ca.nengo.ui.actions RunSimulatorAction

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.icons NetworkIcon

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: ca.nengo.ui.models.viewers NetworkViewer

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutable.Event

UINetwork
=========

.. java:package:: ca.nengo.ui.models.nodes
   :noindex:

.. java:type:: public class UINetwork extends UINodeViewable

   UI Wrapper for a Network

   :author: Shu Wu

Fields
------
typeName
^^^^^^^^

.. java:field:: public static final String typeName
   :outertype: UINetwork

Constructors
------------
UINetwork
^^^^^^^^^

.. java:constructor:: public UINetwork(Network model)
   :outertype: UINetwork

Methods
-------
attachViewToModel
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void attachViewToModel()
   :outertype: UINetwork

constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: UINetwork

constructSimulatorMenu
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void constructSimulatorMenu(PopupMenuBuilder menu, UINetwork network)
   :outertype: UINetwork

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UINetwork

createViewerInstance
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public NetworkViewer createViewerInstance()
   :outertype: UINetwork

detachViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void detachViewFromModel()
   :outertype: UINetwork

getClosestNetwork
^^^^^^^^^^^^^^^^^

.. java:method:: public static UINetwork getClosestNetwork(WorldObject wo)
   :outertype: UINetwork

   :param wo: WorldObject
   :return: The closest parent Network to wo

getDimensionality
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getDimensionality()
   :outertype: UINetwork

getModel
^^^^^^^^

.. java:method:: @Override public NetworkImpl getModel()
   :outertype: UINetwork

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: UINetwork

getNodesCount
^^^^^^^^^^^^^

.. java:method:: @Override public int getNodesCount()
   :outertype: UINetwork

getSimulator
^^^^^^^^^^^^

.. java:method:: public Simulator getSimulator()
   :outertype: UINetwork

   :return: Simulator

getTypeName
^^^^^^^^^^^

.. java:method:: @Override public String getTypeName()
   :outertype: UINetwork

getViewer
^^^^^^^^^

.. java:method:: @Override public NetworkViewer getViewer()
   :outertype: UINetwork

getViewerEnsured
^^^^^^^^^^^^^^^^

.. java:method:: public NodeViewer getViewerEnsured()
   :outertype: UINetwork

   :return: Gets the existing viewer, if it exists. Otherwise, open it.

initialize
^^^^^^^^^^

.. java:method:: @Override protected void initialize()
   :outertype: UINetwork

modelUpdated
^^^^^^^^^^^^

.. java:method:: @Override protected void modelUpdated()
   :outertype: UINetwork

saveContainerConfig
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void saveContainerConfig()
   :outertype: UINetwork

