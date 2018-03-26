.. java:import:: java.awt.geom Point2D

.. java:import:: java.awt.geom Rectangle2D

.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.util ArrayList

.. java:import:: java.util Collection

.. java:import:: java.util HashSet

.. java:import:: java.util Iterator

.. java:import:: java.util LinkedList

.. java:import:: java.util Map.Entry

.. java:import:: java.util Properties

.. java:import:: java.util Vector

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.io FileManager

.. java:import:: ca.nengo.model Ensemble

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model.impl FunctionInput

.. java:import:: ca.nengo.model.nef NEFEnsemble

.. java:import:: ca.nengo.model.nef.impl DecodedOrigin

.. java:import:: ca.nengo.model.neuron Neuron

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.actions AddProbeAction

.. java:import:: ca.nengo.ui.actions CopyAction

.. java:import:: ca.nengo.ui.actions CreateModelAction

.. java:import:: ca.nengo.ui.actions CutAction

.. java:import:: ca.nengo.ui.actions DefaultModeAction

.. java:import:: ca.nengo.ui.actions DirectModeAction

.. java:import:: ca.nengo.ui.actions RateModeAction

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable UserDialogs

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions StandardAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.objects.activities TransientStatusMessage

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.lib.util Util

.. java:import:: ca.nengo.ui.lib.util.menus AbstractMenuBuilder

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

.. java:import:: ca.nengo.ui.lib.world DroppableX

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldImpl

.. java:import:: ca.nengo.ui.models NodeContainer.ContainerException

.. java:import:: ca.nengo.ui.models.nodes UIEnsemble

.. java:import:: ca.nengo.ui.models.nodes UIFunctionInput

.. java:import:: ca.nengo.ui.models.nodes UIGenericNode

.. java:import:: ca.nengo.ui.models.nodes UINEFEnsemble

.. java:import:: ca.nengo.ui.models.nodes UINetwork

.. java:import:: ca.nengo.ui.models.nodes UINeuron

.. java:import:: ca.nengo.ui.models.nodes.widgets UIOrigin

.. java:import:: ca.nengo.ui.models.nodes.widgets UIProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UIStateProbe

.. java:import:: ca.nengo.ui.models.nodes.widgets UITermination

.. java:import:: ca.nengo.ui.models.nodes.widgets Widget

.. java:import:: ca.nengo.ui.models.tooltips TooltipBuilder

.. java:import:: ca.nengo.ui.models.viewers NetworkViewer

.. java:import:: ca.nengo.ui.models.viewers NodeViewer

.. java:import:: ca.nengo.util Probe

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutable.Event

UINeoNode
=========

.. java:package:: ca.nengo.ui.models
   :noindex:

.. java:type:: public abstract class UINeoNode extends UINeoModel implements DroppableX

   UI Wrapper for a NEO Node Model

   :author: Shu

Constructors
------------
UINeoNode
^^^^^^^^^

.. java:constructor:: public UINeoNode(Node model)
   :outertype: UINeoNode

Methods
-------
addProbe
^^^^^^^^

.. java:method:: public UIStateProbe addProbe(String stateName) throws SimulationException
   :outertype: UINeoNode

   Creates a new probe and adds the UI object to the node

   :param stateName: The name of the state variable to probe

addWidget
^^^^^^^^^

.. java:method:: protected void addWidget(Widget widget)
   :outertype: UINeoNode

   :param widget: Widget to be added

attachViewToModel
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void attachViewToModel()
   :outertype: UINeoNode

constructDataCollectionMenu
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @SuppressWarnings protected void constructDataCollectionMenu(AbstractMenuBuilder menu)
   :outertype: UINeoNode

constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: UINeoNode

constructTooltips
^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void constructTooltips(TooltipBuilder tooltips)
   :outertype: UINeoNode

constructViewMenu
^^^^^^^^^^^^^^^^^

.. java:method:: protected void constructViewMenu(AbstractMenuBuilder menu)
   :outertype: UINeoNode

createNodeUI
^^^^^^^^^^^^

.. java:method:: public static UINeoNode createNodeUI(Node node)
   :outertype: UINeoNode

   Factory method which creates a Node UI object around a Node

   :param node: Node to be wrapped
   :return: Node UI Wrapper

detachViewFromModel
^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void detachViewFromModel()
   :outertype: UINeoNode

droppedOnTargets
^^^^^^^^^^^^^^^^

.. java:method:: public void droppedOnTargets(Collection<WorldObject> targets) throws UserCancelledException
   :outertype: UINeoNode

generateScript
^^^^^^^^^^^^^^

.. java:method:: public void generateScript(File file) throws IOException
   :outertype: UINeoNode

getFileName
^^^^^^^^^^^

.. java:method:: public String getFileName()
   :outertype: UINeoNode

   :return: The default file name for this node

getModel
^^^^^^^^

.. java:method:: @Override public Node getModel()
   :outertype: UINeoNode

getName
^^^^^^^

.. java:method:: @Override public String getName()
   :outertype: UINeoNode

getNetworkParent
^^^^^^^^^^^^^^^^

.. java:method:: public UINetwork getNetworkParent()
   :outertype: UINeoNode

   :return: The Network model the Node is attached to

getParentViewer
^^^^^^^^^^^^^^^

.. java:method:: public NodeViewer getParentViewer()
   :outertype: UINeoNode

   :return: The viewer the node is contained in, this may be a regular world or a specialized viewer such as a NetworkViewer or EnsembleViewer

getProbes
^^^^^^^^^

.. java:method:: public Vector<UIProbe> getProbes()
   :outertype: UINeoNode

getVisibleOrigins
^^^^^^^^^^^^^^^^^

.. java:method:: public Collection<UIOrigin> getVisibleOrigins()
   :outertype: UINeoNode

getVisibleTerminations
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Collection<UITermination> getVisibleTerminations()
   :outertype: UINeoNode

hideAllOandT
^^^^^^^^^^^^

.. java:method:: public void hideAllOandT()
   :outertype: UINeoNode

   Hides all origins and terminations

hideOrigin
^^^^^^^^^^

.. java:method:: public UIOrigin hideOrigin(String originName)
   :outertype: UINeoNode

   :param layoutName: Name of an Origin on the Node model
   :return: the POrigin hidden

initialize
^^^^^^^^^^

.. java:method:: @Override protected void initialize()
   :outertype: UINeoNode

layoutChildren
^^^^^^^^^^^^^^

.. java:method:: @Override public void layoutChildren()
   :outertype: UINeoNode

modelUpdated
^^^^^^^^^^^^

.. java:method:: @Override protected void modelUpdated()
   :outertype: UINeoNode

newProbeAdded
^^^^^^^^^^^^^

.. java:method:: protected void newProbeAdded(UIProbe probeUI)
   :outertype: UINeoNode

   Called when a new probe is added

   :param probeUI: New probe that was just added

prepareToDestroyModel
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected void prepareToDestroyModel()
   :outertype: UINeoNode

removeProbe
^^^^^^^^^^^

.. java:method:: public void removeProbe(UIProbe probe)
   :outertype: UINeoNode

   Removes a Probe UI object from node

   :param probe: to be removed

saveModel
^^^^^^^^^

.. java:method:: public void saveModel(File file) throws IOException
   :outertype: UINeoNode

   :param file: File to be saved in
   :throws IOException: if model cannot be saved to file

setName
^^^^^^^

.. java:method:: @Override public final void setName(String name)
   :outertype: UINeoNode

setWidgetsVisible
^^^^^^^^^^^^^^^^^

.. java:method:: public void setWidgetsVisible(boolean visible)
   :outertype: UINeoNode

   Sets the visibility of widgets

showAllDecodedOrigins
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void showAllDecodedOrigins()
   :outertype: UINeoNode

   Shows all the origins on the Node model

showAllOrigins
^^^^^^^^^^^^^^

.. java:method:: public void showAllOrigins()
   :outertype: UINeoNode

   Shows all the origins on the Node model

showAllTerminations
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void showAllTerminations()
   :outertype: UINeoNode

   Shows all the terminations on the Node model

showOrigin
^^^^^^^^^^

.. java:method:: public UIOrigin showOrigin(String originName)
   :outertype: UINeoNode

   :param layoutName: Name of an Origin on the Node model
   :return: the POrigin shown

showProbe
^^^^^^^^^

.. java:method:: public UIProbe showProbe(Probe probe)
   :outertype: UINeoNode

   Call this function if the probe already exists in the simulator and only needs to be shown

   :param probe: To be shown
   :return: Probe UI Object

showTermination
^^^^^^^^^^^^^^^

.. java:method:: public UITermination showTermination(String terminationName)
   :outertype: UINeoNode

   :param layoutName: Name of an Termination on the Node model

